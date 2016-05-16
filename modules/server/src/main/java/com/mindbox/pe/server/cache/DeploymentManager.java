package com.mindbox.pe.server.cache;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;
import com.mindbox.pe.server.deploy.OverallDeployWork;
import com.mindbox.pe.server.generator.DefaultOutputController;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Deployment manager
 * @author MindBox
 */
public class DeploymentManager extends AbstractCacheManager {

	private class CleanUpWork implements Runnable {
		@Override
		public void run() {
			synchronized (generateStatMap) {
				final List<Integer> idsToDelete = new ArrayList<Integer>();
				for (final Map.Entry<Integer, GenerateStats> entry : generateStatMap.entrySet()) {
					if (!entry.getValue().isRunning() && entry.getValue().wasDoneForAtLeast(CLEANUP_THRESHOLD_MILLIS)) {
						idsToDelete.add(entry.getKey());
					}
				}

				for (final Integer deployId : idsToDelete) {
					generateStatMap.remove(deployId);
				}
			}
		}
	}

	private static class EnumValueMap {

		private EnumValue enumValueInstance;

		EnumValueMap(EnumValue enumValue) {
			enumValueInstance = enumValue;
		}
	}

	private static final ExecutorService DEPLOY_EXECUTOR = Executors.newSingleThreadExecutor();
	private static final long CLEANUP_THRESHOLD_MILLIS = 60 * 60 * 1000L; // one hour
	private static final int NOT_RUNNING_ID = -1;

	private static DeploymentManager singleton = null;

	public static synchronized void deinitialize() {
		if (singleton != null && singleton.cleanUpWorkFuture != null) {
			singleton.cleanUpWorkFuture.cancel(true);
		}
	}

	public static synchronized DeploymentManager getInstance() {
		if (singleton == null) {
			singleton = new DeploymentManager();
		}
		return singleton;
	}


	private Map<String, List<EnumValueMap>> enumValueMap;
	private Map<Integer, GenerateStats> generateStatMap;
	private final AtomicInteger currentRunId = new AtomicInteger(NOT_RUNNING_ID);
	private final AtomicReference<String> currentDeployDir = new AtomicReference<String>();
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private final ScheduledFuture<?> cleanUpWorkFuture;

	private DeploymentManager() {
		super();
		enumValueMap = new Hashtable<String, List<EnumValueMap>>();
		generateStatMap = new Hashtable<Integer, GenerateStats>();
		cleanUpWorkFuture = executorService.schedule(new CleanUpWork(), 1, TimeUnit.HOURS);
	}

	public void addEnumValueMap(String className, String attribName, EnumValues<EnumValue> enumValues) {
		addEnumValueMap(className, attribName, enumValues.toUnmodifiableList());
	}

	public void addEnumValueMap(String className, String attribName, List<EnumValue> enumsList) {
		String classAttrNameKey = className.toUpperCase() + "." + attribName.toUpperCase();
		List<EnumValueMap> list2 = enumValueMap.get(classAttrNameKey);
		if (list2 == null) {
			list2 = new java.util.ArrayList<EnumValueMap>();
			enumValueMap.put(classAttrNameKey, list2);
		}
		else {
			list2.clear();
		}
		for (int i = 0; i < enumsList.size(); i++) {
			list2.add(new EnumValueMap(enumsList.get(i)));
		}
	}

	public synchronized boolean deploy(final int deployID, final GuidelineReportFilter filter, final boolean exportPolicies, final String userID) throws RuleGenerationException {
		logInfo(logger, "Calling deploy with id=%d,filter=%s,exportPolicies=%b", deployID, filter, exportPolicies);

		if (!currentRunId.compareAndSet(NOT_RUNNING_ID, deployID)) {
			logInfo(logger, "Another deployment already in progress: id=[%d]", currentRunId.get());
			return false;
		}

		try {
			String baseStatus = filter.getThisStatusAndAbove();
			if (baseStatus == null) {
				if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
					throw new RuleGenerationException("thisStatusAndAbove is required");
				}
				baseStatus = Constants.DRAFT_STATUS;
				logInfo(logger, "Will deploy using %s status for deploy id of [%d]", Constants.DRAFT_STATUS, deployID);
			}

			final Date deployStartDate = new Date();

			AuditLogger.getInstance().logDeployStarted(String.format("Deployment process started by %s for %s stats", userID, baseStatus), userID);

			// Reset rule generator
			final OutputController outputController = new DefaultOutputController(baseStatus);
			currentDeployDir.set(outputController.getDeployDir());

			final GenerateStats generateStats = new GenerateStats(getCurrentDeployDir());

			final OverallDeployWork deployWork = new OverallDeployWork(filter, exportPolicies, outputController, deployID, userID, generateStats);

			// Sets the initial deploy stats
			generateStatMap.put(deployID, generateStats);

			filter.setServerFilterHelper(SearchCooridinator.getServerFilterHelper());

			// run policy generator thread
			final Future<GenerateStats> future = DEPLOY_EXECUTOR.submit(deployWork);

			logDebug(logger, "deployment process kicked off for %s", deployID);

			new DeployProcessChecker(deployStartDate, userID, future).start();

			return true;
		}
		catch (Exception e) {
			logError(logger, e, "Error in deploy: %d", deployID);
			return false;
		}
	}

	public String getCurrentDeployDir() {
		return currentDeployDir.get();
	}

	public int getCurrentRunId() {
		return currentRunId.get();
	}

	/**
	 * 
	 * @param runID runID
	 * @return the deploy error string
	 * @throws IOException on error
	 */
	public String getDeployErrorStr(int runID) throws IOException {
		File errorFile = null;

		final GenerateStats generatestats = monitor(runID);
		// TT 2067
		if (generatestats != null) {
			errorFile = new File(generatestats.getDeployDir(), DefaultOutputController.ERROR_FILE);
		}

		if (errorFile != null && errorFile.exists()) {
			logger.debug("getDeployErrorStr: " + runID + ", errorFile = " + errorFile);
			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			BufferedReader in = new BufferedReader(new FileReader(errorFile));
			try {
				for (String line = in.readLine(); line != null; line = in.readLine()) {
					out.println(line);
				}
			}
			finally {
				in.close();
			}

			return sw.toString();
		}
		else {
			return "";
		}
	}

	/**
	 * Equivalent to <code>getEnumDeployValue(className,attributeName,deployID,returnNullIfNotFound,false)</code>.
	 * @param className className
	 * @param attributeName attributeName
	 * @param deployID deployID
	 * @param returnNullIfNotFound returnNullIfNotFound
	 * @return the enumeration deploy value
	 */
	public String getEnumDeployValue(String className, String attributeName, String deployID, boolean returnNullIfNotFound) {
		return getEnumDeployValue(className, attributeName, deployID, returnNullIfNotFound, false);
	}

	/**
	 * Gets the deploy value for the specified display name for the specified attribute.
	 * If <code>checkIfDeployValue</code> is set to <code>true</code>,
	 * this checks if the specified <code>deployID</code> is a deploy value of an enum. 
	 * If so, this returns <code>deployID</code>. 
	 * Then, this checks to see if <code>deployID</code>
	 * is a deploy id of an enum. If so, it returns the deploy value of the enum.
	 * If not, this checks if <code>deployID</code> is a display label of an enum.
	 * If so, it returns the deploy value of the enum.
	 * If not, this returns <code>null</code>, if <code>returnNullIfNotFound</code>
	 * is set to <code>true</code>; otherwse, this returns <code>deployID</code>.
	 * @param className className
	 * @param attributeName attributeName
	 * @param deployID deployID
	 * @param returnNullIfNotFound if set to <code>true</code>, this returns <code>null</code> 
	 *                               if <code>displayString</code> is not found or attribute has no enum values;
	 *                              this returns the displayString, otherwise
	 * @param checkIfDeployValue if <code>true</code>, this checks deploy values in addition to deploy ids
	 * @return the deloy value, if found; <code>null</code>, otherwise
	 */
	public String getEnumDeployValue(String className, String attributeName, String deployID, boolean returnNullIfNotFound, boolean checkIfDeployValue) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			// check deploy ids first
			try {
				Integer id = Integer.valueOf(deployID);
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumvaluemap.enumValueInstance.getDeployID().equals(id)) {
						return enumvaluemap.enumValueInstance.getDeployValue(); //mDeployValue;
					}
				}
			}
			catch (Exception ex) {
			}

			// check display values
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumvaluemap.enumValueInstance.getDisplayLabel().equals(deployID)) {
					return enumvaluemap.enumValueInstance.getDeployValue();
				}
			}

			// Fix to TT 1835: check deploy value last (after deployID and display label)
			// check if it's a deploy value
			if (checkIfDeployValue) {
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumvaluemap.enumValueInstance.getDeployValue().equals(deployID)) {
						return deployID;
					}
				}
			}
			return null;
		}
		return (returnNullIfNotFound ? null : deployID);
	}

	public List<String> getEnumDeployValues(String s, String s1) {
		List<String> list = null;
		String s2 = s.toUpperCase() + "." + s1.toUpperCase();
		if (enumValueMap != null) {
			List<EnumValueMap> list1 = null;
			if (enumValueMap.containsKey(s2) == true) list1 = enumValueMap.get(s2);
			if (list1 != null) {
				list = new java.util.ArrayList<String>();
				for (int i = 0; i < list1.size(); i++) {
					EnumValueMap enumvaluemap = list1.get(i);
					list.add(enumvaluemap.enumValueInstance.getDisplayLabel());
				}

			}
		}
		return list;
	}

	/**
	 * Gets a list of the deploy value of enumeration values that are in the specified <code>enumValues</code>.
	 * If list is <code>null</code>, this returns <code>null</code>
	 * @param className className
	 * @param attributeName attributeName
	 * @param enumValues enumValues
	 * @return a list of the deploy value of an enumeration value e such that e is in <code>enumValues</code>, 
	 */
	public String[] getEnumDeployValues(String className, String attributeName, EnumValues<?> enumValues) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			List<String> deployValueList = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumValues.containsDeployID_ForEnumValueOnly(enumvaluemap.enumValueInstance.getDeployID())) {
					deployValueList.add(enumvaluemap.enumValueInstance.getDeployValue());
				}
			}
			return deployValueList.toArray(new String[0]);
		}
		else {
			return null;
		}
	}

	public String getEnumDisplayValue(String className, String attributeName, String deployID, boolean returnNullIfNotFound) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			// check deploy ids first
			try {
				Integer id = Integer.valueOf(deployID);
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumvaluemap.enumValueInstance.getDeployID().equals(id)) {
						return enumvaluemap.enumValueInstance.getDisplayLabel(); //mDeployValue;
					}
				}
			}
			catch (Exception ex) {
			}

			// check display values
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null && enumvaluemap.enumValueInstance.getDisplayLabel().equals(deployID)) {
					return enumvaluemap.enumValueInstance.getDisplayLabel();
				}
			}
			return null;
		}
		return (returnNullIfNotFound ? null : deployID);
	}

	private List<EnumValueMap> getEnumValueList(String className, String attributeName) {
		if (className != null && attributeName != null) {
			String referenceStr = className.toUpperCase() + "." + attributeName.toUpperCase();
			if (enumValueMap != null) {
				if (enumValueMap.containsKey(referenceStr)) {
					return enumValueMap.get(referenceStr);
				}
			}
		}
		return null;
	}

	public GenerateStats monitor(final int deployId) {
		synchronized (generateStatMap) {
			return generateStatMap.get(deployId);
		}
	}

	synchronized void resetCurrentDeployId() {
		currentRunId.set(NOT_RUNNING_ID);
	}

	public boolean validateDeployDefinition() {
		return true;
	}

}
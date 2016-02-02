package com.mindbox.pe.server.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.ExternalProcessUtil;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.CBRGenerator;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.ProcessGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGenerator;
import com.mindbox.pe.server.imexport.ExportException;

/**
 * Deployment manager
 * @author MindBox
 */
public class DeploymentManager extends AbstractCacheManager {

	private static class EnumValueMap {

		private EnumValue enumValueInstance;

		EnumValueMap(EnumValue enumValue) {
			enumValueInstance = enumValue;
		}
	}

	public static synchronized DeploymentManager getInstance() {
		if (singleton == null) singleton = new DeploymentManager();
		return singleton;
	}

	private static DeploymentManager singleton = null;


	private Map<String, List<EnumValueMap>> enumValueMap;
	private Map<Integer, List<GenerateStats>> generateStatMap;
	private int currentRunID;
	private RuleGenerator ruleGenerator;
	private ProcessGenerator processGenerator;
	private CBRGenerator cbrGenerator;
	private String deployDir;
	private ExternalProcessUtil externalProcessUtil;

	private DeploymentManager() {
		super();
		enumValueMap = new Hashtable<String, List<EnumValueMap>>();
		generateStatMap = new Hashtable<Integer, List<GenerateStats>>();
		currentRunID = -1;
		ruleGenerator = null;
		externalProcessUtil = new ExternalProcessUtil(logger);
	}

	private RuleGenerator initRuleGenerator(OutputController outputController) throws RuleGenerationException {
		if (ruleGenerator == null) {
			ruleGenerator = new RuleGenerator(outputController);
		}
		else {
			ruleGenerator.init(outputController);
		}
		return ruleGenerator;
	}

	private CBRGenerator initCBRGenerator(OutputController outputController) throws RuleGenerationException {
		if (cbrGenerator == null) {
			cbrGenerator = CBRGenerator.getInstance();
		}
		cbrGenerator.init(outputController);
		return cbrGenerator;
	}

	private ProcessGenerator initProcessGenerator(OutputController outputController) throws RuleGenerationException {
		if (processGenerator == null) {
			processGenerator = ProcessGenerator.getInstance();
		}
		processGenerator.init(outputController);
		return processGenerator;
	}

	private List<GenerateStats> collectGenerateStats() {
		List<GenerateStats> statList = new ArrayList<GenerateStats>();
		if (ruleGenerator != null) {
			statList.addAll(ruleGenerator.collectGenerateStats());
		}
		if (cbrGenerator != null) {
			statList.add(cbrGenerator.getStats());
		}
		if (processGenerator != null) {
			statList.add(processGenerator.getStats());
		}
		return statList;
	}

	public synchronized boolean deploy(final int deployID, final GuidelineReportFilter filter, final boolean exportPolicies,
			final String userID) throws RuleGenerationException {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Calling deploy with id=%d,filter=%s,exportPolicies=%b", deployID, filter, exportPolicies));
		}
		if (isCurrentlyRunning()) return false;

		String baseStatus = filter.getThisStatusAndAbove();
		if (baseStatus == null) {
			if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
				throw new RuleGenerationException("thisStatusAndAbove is required");
			}
			baseStatus = Constants.DRAFT_STATUS;
			if (logger.isInfoEnabled()) {
				logger.info("Will deploy using " + Constants.DRAFT_STATUS + " status for deploy id of " + deployID);
			}
		}

		// Reset rule generator
		final OutputController outputController = new OutputController(baseStatus);
		currentRunID = deployID;
		List<GenerateStats> generatestats = collectGenerateStats();
		synchronized (generateStatMap) {
			generateStatMap.put(new Integer(deployID), generatestats);
		}

		// run policy generator thread
		Thread ruleThread = null;
		try {
			ruleThread = new Thread() {
				public void run() {
					logger.debug(">>> Generator.run");
					filter.setServerFilterHelper(SearchCooridinator.getServerFilterHelper());

					int uncaughtErrorCount = 0;
					try {
						// [1] Generate guideline rules
						try {
							logger.info("Generator.run: deploying guidelines...");
							initRuleGenerator(outputController).generate(filter);
						}
						catch (Exception ex) {
							++uncaughtErrorCount;
							logger.error("Error in guideline deployment", ex);
							try {
								outputController.writeErrorMessage("Guideline Deployment", ex.getMessage());
							}
							catch (RuleGenerationException rge) {
								logger.warn("Failed to report error: " + ex.getMessage(), rge);
							}
						}
						// [2] Generate process/phase objects
						try {
							if (filter.isIncludeProcessData()) {
								logger.info("Generator.run: deploying process data...");
								initProcessGenerator(outputController).generateProcessData();
								processGenerator.writeAll();
							}
							else if (processGenerator != null) {
								processGenerator.setPercentageComplete(100);
							}
						}
						catch (Exception ex) {
							++uncaughtErrorCount;
							logger.error("Error in process data deployment", ex);
							try {
								outputController.writeErrorMessage("Process Data Deployment", ex.getMessage());
							}
							catch (RuleGenerationException rge) {
								logger.warn("Failed to report error: " + ex.getMessage(), rge);
							}
						}
						// [3] Generate CBR
						try {
							if (filter.isIncludeCBR()) {
								logger.info("Generator.run: deploy cbr data...");
								initCBRGenerator(outputController).generateCBRData();
								cbrGenerator.writeAll();
							}
							else if (cbrGenerator != null) {
								cbrGenerator.setPercentageComplete(100);
							}
						}
						catch (Exception ex) {
							++uncaughtErrorCount;
							logger.error("Error in CBR deployment", ex);
							try {
								outputController.writeErrorMessage("CBR Deployment", ex.getMessage());
							}
							catch (RuleGenerationException rge) {
								logger.warn("Failed to report error: " + ex.getMessage(), rge);
							}
						}
						// Generate entities export if indicated
						if (filter.isIncludeEntities() && deployID > 0) {
							try {
								processExport(outputController.getDeployDir(), exportPolicies, userID);
							}
							catch (Exception ex) {
								++uncaughtErrorCount;
								logger.error("Error in generating entities export", ex);
								try {
									outputController.writeErrorMessage("Entity Export", ex.getMessage());
								}
								catch (RuleGenerationException rge) {
									logger.warn("Failed to report error: " + ex.getMessage(), rge);
								}
							}
						}
						// Run post-deploy script, if configured to do so
						try {
							logger.info("Generator.run: executing post deploy script...");
							String scriptFile = ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig().getPostDeployScriptFile();
							logger.info("Generator.run: post deploy script file = " + scriptFile);

							if (scriptFile != null && scriptFile.length() > 0) {
								Map<String, String> environmentVarMap = new HashMap<String, String>();
								environmentVarMap.put("KB_STATUS", outputController.getStatus());
								environmentVarMap.put("DEPLOY_DIR", outputController.getDeployDir());

								externalProcessUtil.executeProcess(
										environmentVarMap,
										scriptFile,
										outputController.getStatus(),
										outputController.getDeployDir());

								logger.info("Generator.run: started post deploy script: " + scriptFile);
							}
						}
						catch (Exception ex) {
							++uncaughtErrorCount;
							logger.error("Error in execustion of post deploy script", ex);
							try {
								outputController.writeErrorMessage("Post Deployment Script", ex.getMessage());
							}
							catch (RuleGenerationException rge) {
								logger.warn("Failed to report error: " + ex.getMessage(), rge);
							}
						}
						logger.debug("Generator.run: end of run");
					}
					finally {
						// TT 2063 -- do this first						
						outputController.closeErrorWriters();

						List<GenerateStats> statsList = collectGenerateStats();
						for (GenerateStats stats : statsList) {
							stats.setRunning(false);
						}
						if (uncaughtErrorCount > 0 && !statsList.isEmpty()) {
							statsList.get(0).setNumErrorsGenerated(uncaughtErrorCount + statsList.get(0).getNumErrorsGenerated());
						}
						synchronized (generateStatMap) {
							generateStatMap.put(deployID, statsList);
						}
						currentRunID = 0;
					}
				}
			};
			ruleThread.start();

			return true;
		}
		catch (Exception exception) {
			logger.error("Error starting rule-gen thread", exception);
			logger.error("-  ruleThread=" + ruleThread);
			return false;
		}
	}

	/**
	 * @since PowerEditor 5.0.0
	 * @param deployData
	 * @param deployDir : directory where other deploy files were written successfully to.
	 * @throws ExportException 
	 * @throws IOException 
	 */
	private void processExport(String deployDir, boolean exportPolicies, String userID) throws IOException, ExportException {
		if (deployDir != null) {
			// Export Entities
			GuidelineReportFilter entitiesFilter = new GuidelineReportFilter();
			entitiesFilter.setIncludeEntities(true);
			entitiesFilter.setIncludeDateSynonyms(true);
			String etitiesFileName = deployDir + File.separator + Constants.ENTITIES_EXPORT_FILE_NAME;
			BizActionCoordinator.getInstance().writeExportXML(entitiesFilter, etitiesFileName, userID);

			if (exportPolicies) {
				GuidelineReportFilter policiesFilter = new GuidelineReportFilter();
				// We need date synonym as grid elements refer to them
				policiesFilter.setIncludeDateSynonyms(true);
				policiesFilter.setIncludeEntities(true);
				policiesFilter.setIncludeTemplates(true);
				policiesFilter.setIncludeGuidelines(true);
				policiesFilter.setIncludeParameters(true);
				policiesFilter.setIncludeSecurityData(true);
				String policiesFilename = deployDir + File.separator + Constants.POLICIES_EXPORT_FILE_NAME;
				BizActionCoordinator.getInstance().writeExportXML(policiesFilter, policiesFilename, userID);
			}

		}
	}

	public boolean validateDeployDefinition() {
		return true;
	}

	public void startLoading() {
	}

	public synchronized int getCurrentRunId() {
		return currentRunID;
	}

	/**
	 * @since PowerEditor 5.0.0
	 * @return deploy directory to which files were written
	 */
	public synchronized String getDeployDir() {
		return deployDir;
	}

	/**
	 * Gets a list of the deploy value of enumeration values that are in the specified <code>enumValues</code>.
	 * If list is <code>null</code>, this returns <code>null</code>
	 * @param className
	 * @param attributeName
	 * @param enumValues
	 * @return a list of the deploy value of an enumeration value e such that e is in <code>enumValues</code>, 
	 */
	public String[] getEnumDeployValues(String className, String attributeName, EnumValues<?> enumValues) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			List<String> deployValueList = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
						&& enumValues.containsDeployID_ForEnumValueOnly(enumvaluemap.enumValueInstance.getDeployID())) {
					deployValueList.add(enumvaluemap.enumValueInstance.getDeployValue());
				}
			}
			return deployValueList.toArray(new String[0]);
		}
		else {
			return null;
		}
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

	/**
	 * Equivalent to <code>getEnumDeployValue(className,attributeName,deployID,returnNullIfNotFound,false)</code>.
	 * @param className
	 * @param attributeName
	 * @param deployID
	 * @param returnNullIfNotFound
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
	 * @param className
	 * @param attributeName
	 * @param deployID
	 * @param returnNullIfNotFound if set to <code>true</code>, this returns <code>null</code> 
	 *                               if <code>displayString</code> is not found or attribute has no enum values;
	 *                              this returns the displayString, otherwise
	 * @param checkIfDeployValue if <code>true</code>, this checks deploy values in addition to deploy ids
	 * @return the deloy value, if found; <code>null</code>, otherwise
	 */
	public String getEnumDeployValue(String className, String attributeName, String deployID, boolean returnNullIfNotFound,
			boolean checkIfDeployValue) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			// check deploy ids first
			try {
				Integer id = Integer.valueOf(deployID);
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
							&& enumvaluemap.enumValueInstance.getDeployID().equals(id)) {
						return enumvaluemap.enumValueInstance.getDeployValue(); //mDeployValue;
					}
				}
			}
			catch (Exception ex) {
			}

			// check display values
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
						&& enumvaluemap.enumValueInstance.getDisplayLabel().equals(deployID)) {
					return enumvaluemap.enumValueInstance.getDeployValue();
				}
			}

			// Fix to TT 1835: check deploy value last (after deployID and display label)
			// check if it's a deploy value
			if (checkIfDeployValue) {
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
							&& enumvaluemap.enumValueInstance.getDeployValue().equals(deployID)) {
						return deployID;
					}
				}
			}
			return null;
		}
		return (returnNullIfNotFound ? null : deployID);
	}

	public String getEnumDisplayValue(String className, String attributeName, String deployID, boolean returnNullIfNotFound) {
		List<EnumValueMap> list = getEnumValueList(className, attributeName);
		if (list != null) {
			// check deploy ids first
			try {
				Integer id = Integer.valueOf(deployID);
				for (int i = 0; i < list.size(); i++) {
					EnumValueMap enumvaluemap = list.get(i);
					if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
							&& enumvaluemap.enumValueInstance.getDeployID().equals(id)) {
						return enumvaluemap.enumValueInstance.getDisplayLabel(); //mDeployValue;
					}
				}
			}
			catch (Exception ex) {
			}

			// check display values
			for (int i = 0; i < list.size(); i++) {
				EnumValueMap enumvaluemap = list.get(i);
				if (enumvaluemap != null && enumvaluemap.enumValueInstance != null
						&& enumvaluemap.enumValueInstance.getDisplayLabel().equals(deployID)) {
					return enumvaluemap.enumValueInstance.getDisplayLabel();
				}
			}
			return null;
		}
		return (returnNullIfNotFound ? null : deployID);
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
		for (int i = 0; i < enumsList.size(); i++)
			list2.add(new EnumValueMap(enumsList.get(i)));
	}

	public void finishLoading() {
	}

	public List<GenerateStats> monitor(int id) {
		logger.info(">>> monitor: " + id);
		if (currentRunID == id) {
			List<GenerateStats> stats = collectGenerateStats();
			logger.info("<<< monitor: " + stats);
			return stats;
		}
		else {
			return generateStatMap.get(new Integer(id));
		}
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

	private boolean isCurrentlyRunning() {
		synchronized (generateStatMap) {
			List<GenerateStats> generatestats = generateStatMap.get(new Integer(currentRunID));
			return generatestats != null && generatestats.get(0).isRunning();
		}
	}

	/**
	 * 
	 * @param runID
	 * @return the deploy error string
	 * @throws IOException
	 */
	public String getDeployErrorStr(int runID) throws IOException {
		List<GenerateStats> generatestats;
		File errorFile = null;

		synchronized (generateStatMap) {
			generatestats = generateStatMap.get(new Integer(runID));
		}
		// TT 2067
		if (generatestats != null) {
			errorFile = new File(generatestats.get(0).getDeployDir(), OutputController.ERROR_FILE);
		}

		if (errorFile != null && errorFile.exists()) {
			logger.debug("getDeployErrorStr: " + runID + ", errorFile = " + errorFile);
			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			BufferedReader in = new BufferedReader(new FileReader(errorFile));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				out.println(line);
			}
			in.close();

			return sw.toString();
		}
		else {
			return "";
		}
	}

}
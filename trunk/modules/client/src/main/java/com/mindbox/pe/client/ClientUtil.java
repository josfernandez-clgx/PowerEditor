package com.mindbox.pe.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;

import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.EnumerationSourceNotConfiguredException;
import com.mindbox.pe.common.GuidelineActionProvider;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.common.timeout.TimeOutController;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseClass;
import com.mindbox.pe.model.cbr.CBRScoringFunction;
import com.mindbox.pe.model.cbr.CBRValueRange;
import com.mindbox.pe.model.comparator.DateSynonymComparator;
import com.mindbox.pe.model.comparator.IDNameObjectComparator;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.AllDateSynonymFilter;
import com.mindbox.pe.model.filter.AllNamedDateSynonymFilter;
import com.mindbox.pe.model.filter.AllSearchFilter;
import com.mindbox.pe.model.filter.GuidelineActionSearchFilter;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.table.EnumerationSourceProxy;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityTab;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.config.GuidelineTab;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.UserInterfaceConfig;

/**
 * Provides utility methods for client.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public final class ClientUtil extends UtilBase implements GuidelineActionProvider {

	private static class EnumSourceProxyImpl implements EnumerationSourceProxy {

		@Override
		public List<EnumValue> getAllEnumValues(String sourceName) {
			try {
				if (getCommunicator().getEnumerationSourceDetails().isEmpty()) {
					throw new EnumerationSourceNotConfiguredException();
				}
				return getCommunicator().getAllEnumValuesFromEnumerationSource(sourceName);
			}
			catch (ServerException e) {
				handleRuntimeException(e);
				return new ArrayList<EnumValue>();
			}
		}

		@Override
		public List<EnumValue> getApplicableEnumValues(String sourceName, String selectorValue) {
			try {
				if (getCommunicator().getEnumerationSourceDetails().isEmpty()) {
					throw new EnumerationSourceNotConfiguredException();
				}
				return getCommunicator().getApplicableEnumValuesFromEnumerationSource(sourceName, selectorValue);
			}
			catch (ServerException e) {
				handleRuntimeException(e);
				return new ArrayList<EnumValue>();
			}
		}

	}

	private static ConfigHelper configHelper = null;
	public static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");


	/**
	 * Date formatter. Format is "yyyy-MM-dd HH:mm:ss".
	 */
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String RESOURCE_NAME_LABELS = "LabelsBundle";

	private static final String RESOURCE_NAME_MESSAGES = "MessagesBundle";

	private static final String RESOURCE_NAME_GIFS = "GIFsBundle";
	private static final String RESOURCE_NAME_VALUES = "ValuesBundle";
	private static Logger logger = null;

	private static final SimpleDateFormat reportNameFormat = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");
	private static ClientUtil instance = null;

	private static Clipboard clipBoard = null;

	public static boolean checkDeployPermission(String status) {
		return checkPermissionByPrivilegeName(PrivilegeConstants.DEPLOY_PRIV_NAME_PREFIX + status);
	}

	/**
	 * Returns true if Edit privilege exists on an EntityType
	 * @param entityTypeDef
	 * @return boolean
	 * @since 5.0.0
	 */
	public static boolean checkEditEntityPermission(EntityType entityTypeDef) {
		String editPrivilegeOnCurrentEntity = PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + entityTypeDef.getName();
		if (checkPermissionByPrivilegeName(editPrivilegeOnCurrentEntity)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Extracts UsageType from GridTemplate and returns true if Edit Template privilege
	 * exists for that UsageType's privilege
	 * @param template
	 * @return boolean
	 * @since 5.0.0 
	 */
	public static boolean checkEditTemplatePermission(GridTemplate template) {
		return ClientUtil.checkEditTemplatePermission(template.getUsageType());
	}

	/**
	 * returns true if Edit Template privilege exists for this UsageType's privilege
	 * @param usageType
	 * @return boolean
	 * @since 5.0.0 
	 */
	public static boolean checkEditTemplatePermission(TemplateUsageType usageType) {
		String editTemplatesPrivilege_Name = UtilBase.constructEditTemplatesPrivilege_Name(usageType.getPrivilege());
		return ClientUtil.checkPermissionByPrivilegeName(editTemplatesPrivilege_Name);
	}

	/**
	 * Calls {@link com.mindbox.pe.client.MainApplication#checkPermissionByPrivilegeName(String)}
	 * Given the privilege name, this returns true if that permission exists
	 * for the role the current user has logged in as
	 * @param str
	 * @return boolean
	 * @since 5.0.0 (replaces checkPermission(String str))
	 */
	public static boolean checkPermissionByPrivilegeName(String str) {
		return instance.parent.checkPermissionByPrivilegeName(str);
	}

	/**
	 * Returns true if user has either View or Edit permission on any EntityType
	 * @return boolean
	 * @since 5.0.0
	 */
	public static boolean checkViewOrEditAnyEntityPermission() {
		Set<Privilege> privSet = ClientUtil.getUserSession().getPrivileges();
		for (Iterator<Privilege> iter1 = privSet.iterator(); iter1.hasNext();) {
			Privilege priv = iter1.next();
			if (priv.getPrivilegeType() == PrivilegeConstants.ENTITY_TYPE_PRIV) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the user has either view or edit Guideline privilege on any
	 * of the usage type in the set of privileges that the user has.
	 * @return boolean
	 * @since 5.0.0
	 */
	public static boolean checkViewOrEditAnyGuidelinePermission() {
		Set<Privilege> privSet = ClientUtil.getUserSession().getPrivileges();
		for (Iterator<Privilege> iter1 = privSet.iterator(); iter1.hasNext();) {
			Privilege priv = iter1.next();
			if (priv.getPrivilegeType() == PrivilegeConstants.USAGE_TYPE_PRIV && priv.getDisplayString().indexOf(PrivilegeConstants.VIEW_AND_EDIT_USAGE_GUIDELINE_PRIV_DISPLAY_NAME_SUFFIX) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the user has either view or edit template privilege on any
	 * of the usage type in the set of privileges that the user has.-breakiterator
	 * This is subsituting 'ManageTemplates' permission from 4.5.x which used to be a hard coded 
	 * privilege in MB_Privilege table.
	 * @return boolean
	 * @since 5.0.0
	 */
	public static boolean checkViewOrEditAnyTemplatePermission() {
		Set<Privilege> privSet = ClientUtil.getUserSession().getPrivileges();
		for (Iterator<Privilege> iter1 = privSet.iterator(); iter1.hasNext();) {
			Privilege priv = iter1.next();
			if (priv.getPrivilegeType() == PrivilegeConstants.USAGE_TYPE_PRIV && priv.getName().indexOf(PrivilegeConstants.VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_NAME_SUFFIX) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if either View or Edit privilege on an EntityType exists
	 * @param entityTypeDef
	 * @return boolean
	 * @since 5.0.0
	 */
	public static boolean checkViewOrEditEntityPermission(EntityType entityTypeDef) {
		String viewPrivilegeOnCurrentEntity = PrivilegeConstants.VIEW_PRIV_NAME_PREFIX + entityTypeDef.getName();
		String editPrivilegeOnCurrentEntity = PrivilegeConstants.EDIT_PRIV_NAME_PREFIX + entityTypeDef.getName();
		if (checkPermissionByPrivilegeName(viewPrivilegeOnCurrentEntity) || checkPermissionByPrivilegeName(editPrivilegeOnCurrentEntity)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Calls {@link com.mindbox.pe.client.MainApplication#checkViewOrEditGuidelinePermission(com.mindbox.pe.common.config.GuidelineTab)}
	 * @param gtConfig
	 * @return boolean
	 */
	public static boolean checkViewOrEditGuidelinePermission(GuidelineTab gtConfig) {
		return instance.parent.checkViewOrEditGuidelinePermission(gtConfig);

	}

	/**
	 * Calls {@link com.mindbox.pe.client.MainApplication#checkViewOrEditGuidelinePermissionOnUsageType(com.mindbox.pe.model.TemplateUsageType)}
	 * @param usageType
	 * @return boolean
	 * @since 5.0.0 
	 */
	public static boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType) {
		return instance.parent.checkViewOrEditGuidelinePermissionOnUsageType(usageType);
	}

	/**
	 * Calls {@link com.mindbox.pe.client.MainApplication#checkViewOrEditTemplatePermission(com.mindbox.pe.common.config.GuidelineTab)}
	 * @param gtConfig
	 * @return boolean
	 */
	public static boolean checkViewOrEditTemplatePermission(GuidelineTab gtConfig) {
		return instance.parent.checkViewOrEditTemplatePermission(gtConfig);
	}

	/**
	 * Calls {@link com.mindbox.pe.client.MainApplication#checkViewOrEditTemplatePermissionOnUsageType(com.mindbox.pe.model.TemplateUsageType)}
	 * @param usageType
	 * @return boolean
	 * @since 5.0.0 
	 */
	public static boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType) {
		return instance.parent.checkViewOrEditTemplatePermissionOnUsageType(usageType);
	}

	/**
	 * Clears cached items in this class.
	 *
	 */
	public static void clearCache() {
		if (instance != null) {
			synchronized (instance) {
				instance.parent = null;
			}
		}
	}


	public static void executeAsScript(String urlStr) throws IOException {
		Runtime.getRuntime().exec(new String[] { "explorer", '"' + urlStr + '"' });//Runtime.getRuntime().exec(urlStr);
	}

	public static ActionTypeDefinition fetchActionTypeDefinition(int actionID) throws ServerException {
		return (ActionTypeDefinition) getCommunicator().fetch(actionID, PeDataType.GUIDELINE_ACTION, false);
	}

	public static CBRAttributeType[] fetchAllCBRAttributeTypes() throws ServerException {
		List<CBRAttributeType> list = getCommunicator().search(new AllSearchFilter<CBRAttributeType>(PeDataType.CBR_ATTRIBUTE_TYPE));
		Collections.sort(list, new IDNameObjectComparator<CBRAttributeType>());
		return list.toArray(new CBRAttributeType[0]);
	}

	public static CBRCaseAction[] fetchAllCBRCaseActions() throws ServerException {
		List<CBRCaseAction> list = getCommunicator().search(new AllSearchFilter<CBRCaseAction>(PeDataType.CBR_CASE_ACTION));
		Collections.sort(list, new IDNameObjectComparator<CBRCaseAction>());
		return list.toArray(new CBRCaseAction[0]);
	}

	public static CBRCaseClass[] fetchAllCBRCaseClasses() throws ServerException {
		List<CBRCaseClass> list = getCommunicator().search(new AllSearchFilter<CBRCaseClass>(PeDataType.CBR_CASE_CLASS));
		Collections.sort(list, new IDNameObjectComparator<CBRCaseClass>());
		return list.toArray(new CBRCaseClass[0]);
	}

	public static CBRScoringFunction[] fetchAllCBRScoringFunctions() throws ServerException {
		List<CBRScoringFunction> list = getCommunicator().search(new AllSearchFilter<CBRScoringFunction>(PeDataType.CBR_SCORING_FUNCTION));
		Collections.sort(list, new IDNameObjectComparator<CBRScoringFunction>());
		return list.toArray(new CBRScoringFunction[0]);
	}

	public static CBRValueRange[] fetchAllCBRValueRanges() throws ServerException {
		List<CBRValueRange> list = getCommunicator().search(new AllSearchFilter<CBRValueRange>(PeDataType.CBR_VALUE_RANGE));
		Collections.sort(list, new IDNameObjectComparator<CBRValueRange>());
		return list.toArray(new CBRValueRange[0]);
	}

	public static DateSynonym[] fetchAllDateSynonyms() throws ServerException {
		List<DateSynonym> list = getCommunicator().search(new AllDateSynonymFilter());
		Collections.sort(list, DateSynonymComparator.getInstance());
		return list.toArray(new DateSynonym[0]);
	}

	public static DateSynonym[] fetchAllNamedDateSynonyms() throws ServerException {
		List<DateSynonym> list = getCommunicator().search(new AllNamedDateSynonymFilter());
		Collections.sort(list, DateSynonymComparator.getInstance());
		return list.toArray(new DateSynonym[0]);
	}

	public static CBRAttributeType fetchCBRAttributeType(int AttributeTypeID) throws ServerException {
		return (CBRAttributeType) getCommunicator().fetch(AttributeTypeID, PeDataType.CBR_ATTRIBUTE_TYPE, false);
	}

	public static CBRCaseAction fetchCBRCaseAction(int caseActionID) throws ServerException {
		return (CBRCaseAction) getCommunicator().fetch(caseActionID, PeDataType.CBR_CASE_ACTION, false);
	}

	public static CBRCaseClass fetchCBRCaseClass(int caseClassID) throws ServerException {
		return (CBRCaseClass) getCommunicator().fetch(caseClassID, PeDataType.CBR_CASE_CLASS, false);
	}

	public static CBRScoringFunction fetchCBRScoringFunction(int scoringFunctionID) throws ServerException {
		return (CBRScoringFunction) getCommunicator().fetch(scoringFunctionID, PeDataType.CBR_SCORING_FUNCTION, false);
	}

	public static CBRValueRange fetchCBRValueRange(int valueRangeID) throws ServerException {
		return (CBRValueRange) getCommunicator().fetch(valueRangeID, PeDataType.CBR_VALUE_RANGE, false);
	}

	public static DateSynonym fetchDateSynonym(int dsID) throws ServerException {
		return (DateSynonym) getCommunicator().fetch(dsID, PeDataType.DATE_SYNONYM, false);
	}

	public static GridTemplate fetchGuidelineTemplateDefinition(int templateID) throws ServerException {
		return (GridTemplate) getCommunicator().fetch(templateID, PeDataType.TEMPLATE, false);
	}

	public static String formatDate(Date date) {
		if (date != null) {
			try {
				printInfo("Date=" + date.toString());
				return DateFormat.getDateInstance().format(date);
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
			return null;
		}
		else {
			return null;
		}
	}

	public static String generateReportFilename() {
		return System.getProperty("user.home") + File.separatorChar + reportNameFormat.format(new Date()) + ".html";
	}

	public static List<ActionTypeDefinition> getActionTypes(TemplateUsageType usage) throws ServerException {
		GuidelineActionSearchFilter filter = new GuidelineActionSearchFilter();
		filter.setUsage(usage);
		List<ActionTypeDefinition> list = getCommunicator().search(filter);
		Collections.sort(list, new IDNameObjectComparator<ActionTypeDefinition>());
		return list;
	}

	public static PowerEditorLoggedApplet getApplet() {
		return instance.applet;
	}

	private static final Clipboard getClipBoard() {
		if (clipBoard == null) {
			clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		return clipBoard;
	}

	/**
	 * 
	 * @return <code>null</code> on error while accessing clipboard; empty string is clipboard is empty
	 */
	public static String getClipBoardContent() {
		try {
			String str = (String) getClipBoard().getContents(null).getTransferData(DataFlavor.stringFlavor);
			return (str == null ? "" : str);
		}
		catch (Exception e) {
			getLogger().warn("Failed to get system clipbaord content", e);
			return null;
		}
	}

	public static Communicator getCommunicator() {
		return instance.parent.getCommunicator();
	}

	public static EntityConfigHelper getEntityConfigHelper() {
		return configHelper.getEntityConfigHelper();

	}

	public static Map<GenericEntityType, EntityTab> getEntityTabMap() {
		return configHelper.getEntityTabMap();
	}

	public static GenericEntityType getEntityTypeForMessageContext() {
		return configHelper.getEntityConfigHelper().getEntityTypeForMessageContext() == null
				? null
				: GenericEntityType.forID(configHelper.getEntityConfigHelper().getEntityTypeForMessageContext().getTypeID().intValue());
	}

	public static EnumerationSourceProxy getEnumerationSourceProxy() {
		return new EnumSourceProxyImpl();
	}

	/**
	 * Reads the content of the specified file.
	 * @param file
	 * @return String
	 * @throws IOException on I/O error
	 */
	public static String getFileContent(File file) throws IOException {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		BufferedReader in = new BufferedReader(new FileReader(file));
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			out.println(line);
		}
		out.flush();
		out.close();
		in.close();
		return writer.toString();
	}

	public static List<GuidelineTab> getGuidelineTabs() {
		return configHelper.getGuidelineTabs();

	}

	public static String getHighestStatus() {
		return getInstance().statusCache.getHighestStatus();
	}

	public static String getHighestStatusDisplayLabel() {
		return getInstance().statusCache.getHighestStatusDisplayLabel();
	}

	/**
	 * Gets the one and only instance of this.
	 * @return the singleton instance of this class
	 */
	public static ClientUtil getInstance() {
		if (instance == null) {
			instance = new ClientUtil();
		}
		return instance;
	}

	/**
	 * Log4j Logger default instance for MPE applet.
	 */
	public static final Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger("com.mindbox.pe.client.PowerEditor");
		}
		return logger;
	}

	public static String getLowestStatus() {
		return getInstance().statusCache.getLowestStatus();
	}

	public static MainApplication getParent() {
		return instance.parent;
	}

	public static PowerEditorConfiguration getPowerEditorConfiguration() {
		return configHelper.getPowerEditorConfiguration();
	}

	public static PreferenceManager getPreferenceManager() {
		return DefaultPreferenceManager.getInstance();
	}

	// Convenience methods for retrieving various entities from the server

	public static String getStatusDisplayLabel(String status) {
		return getInstance().statusCache.getStatusDisplayLabel(status);
	}

	public static List<TestTypeDefinition> getTestTypes() throws ServerException {
		AllSearchFilter<TestTypeDefinition> filter = new AllSearchFilter<TestTypeDefinition>(PeDataType.GUIDELINE_TEST_CONDITION);
		List<TestTypeDefinition> list = getCommunicator().search(filter);
		Collections.sort(list, new IDNameObjectComparator<TestTypeDefinition>());
		return list;
	}

	public static UserInterfaceConfig getUserInterfaceConfig() {
		return configHelper.getUserInterfaceConfig();
	}

	public static UserManagementConfig getUserManagementConfig() {
		return configHelper.getUserManagementConfig();
	}

	public static UserProfile getUserSession() {
		return instance.parent.getUserSession();
	}

	public static void handleRuntimeException(Exception ex) {
		instance.parent.handleRuntimeException(ex);
	}

	public static boolean hasProductionRestrictions(String status) {
		return !ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(status);
	}

	public static boolean isFeatureEnabled(final FeatureNameType featureNameType) {
		return ConfigUtil.isFeatureEnabled(configHelper.getPowerEditorConfiguration(), featureNameType);
	}

	public static boolean isHighestStatus(String status) {
		return getInstance().statusCache.isHighestStatus(status);
	}

	public static Date parseDate(String s) throws ParseException {
		return DateFormat.getDateInstance().parse(s);
	}

	public static void placeOnClipboard(String str) {
		StringSelection selectionString = new StringSelection(str);
		getClipBoard().setContents(selectionString, selectionString);
	}

	public static void printError(String s) {
		getLogger().error(s);
	}

	public static void printInfo(String s) {
		getLogger().info(s);
	}

	public static void printOnDebug(String s) {
		if (logger != null && logger.isDebugEnabled()) {
			logger.debug(s);
		}
	}

	public static void printWarning(String s) {
		getLogger().warn(s);
	}

	public static String replace(String s, String s1, String s2) {
		int i = s.indexOf(s1);

		if (i == -1) return s;
		int j = s1.length();
		char ac[] = s.toCharArray();
		StringBuilder stringbuffer = new StringBuilder();
		int k = 0;

		for (; i != -1; i = s.indexOf(s1, k)) {
			stringbuffer.append(ac, k, i - k);
			stringbuffer.append(s2);
			k = i + j;
		}

		stringbuffer.append(ac, k, ac.length - k);
		return stringbuffer.toString();
	}

	public static synchronized void resetPowerEditorConfiguration(final PowerEditorConfiguration configuration, final UserManagementConfig userManagementConfig) {
		configHelper = new ConfigHelper(configuration, userManagementConfig);
	}

	/**
	 * Sets the context of the specified grid to the specified context array.
	 * @param grid grid
	 * @param context context array
	 */
	public static void setContext(AbstractGrid<?> grid, GuidelineContext[] context) {
		grid.clearAllContext();
		if (context != null) {
			for (int i = 0; i < context.length; i++) {
				if (context[i].getGenericEntityType() != null) {
					grid.setGenericEntityIDs(context[i].getGenericEntityType(), context[i].getIDs());
				}
				else if (context[i].hasCategoryContext()) {
					grid.setGenericCategoryIDs(getEntityConfigHelper().findEntityTypeForCategoryType(context[i].getGenericCategoryType()), context[i].getIDs());
				}
			}
		}
	}

	public static void setEnabled(boolean enabled, JComponent... components) {
		for (JComponent component : components) {
			component.setEnabled(enabled);
		}
	}

	/**
	 * Displays the specified URL in a web browser.
	 * @param urlStr the URL to display
	 * @throws IOException on error
	 * @since PowerEditor 4.2.0
	 */
	public static void showInWebBrowser(String urlStr) throws IOException {
		//Runtime.getRuntime().exec(new String[] { "explorer", '"' + filename + '"'});
		instance.applet.getAppletContext().showDocument(new URL(urlStr), "_blank");
	}

	public static void showTemplateEditPanel(GridTemplate template) throws CanceledException {
		instance.parent.showTemplateEditPanel(template);
	}

	/**
	 * Disables and hides buttons or enables and shows buttons
	 * @param jb : JButton array
	 * @param flagVisible : indicating whether to hide or show buttons
	 * @param flagEnable : indicating whether to enable or disable buttons
	 * @since 5.0.0
	 */
	public static void updateVisibileAndEnableOfButtons(JButton[] jb, boolean flagVisible, boolean flagEnable) {
		for (int i = 0; i < jb.length; i++) {
			JButton j = jb[i];
			j.setVisible(flagVisible);
			j.setEnabled(flagEnable);
		}
	}

	private Map<String, ImageIcon> iconMap;
	private final ResourceBundle labelsBundle;
	private final ResourceBundle messagesBundle;
	private final ResourceBundle gifsBundle;
	private final ResourceBundle valuesBundle;
	private PowerEditorLoggedApplet applet;
	private MainApplication parent = null;
	private StatusCache statusCache = null;
	private TimeOutController timeOutController = null;

	/**
	 * Constructs a new Client Util instance.
	 *
	 */
	private ClientUtil() {
		final ResourceBundleControlWrapper resourceBundleControlWrapper = new ResourceBundleControlWrapper();
		ClientUtil.getLogger().debug("--> ClientUtil()");

		iconMap = new HashMap<String, ImageIcon>(80, 0.95F);

		ClientUtil.getLogger().debug("... Loading lables bundle...");
		labelsBundle = ResourceBundle.getBundle(RESOURCE_NAME_LABELS, resourceBundleControlWrapper);

		ClientUtil.getLogger().debug("... Loading messages bundle...");
		messagesBundle = ResourceBundle.getBundle(RESOURCE_NAME_MESSAGES, resourceBundleControlWrapper);

		ClientUtil.getLogger().debug("... Loading GIFs bundle...");
		gifsBundle = ResourceBundle.getBundle(RESOURCE_NAME_GIFS, resourceBundleControlWrapper);

		ClientUtil.getLogger().debug("... Loading values bundle...");
		valuesBundle = ResourceBundle.getBundle(RESOURCE_NAME_VALUES, resourceBundleControlWrapper);

		ClientUtil.getLogger().debug("<-- ClientUtil()");
	}

	public synchronized void initializeTimeOutController(final long timeOutInSeconds) {
		final List<Long> notificationIntervals = new ArrayList<Long>();
		if (timeOutInSeconds > 60) {
			notificationIntervals.add(60L); // warning at 1 min
		}
		if (timeOutInSeconds > 300) {
			notificationIntervals.add(300L); // warning at 5 mins
		}
		if (timeOutInSeconds > 600) {
			notificationIntervals.add(600L); // warning at 10 mins
		}
		timeOutController = new TimeOutController(timeOutInSeconds, notificationIntervals, TimeUnit.SECONDS);
	}

	/**
	 * Download a file from PE server on the the specified path on the local machine.
	 * @param sourceFile the file path on the server
	 * @param targetFile the file path on local machine
	 * @throws IOException on I/O error
	 */
	public void downloadResourceFileFromServer(String sourceFile, String targetFile) throws IOException {
		InputStream sourceIS = this.getClass().getClassLoader().getResourceAsStream(sourceFile);
		if (sourceIS != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(sourceIS));
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(targetFile, true)), false);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				writer.println(line);
			}
			reader.close();
			writer.close();
		}
	}

	/**
	 * 
	 * @param cause
	 * @return message text from the specified error response
	 * @since PowerEditor 4.2.0
	 */
	private String generateDetailMessage(ErrorResponse cause) {
		if (cause.hasMessageResource()) {
			try {
				String message = cause.getErrorResourceParams() == null ? getMessage(cause.getErrorResourceKey()) : getMessage(cause.getErrorResourceKey(), cause.getErrorResourceParams());
				return message;
			}
			catch (Exception ex) {
				return cause.getErrorType();
			}
		}
		else {
			return cause.getErrorMessage();
		}
	}

	public ActionTypeDefinition getActionType(int id) {
		try {
			return (ActionTypeDefinition) getCommunicator().fetch(id, PeDataType.GUIDELINE_ACTION, false);
		}
		catch (Exception ex) {
			handleRuntimeException(ex);
			return null;
		}
	}

	public synchronized TimeOutController getTimeOutController() {
		return timeOutController;
	}

	public String getErrorMessage(ServerException ex) {
		if (ex == null) throw new NullPointerException("exception cannot be null");
		String message = null;
		if (ex.getErrorResponse() == null) {
			message = getMessage(ex.getErrorMessageKey(), ex.getErrorParams());
		}
		else {
			message = generateDetailMessage(ex.getErrorResponse());
		}
		return (UtilBase.isEmpty(message) ? getMessage("UnknownErrorMsg") : message);
	}

	public String getGIF(String s) {
		return gifsBundle.getString(s);
	}

	public String getLabel(CategoryType categoryDef) {
		return getLabel("label.category." + categoryDef.getName(), categoryDef.getName());
	}

	public String getLabel(GenericEntityType type) {
		return getLabel("label.entity." + type.toString(), type.getDisplayName());
	}

	public String getLabel(GenericEntityType type, CategoryType categoryDef) {
		return getLabel(categoryDef) + "/" + getLabel(type);
	}

	public String getLabel(String s) {
		if (s == null) return null;
		return getLabel(s, s);
	}

	public String getLabel(String s, Object[] params) {
		return MessageFormat.format(labelsBundle.getString(s), params);
	}

	/**
	 * Gets the label for the specified key.
	 * This returns the specified default, if the value of key is not found.
	 * @param s the key
	 * @param defaultVal
	 * @return value of <code>s</code>, if found; <code>defaultValue</code>, otherwise
	 * @since 3.0.0
	 */
	public String getLabel(String s, String defaultVal) {
		try {
			String value = labelsBundle.getString(s);
			return ((value == null || value.length() == 0) ? defaultVal : value);
		}
		catch (MissingResourceException ex) {
			return defaultVal;
		}
	}

	public String getMessage(String s) {
		return messagesBundle.getString(s);
	}

	public String getMessage(String s, Object... params) {
		String s1 = messagesBundle.getString(s);

		if (params != null && params.length > 0) {
			return MessageFormat.format(s1, params);
		}
		else
			return s1;
	}

	public TestTypeDefinition getTestType(int id) {
		try {
			return (TestTypeDefinition) getCommunicator().fetch(id, PeDataType.GUIDELINE_TEST_CONDITION, false);
		}
		catch (Exception ex) {
			handleRuntimeException(ex);
			return null;
		}
	}


	protected URL getURL(JApplet japplet, String s) {
		URL url = japplet.getCodeBase();
		if (url == null) throw null;
		URL url1 = null;
		try {
			url1 = new URL(url, s);
		}
		catch (MalformedURLException malformedurlexception) {
			malformedurlexception.printStackTrace();
			return null;
		}
		return url1;
	}

	public String getValueLiteral(String s) {
		return valuesBundle.getString(s);
	}

	private ImageIcon makeImageIcon(final JApplet japplet, final String s) {
		ImageIcon imageicon = iconMap.get(s);
		if (imageicon == null) {
			final URL url = getURL(japplet, getGIF(s)); // PE Applet's base URL is /powereditor
			imageicon = new ImageIcon(url);
			iconMap.put(s, imageicon);
		}
		return imageicon;
	}

	public ImageIcon makeImageIcon(String s) {
		return makeImageIcon(applet, s);
	}

	public String promptString(String message, String prevString) {
		return JOptionPane.showInputDialog(applet, message, prevString);
	}

	/**
	 * Resets cached type enum value map.
	 * <b>Note:</b> This must be called before calling other methods that uses cached type enum values.
	 * 
	 * @param map
	 */
	public synchronized void resetCachedTypeEnumValueMap(Map<String, List<TypeEnumValue>> map) {
		EntityModelCacheFactory.getInstance().setCacheTypeEnumValueMap(map);
		this.statusCache = null;
		this.statusCache = new StatusCache(EntityModelCacheFactory.getInstance().getAllEnumValues(TypeEnumValue.TYPE_STATUS));
	}

	public void setApplet(PowerEditorLoggedApplet japplet) {
		this.applet = japplet;
	}

	public void setParent(MainApplication parent) throws ServerException {
		this.parent = parent;
	}

	public void showAsDialog(String titleKey, boolean modal, JComponent component, boolean addCloseButton) {
		final JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(applet));
		dialog.setTitle(getLabel(titleKey));
		dialog.setModal(modal);
		dialog.setResizable(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.getContentPane().setLayout(new BorderLayout(2, 2));
		dialog.getContentPane().add(component, BorderLayout.CENTER);
		dialog.setSize(600, 420);
		UIFactory.centerize(dialog);
		if (addCloseButton) {
			JPanel bp = UIFactory.createFlowLayoutPanelCenterAlignment(4, 4);
			bp.add(UIFactory.createJButton("button.close", null, new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			}, null));
			dialog.getContentPane().add(bp, BorderLayout.SOUTH);
		}
		dialog.setVisible(true);
	}

	/**
	 * Displays a confirmation dialog with the specified message key and returns the confirmation status.
	 * @param key the confirmation message key
	 * @return <code>true</code> if confirmed; <code>false</code>, otherwise
	 */
	public boolean showConfirmation(String key) {
		int result = JOptionPane.showConfirmDialog(applet, getMessage(key), getLabel("d.title.question"), JOptionPane.YES_NO_OPTION);
		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Displays a confirmation dialog with the specified message key and returns the confirmation status.
	 * @param key the confirmation message key
	 * @param params the paramter for the message
	 * @return <code>true</code> if confirmed; <code>false</code>, otherwise
	 */
	public boolean showConfirmation(String key, Object... params) {
		int result = JOptionPane.showConfirmDialog(applet, getMessage(key, params), getLabel("d.title.question"), JOptionPane.YES_NO_OPTION);
		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Confirm with cancel option.
	 * @param key
	 * @return <code>null</code> on cancel
	 */
	public Boolean showConfirmationWithCancel(String key) {
		int result = JOptionPane.showConfirmDialog(applet, getMessage(key), getLabel("d.title.question"), JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else if (result == JOptionPane.YES_OPTION) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Confirm with cancel option.
	 * @param key
	 * @param params
	 * @return null on cancel
	 */
	public Boolean showConfirmationWithCancel(String key, Object... params) {
		int result = JOptionPane.showConfirmDialog(applet, getMessage(key, params), getLabel("d.title.question"), JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else if (result == JOptionPane.YES_OPTION) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	public Boolean showCustomConfirmationWithCancel(String key, Object[] options, Object defaultOption) {
		int result = JOptionPane.showOptionDialog(applet, getMessage(key), getLabel("d.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption);
		if (result == JOptionPane.CANCEL_OPTION) {
			return null;
		}
		else if (result == JOptionPane.YES_OPTION) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	public void showErrorDialog(ServerException serverException) {
		showErrorMessage(getErrorMessage(serverException));
	}

	public void showErrorDialog(String key) {
		try {
			showErrorMessage(getMessage(key));
		}
		catch (MissingResourceException ex) {
			showErrorMessage(key);
		}
	}

	public void showErrorDialog(String key, Object... params) {
		if (params == null) {
			showErrorDialog(key);
		}
		else {
			showErrorMessage(getMessage(key, params));
		}
	}

	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(applet, message, getLabel("d.title.error"), 0);
	}

	public void showInformation(String key) {
		JOptionPane.showMessageDialog(applet, getMessage(key), getLabel("d.title.info"), JOptionPane.INFORMATION_MESSAGE);
	}

	public void showInformation(String key, Object... params) {
		JOptionPane.showMessageDialog(applet, getMessage(key, params), getLabel("d.title.info"), JOptionPane.INFORMATION_MESSAGE);
	}

	public Boolean showSaveDiscardCancelDialog() {
		return showCustomConfirmationWithCancel("msg.question.unsaved.changes", new String[] { "Save", "Discard", "Cancel" }, "Save");
	}

	public void showText(String titleKey, String text) {
		JTextPane editorPane = new JTextPane();
		editorPane.setEditable(false);
		editorPane.setText(text);
		editorPane.setPreferredSize(new Dimension(400, 200));
		JOptionPane.showMessageDialog(ClientUtil.getApplet(), new JScrollPane(editorPane), ClientUtil.getInstance().getLabel(titleKey), JOptionPane.PLAIN_MESSAGE);
	}

	public void showWarning(String key) {
		JOptionPane.showMessageDialog(applet, getMessage(key), getLabel("d.title.warning"), JOptionPane.WARNING_MESSAGE);
	}

	public void showWarning(String key, Object... params) {
		JOptionPane.showMessageDialog(applet, getMessage(key, params), getLabel("d.title.warning"), JOptionPane.WARNING_MESSAGE);
	}


}
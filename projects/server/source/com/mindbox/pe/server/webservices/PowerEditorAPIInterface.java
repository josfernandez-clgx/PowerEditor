/**
 * 
 */
package com.mindbox.pe.server.webservices;

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.imexport.ExportException;
import com.mindbox.pe.server.imexport.ExportService;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.imexport.ImportService;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.report.ReportFilterDataHolder;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;

/**
 * For a Java class to be a Web Services Metadata-enabled service implementation bean,
 * it needs to follow these key musts and must-nots:<br>
 * <br>
 * . It must be public<br>
 * . It must not be final or abstract<br>
 * . It must have a default public constructor<br>
 * . It must not have a finalize() method<br>
 * <br>
 * The functions in a service implementation bean must follow these musts:<br>
 * <br>
 * . It must be public.<br>
 * . Its parameters, return values, and exceptions can be XML-enabled as per JAX RPC 1.1's
 * Java to XML/WSDL mapping rules—e.g., parameters and return values are primitives,
 * arrays, and so on; exception extends Exception; etc.
 * (Refer to Java API for XML-based Remote Procedure Call 1.1, Section 5 for more information.)
 * 
 * @author nill
 * @since PE 5.4.0
 * 
 */
@WebService
@HandlerChain(file = "handlers.xml")
public class PowerEditorAPIInterface {
	// Next line gives access to web service context.  It is "injected" in.
	@Resource
	WebServiceContext wsContext;

	private Logger logger = Logger.getLogger(getClass());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	static String username = null;

	/**
	 * Import the content into the PE set of entities.
	 * @param content
	 * @param merge
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure importEntities(String content, boolean merge) { //throws ImportException {
		if (logger.isDebugEnabled()) logger.debug("--> importData(WebService): size=" + content.length() + ",merge=" + merge);

		String userID = popUsername();
		User user = SecurityCacheManager.getInstance().getUser(userID);
		return importEntitiesMain(content, merge, userID, null, user);
	}

	/**
	 * 
	 * Import the content into the PE set of entities.  Credentials are supplied.
	 * @param content
	 * @param merge
	 * @param username
	 * @param password
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure importEntitiesWithCredentials(String content, boolean merge, String username, String password) { //throws ImportException {
		popUsername(); //shouldn't be needed but will prevent problems if credentials sent in header too
		if (logger.isDebugEnabled())
			logger.debug("--> importDataWithCredentials(WebService): size=" + content.length() + ",merge=" + merge);
		return importEntitiesMain(content, merge, username, password, null);
	}

	/**
	 * 
	 * Import the content into the PE set of entities.  This is the main method called by the web methods.
	 * @param content
	 * @param merge
	 * @param username
	 * @param password
	 * @param user
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	private PowerEditorInterfaceReturnStructure importEntitiesMain(String content, boolean merge, String username, String password,
			User user) { //throws ImportException {

		PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		ImportService importService = new ImportService();
		ImportSpec importSpec = new ImportSpec("ws-" + System.currentTimeMillis(), content, ImportSpec.IMPORT_DATA_REQUEST, merge);

		if (user == null) {
			if (!checkCredentials(username, password)) {
				returnStruct.addErrorMessage("Login failed for user " + username);
			}
			user = SecurityCacheManager.getInstance().getUser(username);
		}
		if (user != null) {
			if (hasPrivilege(username, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE)) {
				String errMsgs = null;
				try {
					importService.importDataXML(importSpec, true, user);
				}
				catch (ImportException ie) {
					errMsgs = "Error encountered importing file.  Message: " + ie.getMessage();
					returnStruct.addErrorMessage(errMsgs);
					logger.error(errMsgs);
					return returnStruct;
				}
				catch (Exception e) {
					errMsgs = "General error encountered importing file.  Message: " + e.getMessage();
					returnStruct.addErrorMessage(errMsgs);
					logger.error(errMsgs);
					return returnStruct;
				}
				returnStruct.setErrorMessages(importService.getImportResult().getErrorMessages());
				errMsgs = UtilBase.toString(importService.getImportResult().getErrorMessages());
				if (logger.isDebugEnabled())
					logger.debug("Return ERROR value from web service call (importDataWithCredentials): " + errMsgs);
				returnStruct.setGeneralMessages(importService.getImportResult().getMessages());
			}
			else {
				returnStruct.addErrorMessage("WebService API Access denieid for user " + user.getName());
			}
		}
		else {
			returnStruct.addErrorMessage("Invalid user; user not found: " + username);
		}
		return returnStruct;
	}

	/**
	 * Allows a user name that was passed in from the PowerEditorAPISecurityHandler to be retrieved.
	 * @return user name
	 * @author schneider
	 */
	private static synchronized String popUsername() {
		String un = PowerEditorAPIInterface.username;
		PowerEditorAPIInterface.username = null;
		return un;
	}

	/**
	 * Allows a user name to be passed in from the PowerEditorAPISecurityHandler.
	 * @param username
	 * @return true if push was successful (username must not be set)
	 * @author schneider
	 */
	public static synchronized boolean pushUsername(String username) {
		if (isUsernameClear()) {
			PowerEditorAPIInterface.username = username;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * One user can log-in at a time (synchronized access), so this checks to see if there
	 * is a user name waiting to be "popped" from the stack (of one).
	 * @return true if there is no value for username.
	 */
	public static boolean isUsernameClear() {
		return PowerEditorAPIInterface.username == null;
	}

	/**
	 * Check the credentials against the PE database.
	 * @param un
	 * @param pw
	 * @return true if credentials are valid.
	 */
	private boolean checkCredentials(String un, String pw) {
		boolean authenticated = false;
		try {
			// SGS - Authenticate using PowerEditor mechanisms
			LoginAttempt loginAttempt = new LoginAttempt(un, pw);
			logger.debug("Checking login attempt " + loginAttempt);
			authenticated = !loginAttempt.failed();
		}
		catch (ServletActionException sae) {
			logger.error("ServletActionException occurred in WebService PlainTextPasswordValidator: " + sae.getMessage());
		}
		catch (Exception ex) {
			logger.error("Exception occurred in WebService PlainTextPasswordValidator: " + ex.getMessage());
		}
		return authenticated;
	}

	/**
	 * 
	 * Export XML structures as specified by the parameters.
	 * Credentials are parameters.
	 * 
	 * @param exportEntities
	 * @param exportSecurity
	 * @param exportGuidelines
	 * @param exportParameters
	 * @param exportTemplates
	 * @param exportGuidelineActions
	 * @param exportTestConditions
	 * @param exportDateSynonyms
	 * @param includeEmptyContexts
	 * @param includeParentCategories
	 * @param includeChildrenCategories
	 * @param status
	 * @param usageTypes
	 * @param guidelineTemplateIDs
	 * @param paramTemplateIDs
	 * @param useDaysAgo
	 * @param daysAgo
	 * @param activeOnDate
	 * @param contextElements
	 * @param userID
	 * @param password
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure exportDataWithCredentials(boolean exportEntities, boolean exportSecurity,
			boolean exportGuidelines, boolean exportParameters, boolean exportTemplates, boolean exportGuidelineActions,
			boolean exportTestConditions, boolean exportDateSynonyms, boolean includeEmptyContexts, boolean includeParentCategories,
			boolean includeChildrenCategories, String status, String[] usageTypes, int[] guidelineTemplateIDs, int[] paramTemplateIDs,
			boolean useDaysAgo, int daysAgo, String activeOnDate, String contextElements, String userID, String password) {

		popUsername(); //shouldn't be needed but will prevent problems if credentials sent in header
		return exportDataMain(
				exportEntities,
				exportSecurity,
				exportGuidelines,
				exportParameters,
				exportTemplates,
				exportGuidelineActions,
				exportTestConditions,
				exportDateSynonyms,
				includeEmptyContexts,
				includeParentCategories,
				includeChildrenCategories,
				status,
				usageTypes,
				guidelineTemplateIDs,
				paramTemplateIDs,
				useDaysAgo,
				daysAgo,
				activeOnDate,
				contextElements,
				userID,
				password,
				null);
	}

	/**
	 * Export XML structures as specified by the parameters.
	 * Credentials are expected in the SOAP header.
	 * 
	 * @param exportEntities
	 * @param exportSecurity
	 * @param exportGuidelines
	 * @param exportParameters
	 * @param exportTemplates
	 * @param exportGuidelineActions
	 * @param exportTestConditions
	 * @param exportDateSynonyms
	 * @param includeEmptyContexts
	 * @param includeParentCategories
	 * @param includeChildrenCategories
	 * @param status
	 * @param usageTypes
	 * @param guidelineTemplateIDs
	 * @param paramTemplateIDs
	 * @param useDaysAgo
	 * @param daysAgo
	 * @param activeOnDate
	 * @param contextElements
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure exportData(boolean exportEntities, boolean exportSecurity, boolean exportGuidelines,
			boolean exportParameters, boolean exportTemplates, boolean exportGuidelineActions, boolean exportTestConditions,
			boolean exportDateSynonyms, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			String status, String[] usageTypes, int[] guidelineTemplateIDs, int[] paramTemplateIDs, boolean useDaysAgo, int daysAgo,
			String activeOnDate, String contextElements) {

		String userID = popUsername();
		User user = SecurityCacheManager.getInstance().getUser(userID);
		return exportDataMain(
				exportEntities,
				exportSecurity,
				exportGuidelines,
				exportParameters,
				exportTemplates,
				exportGuidelineActions,
				exportTestConditions,
				exportDateSynonyms,
				includeEmptyContexts,
				includeParentCategories,
				includeChildrenCategories,
				status,
				usageTypes,
				guidelineTemplateIDs,
				paramTemplateIDs,
				useDaysAgo,
				daysAgo,
				activeOnDate,
				contextElements,
				userID,
				null,
				user);
	}


	private PowerEditorInterfaceReturnStructure exportDataMain(boolean exportEntities, boolean exportSecurity, boolean exportGuidelines,
			boolean exportParameters, boolean exportTemplates, boolean exportGuidelineActions, boolean exportTestConditions,
			boolean exportDateSynonyms, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			String status, String[] usageTypes, int[] guidelineTemplateIDs, int[] paramTemplateIDs, boolean useDaysAgo, int daysAgo,
			String activeOnDate, String contextElements, String userID, String password, User user) {

		PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		if (user == null) {
			if (!checkCredentials(userID, password)) {
				returnStruct.addErrorMessage("Login failed for user " + userID);
				return returnStruct;
			}
			user = SecurityCacheManager.getInstance().getUser(userID);
		}
		if (user != null) {
			if (hasPrivilege(userID, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE)) {
				try {
					GuidelineReportFilter filter = new GuidelineReportFilter();
					filter.setIncludeEntities(exportEntities);
					filter.setIncludeSecurityData(exportSecurity);
					filter.setIncludeGuidelines(exportGuidelines);
					filter.setIncludeParameters(exportParameters);
					filter.setIncludeTemplates(exportTemplates);
					filter.setIncludeGuidelineActions(exportGuidelineActions);
					filter.setIncludeTestConditions(exportTestConditions);
					filter.setIncludeDateSynonyms(exportDateSynonyms);
					filter.setIncludeProcessData(false);
					filter.setIncludeCBR(false);
					filter.setIncludeParentCategories(includeParentCategories);
					filter.setIncludeEmptyContexts(includeEmptyContexts);
					filter.setIncludeChildrenCategories(includeChildrenCategories);

					if (usageTypes != null && usageTypes.length > 0) {
						TemplateUsageType[] uts = null;
						int utsLen = usageTypes.length;
						uts = new TemplateUsageType[utsLen];
						for (int cnt = 0; cnt < utsLen; cnt++) {
							uts[cnt] = TemplateUsageType.createInstance(usageTypes[cnt], null, null);
						}
						filter.addAllUsageTypes(uts);
					}

					if (contextElements != null) {
						ContextCreationReportHelper errorMessageStorage = new ContextCreationReportHelper(returnStruct);
						GuidelineContext[] contexts = ReportFilterDataHolder.createContexts(contextElements, errorMessageStorage);
						if (contexts != null) {
							for (int cnt = 0; cnt < contexts.length; cnt++) {
								filter.addContext(contexts[cnt]);
							}
						}
					}

					if (activeOnDate != null) {
						try {
							Date activeOnDateDt = dateFormat.parse(activeOnDate);
							if (activeOnDateDt != null) {
								filter.setActiveDate(activeOnDateDt);
							}
						}
						catch (ParseException e) {
							returnStruct.addErrorMessage("Invalid date format entered: " + activeOnDate + ". Date format pattern must be "
									+ dateFormat.toPattern());
							return returnStruct;
						}
					}

					if (status != null) {
						filter.setThisStatusAndAbove(status);
					}
					if (guidelineTemplateIDs != null && guidelineTemplateIDs.length > 0) {
						for (int cnt = 0; cnt < guidelineTemplateIDs.length; cnt++) {
							filter.addGuidelineTemplateID(guidelineTemplateIDs[cnt]);
						}
					}
					if (useDaysAgo) {
						filter.setDaysAgo(daysAgo);
					}

					if (paramTemplateIDs != null && paramTemplateIDs.length > 0) {
						for (int cnt = 0; cnt < paramTemplateIDs.length; cnt++) {
							filter.addParameterTemplateID(paramTemplateIDs[cnt]);
						}
					}

					StringWriter writer = new StringWriter();
					ExportService es = ExportService.getInstance();
					es.export(writer, filter, userID);
					returnStruct.setContent(writer.toString());
					writer.close();
				}
				catch (ExportException ee) {
					logger.error("ERROR - Export Exception", ee);
					returnStruct.addErrorMessage("ERROR - Export exception: " + ee.getMessage());
				}
				catch (Exception ee) {
					logger.error("ERROR - general error in Export", ee);
					returnStruct.addErrorMessage("ERROR - general error in Export: " + ee.getMessage());
				}
			}
			else {
				returnStruct.addErrorMessage("WebService API Access denieid for user " + user.getName());
			}
		}
		else {
			returnStruct.addErrorMessage("Invalid user; user not found: " + username);
		}
		return returnStruct;
	}


	/**
	 * Deploy rules, parameters, etc., as specified in the arguments.
	 * Deployed constructs will be on the server.
	 * Credentials are expected in the SOAP header.
	 * 
	 * @param status
	 * @param usageTypes
	 * @param deployGuidelines
	 * @param guidelineTemplateIDs
	 * @param deployParameters
	 * @param paramTemplateIDs
	 * @param useDaysAgo
	 * @param daysAgo
	 * @param activeOnDate
	 * @param includeEmptyContexts
	 * @param includeParentCategories
	 * @param includeChildrenCategories
	 * @param includeProcessData
	 * @param includeCBR
	 * @param includeEntities
	 * @param contextElements
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	*/
	@WebMethod()
	public PowerEditorInterfaceReturnStructure deploy(String status, String[] usageTypes, boolean deployGuidelines,
			int[] guidelineTemplateIDs, boolean deployParameters, int[] paramTemplateIDs, boolean useDaysAgo, int daysAgo,
			String activeOnDate, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			boolean includeProcessData, boolean includeCBR, boolean includeEntities, String contextElements, boolean exportPolicies) {

		String userID = popUsername();
		User user = SecurityCacheManager.getInstance().getUser(userID);
		return deployMain(
				status,
				usageTypes,
				deployGuidelines,
				guidelineTemplateIDs,
				deployParameters,
				paramTemplateIDs,
				useDaysAgo,
				daysAgo,
				activeOnDate,
				includeEmptyContexts,
				includeParentCategories,
				includeChildrenCategories,
				includeProcessData,
				includeCBR,
				includeEntities,
				contextElements,
				exportPolicies,
				userID,
				null,
				user);
	}


	/**
	 * 
	 * Deploy rules, parameters, etc., as specified in the arguments.
	 * Deployed constructs will be on the server.
	 * Credentials are specified as parameters.
	 * 
	 * @param status
	 * @param usageTypes
	 * @param deployGuidelines
	 * @param guidelineTemplateIDs
	 * @param deployParameters
	 * @param paramTemplateIDs
	 * @param useDaysAgo
	 * @param daysAgo
	 * @param activeOnDate
	 * @param includeEmptyContexts
	 * @param includeParentCategories
	 * @param includeChildrenCategories
	 * @param includeProcessData
	 * @param includeCBR
	 * @param includeEntities
	 * @param contextElements
	 * @param userID
	 * @param password
	 * @return PowerEditorInterfaceReturnStructure
	 * @author schneider
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure deployWithCredentials(String status, String[] usageTypes, boolean deployGuidelines,
			int[] guidelineTemplateIDs, boolean deployParameters, int[] paramTemplateIDs, boolean useDaysAgo, int daysAgo,
			String activeOnDate, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			boolean includeProcessData, boolean includeCBR, boolean includeEntities, String contextElements, boolean exportPolicies,
			String userID, String password) {

		popUsername(); //shouldn't be needed but will prevent problems if credentials sent in header
		return deployMain(
				status,
				usageTypes,
				deployGuidelines,
				guidelineTemplateIDs,
				deployParameters,
				paramTemplateIDs,
				useDaysAgo,
				daysAgo,
				activeOnDate,
				includeEmptyContexts,
				includeParentCategories,
				includeChildrenCategories,
				includeProcessData,
				includeCBR,
				includeEntities,
				contextElements,
				exportPolicies,
				userID,
				password,
				null);
	}

	private boolean hasPrivilege(String userID, String privilegeName) {
		try {
			return SecurityCacheManager.getInstance().checkPermissionByPrivilegeName(userID, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE);
		}
		catch (ServerException e) {
			logger.warn("Failed to check privilege for user " + userID);
			return false;
		}
	}

	private PowerEditorInterfaceReturnStructure deployMain(String status, String[] usageTypes, boolean deployGuidelines,
			int[] guidelineTemplateIDs, boolean deployParameters, int[] paramTemplateIDs, boolean useDaysAgo, int daysAgo,
			String activeOnDate, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			boolean includeProcessData, boolean includeCBR, boolean includeEntities, String contextElements, boolean exportPolicies,
			String userID, String password, User user) {

		PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		if (user == null) {
			if (!checkCredentials(userID, password)) {
				returnStruct.addErrorMessage("Login failed for user " + userID);
				return returnStruct;
			}
			user = SecurityCacheManager.getInstance().getUser(userID);
		}

		if (user != null) {
			if (hasPrivilege(userID, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE)) {
				DeploymentManager deploymentmanager = DeploymentManager.getInstance();
				try {
					GuidelineReportFilter filter = new GuidelineReportFilter();

					if (usageTypes != null && usageTypes.length > 0) {
						TemplateUsageType[] uts = null;
						int utsLen = usageTypes.length;
						uts = new TemplateUsageType[utsLen];
						for (int cnt = 0; cnt < utsLen; cnt++) {
							uts[cnt] = TemplateUsageType.createInstance(usageTypes[cnt], null, null);
						}
						filter.addAllUsageTypes(uts);
					}

					if (status != null) {
						filter.setThisStatusAndAbove(status);
					}
					if (guidelineTemplateIDs != null && guidelineTemplateIDs.length > 0) {
						for (int cnt = 0; cnt < guidelineTemplateIDs.length; cnt++) {
							filter.addGuidelineTemplateID(guidelineTemplateIDs[cnt]);
						}
					}
					filter.setIncludeGuidelines(deployGuidelines);
					filter.setIncludeParameters(deployParameters);
					if (useDaysAgo) {
						filter.setDaysAgo(daysAgo);
					}

					filter.setIncludeParentCategories(includeParentCategories);
					filter.setIncludeEmptyContexts(includeEmptyContexts);
					filter.setIncludeChildrenCategories(includeChildrenCategories);
					filter.setIncludeProcessData(includeProcessData);
					filter.setIncludeCBR(includeCBR);
					filter.setIncludeEntities(includeEntities);

					if (paramTemplateIDs != null && paramTemplateIDs.length > 0) {
						for (int cnt = 0; cnt < paramTemplateIDs.length; cnt++) {
							filter.addParameterTemplateID(paramTemplateIDs[cnt]);
						}
					}

					if (contextElements != null) {
						ContextCreationReportHelper errorMessageStorage = new ContextCreationReportHelper(returnStruct);
						GuidelineContext[] contexts = ReportFilterDataHolder.createContexts(contextElements, errorMessageStorage);
						for (int cnt = 0; cnt < contexts.length; cnt++) {
							filter.addContext(contexts[cnt]);
						}
					}

					if (activeOnDate != null) {
						try {
							Date activeOnDateDt = dateFormat.parse(activeOnDate);
							if (activeOnDateDt != null) {
								filter.setActiveDate(activeOnDateDt);
							}
						}
						catch (ParseException e) {
							returnStruct.addErrorMessage("Invalid date format entered: " + activeOnDate + ". Date format pattern must be "
									+ dateFormat.toPattern());
							return returnStruct;
						}
					}
					// A unique ID is needed for deployment 
					int id;
					try {
						id = DBIdGenerator.getInstance().nextSequentialID();
						logger.info("Generated id: " + id);
					}
					catch (SapphireException _ex) {
						returnStruct.addErrorMessage("Error - Could not obtain new ID to generate.");
						return returnStruct;
					}

					boolean flag = !deploymentmanager.deploy(id, filter, exportPolicies, userID);

					// Deploy is spawned on a thread so we must monitor 
					// the process instead of returning immediately.
					int runID = deploymentmanager.getCurrentRunId();
					List<GenerateStats> statsList;
					boolean deployDoneFlag = false;
					try {
						while (!deployDoneFlag) {
							try {
								Thread.sleep(500L);
								statsList = deploymentmanager.monitor(runID);
								deployDoneFlag = statsList != null ? !GenerateStats.isRunning(statsList) : true;
							}
							catch (InterruptedException interruptedexception) {
								returnStruct.addErrorMessage("Deploy process was interrupted. " + interruptedexception);
							}
						} // while
					}
					catch (Exception ex) {
						returnStruct.addErrorMessage("General runtime exception waiting for deploy. " + ex);
					}

					if (flag)
						returnStruct.addErrorMessage("Error - A generate run is currently in progress!!! Please wait!");
					else
						try {
							if (runID < 0) {
								returnStruct.addErrorMessage("Error generating rules.");
							}
							else {
								returnStruct.addErrorMessage(deploymentmanager.getDeployErrorStr(runID));
							}
						}
						catch (Exception ex) {
							returnStruct.addErrorMessage("Exception caught: " + ex.getMessage());
						}
					returnStruct.addGeneralMessage("Deployment completed.");
				}
				catch (RuleGenerationException ex) {
					returnStruct.addErrorMessage("Error generating rules " + ex.getMessage());
				}
			}
			else {
				returnStruct.addErrorMessage("WebService API Access denieid for user " + user.getName());
			}
		}
		else {
			returnStruct.addErrorMessage("Invalid user; user not found: " + username);
		}
		return returnStruct;
	}

	/**
	 * Simple ping test
	 * @return PowerEditorInterfaceReturnStructure
	 */
	@WebMethod()
	public PowerEditorInterfaceReturnStructure ping() {
		popUsername(); //shouldn't be needed but will prevent problems if credentials sent in header
		PowerEditorInterfaceReturnStructure ret = new PowerEditorInterfaceReturnStructure();
		logger.debug("--> ping(WebService) called");
		ret.setContent("Ping command received and processed.");
		return ret;
	}


}

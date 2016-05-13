package com.mindbox.pe.server.webservices;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.server.webservices.WebServiceUtil.checkCredentials;
import static com.mindbox.pe.server.webservices.WebServiceUtil.getAuthenticatedUserId;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.IOUtil;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.validate.MessageDetail;
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
import com.mindbox.pe.server.report.ErrorMessageStorage;
import com.mindbox.pe.server.report.ReportFilterDataHolder;
import com.mindbox.pe.wsdl.api.DeployRequest;
import com.mindbox.pe.wsdl.api.DeployResponse;
import com.mindbox.pe.wsdl.api.DeployWithCredentialsRequest;
import com.mindbox.pe.wsdl.api.DeployWithCredentialsResponse;
import com.mindbox.pe.wsdl.api.ExportDataRequest;
import com.mindbox.pe.wsdl.api.ExportDataResponse;
import com.mindbox.pe.wsdl.api.ExportDataWithCredentialsRequest;
import com.mindbox.pe.wsdl.api.ExportDataWithCredentialsResponse;
import com.mindbox.pe.wsdl.api.ImportEntitiesRequest;
import com.mindbox.pe.wsdl.api.ImportEntitiesResponse;
import com.mindbox.pe.wsdl.api.ImportEntitiesWithCredentialsRequest;
import com.mindbox.pe.wsdl.api.ImportEntitiesWithCredentialsResponse;
import com.mindbox.pe.wsdl.api.PingRequest;
import com.mindbox.pe.wsdl.api.PingResponse;
import com.mindbox.pe.wsdl.api.PowerEditorInterfaceReturnStructure;

// The WebService annotation is used by WebSphere
// The JAX-WS implementation uses WEB-INF/sun-jaxws.xml
@WebService(endpointInterface = "com.mindbox.pe.wsdl.api.PowerEditorAPIInterface", targetNamespace = "http://webservices.server.pe.mindbox.com/", portName = "PowerEditorAPIInterfacePort", serviceName = "PowerEditorAPIInterfaceService", wsdlLocation = "WEB-INF/wsdl/PowerEditorAPIInterfaceService.wsdl")
public class PowerEditorAPIInterfaceServiceImpl {

	private static final Logger LOG = Logger.getLogger(PowerEditorAPIInterfaceServiceImpl.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	// Next line gives access to web service context.  It is "injected" in.
	@Resource
	private WebServiceContext webServiceContext;

	public DeployResponse deploy(final DeployRequest deployRequest) {
		final String userID = getAuthenticatedUserId(getServletRequest());
		final User user = SecurityCacheManager.getInstance().getUser(userID);

		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processDeployRequest(
				deployRequest.getStatus(),
				deployRequest.getUsageTypes(),
				deployRequest.isDeployGuidelines(),
				deployRequest.getGuidelineTemplateIDs(),
				deployRequest.isDeployParameters(),
				deployRequest.getParamTemplateIDs(),
				deployRequest.isUseDaysAgo(),
				deployRequest.getDaysAgo(),
				deployRequest.getActiveOnDate(),
				deployRequest.isIncludeEmptyContexts(),
				deployRequest.isIncludeParentCategories(),
				deployRequest.isIncludeChildrenCategories(),
				deployRequest.isIncludeProcessData(),
				deployRequest.isIncludeCBR(),
				deployRequest.isIncludeEntities(),
				deployRequest.getContextElements(),
				deployRequest.isExportPolicies(),
				userID,
				null,
				user);
		final DeployResponse response = new DeployResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	public DeployWithCredentialsResponse deployWithCredentials(final DeployWithCredentialsRequest deployWithCredentialsRequest) {
		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processDeployRequest(
				deployWithCredentialsRequest.getStatus(),
				deployWithCredentialsRequest.getUsageTypes(),
				deployWithCredentialsRequest.isDeployGuidelines(),
				deployWithCredentialsRequest.getGuidelineTemplateIDs(),
				deployWithCredentialsRequest.isDeployParameters(),
				deployWithCredentialsRequest.getParamTemplateIDs(),
				deployWithCredentialsRequest.isUseDaysAgo(),
				deployWithCredentialsRequest.getDaysAgo(),
				deployWithCredentialsRequest.getActiveOnDate(),
				deployWithCredentialsRequest.isIncludeEmptyContexts(),
				deployWithCredentialsRequest.isIncludeParentCategories(),
				deployWithCredentialsRequest.isIncludeChildrenCategories(),
				deployWithCredentialsRequest.isIncludeProcessData(),
				deployWithCredentialsRequest.isIncludeCBR(),
				deployWithCredentialsRequest.isIncludeEntities(),
				deployWithCredentialsRequest.getContextElements(),
				deployWithCredentialsRequest.isExportPolicies(),
				deployWithCredentialsRequest.getUserID(),
				deployWithCredentialsRequest.getPassword(),
				null);

		final DeployWithCredentialsResponse response = new DeployWithCredentialsResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	public ExportDataResponse exportData(final ExportDataRequest exportDataRequest) {
		final String userID = getAuthenticatedUserId(getServletRequest());
		final User user = SecurityCacheManager.getInstance().getUser(userID);

		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processExportData(
				exportDataRequest.isExportEntities(),
				exportDataRequest.isExportSecurity(),
				exportDataRequest.isExportGuidelines(),
				exportDataRequest.isExportParameters(),
				exportDataRequest.isExportTemplates(),
				exportDataRequest.isExportGuidelineActions(),
				exportDataRequest.isExportTestConditions(),
				exportDataRequest.isExportDateSynonyms(),
				exportDataRequest.isIncludeEmptyContexts(),
				exportDataRequest.isIncludeParentCategories(),
				exportDataRequest.isIncludeChildrenCategories(),
				exportDataRequest.getStatus(),
				exportDataRequest.getUsageTypes(),
				exportDataRequest.getGuidelineTemplateIDs(),
				exportDataRequest.getParamTemplateIDs(),
				exportDataRequest.isUseDaysAgo(),
				exportDataRequest.getDaysAgo(),
				exportDataRequest.getActiveOnDate(),
				exportDataRequest.getContextElements(),
				userID,
				null,
				user);

		final ExportDataResponse response = new ExportDataResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	public ExportDataWithCredentialsResponse exportDataWithCredentials(final ExportDataWithCredentialsRequest exportDataWithCredentialsRequest) {
		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processExportData(
				exportDataWithCredentialsRequest.isExportEntities(),
				exportDataWithCredentialsRequest.isExportSecurity(),
				exportDataWithCredentialsRequest.isExportGuidelines(),
				exportDataWithCredentialsRequest.isExportParameters(),
				exportDataWithCredentialsRequest.isExportTemplates(),
				exportDataWithCredentialsRequest.isExportGuidelineActions(),
				exportDataWithCredentialsRequest.isExportTestConditions(),
				exportDataWithCredentialsRequest.isExportDateSynonyms(),
				exportDataWithCredentialsRequest.isIncludeEmptyContexts(),
				exportDataWithCredentialsRequest.isIncludeParentCategories(),
				exportDataWithCredentialsRequest.isIncludeChildrenCategories(),
				exportDataWithCredentialsRequest.getStatus(),
				exportDataWithCredentialsRequest.getUsageTypes(),
				exportDataWithCredentialsRequest.getGuidelineTemplateIDs(),
				exportDataWithCredentialsRequest.getParamTemplateIDs(),
				exportDataWithCredentialsRequest.isUseDaysAgo(),
				exportDataWithCredentialsRequest.getDaysAgo(),
				exportDataWithCredentialsRequest.getActiveOnDate(),
				exportDataWithCredentialsRequest.getContextElements(),
				exportDataWithCredentialsRequest.getUserID(),
				exportDataWithCredentialsRequest.getPassword(),
				null);

		final ExportDataWithCredentialsResponse response = new ExportDataWithCredentialsResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	private HttpServletRequest getServletRequest() {
		return HttpServletRequest.class.cast(webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST));
	}

	private boolean hasPrivilege(String userID, String privilegeName) {
		try {
			return SecurityCacheManager.getInstance().checkPermissionByPrivilegeName(userID, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE);
		}
		catch (ServerException e) {
			LOG.warn("Failed to check privilege for user " + userID);
			return false;
		}
	}

	public ImportEntitiesResponse importEntities(final ImportEntitiesRequest importEntitiesRequest) {
		logDebug(LOG, "--> importData(WebService): size=%d,merge=%s", importEntitiesRequest.getContent().length(), importEntitiesRequest.isMerge());

		final String userID = getAuthenticatedUserId(getServletRequest());
		final User user = SecurityCacheManager.getInstance().getUser(userID);
		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processImportEntities(importEntitiesRequest.getContent(), importEntitiesRequest.isMerge(), userID, null, user);

		final ImportEntitiesResponse response = new ImportEntitiesResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	public ImportEntitiesWithCredentialsResponse importEntitiesWithCredentials(final ImportEntitiesWithCredentialsRequest importEntitiesWithCredentialsRequest) {
		final PowerEditorInterfaceReturnStructure powerEditorInterfaceReturnStructure = processImportEntities(
				importEntitiesWithCredentialsRequest.getContent(),
				importEntitiesWithCredentialsRequest.isMerge(),
				importEntitiesWithCredentialsRequest.getUserID(),
				importEntitiesWithCredentialsRequest.getPassword(),
				null);
		final ImportEntitiesWithCredentialsResponse response = new ImportEntitiesWithCredentialsResponse();
		response.setReturn(powerEditorInterfaceReturnStructure);
		return response;
	}

	public PingResponse ping(final PingRequest pingRequest) {
		LOG.debug("--> ping(WebService) called");

		final PingResponse pingResponse = new PingResponse();
		pingResponse.setStatus("PowerEditor is running");
		return pingResponse;
	}

	private PowerEditorInterfaceReturnStructure processDeployRequest(String status, List<String> usageTypes, boolean deployGuidelines, List<Integer> guidelineTemplateIDs, boolean deployParameters,
			List<Integer> paramTemplateIDs, boolean useDaysAgo, int daysAgo, String activeOnDate, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			boolean includeProcessData, boolean includeCBR, boolean includeEntities, String contextElements, boolean exportPolicies, String userID, String password, User user) {
		final PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		if (user == null) {
			if (!checkCredentials(userID, password)) {
				returnStruct.getErrorMessages().add("Login failed for user " + userID);
				return returnStruct;
			}
			user = SecurityCacheManager.getInstance().getUser(userID);
		}

		if (user != null) {
			if (hasPrivilege(userID, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE)) {
				DeploymentManager deploymentmanager = DeploymentManager.getInstance();
				try {
					GuidelineReportFilter filter = new GuidelineReportFilter();

					for (final String usageType : usageTypes) {
						filter.addUsageType(TemplateUsageType.valueOf(usageType));
					}

					if (status != null) {
						filter.setThisStatusAndAbove(status);
					}
					if (guidelineTemplateIDs != null) {
						for (int id : guidelineTemplateIDs) {
							filter.addGuidelineTemplateID(id);
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

					if (paramTemplateIDs != null) {
						for (int id : paramTemplateIDs) {
							filter.addParameterTemplateID(id);
						}
					}

					if (contextElements != null) {
						final ErrorMessageStorage errorMessageStorage = new SimpleErrorMessageStorage();
						GuidelineContext[] contexts = ReportFilterDataHolder.createContexts(contextElements, errorMessageStorage);
						for (int cnt = 0; cnt < contexts.length; cnt++) {
							filter.addContext(contexts[cnt]);
						}
					}

					if (activeOnDate != null) {
						try {
							synchronized (DATE_FORMAT) {
								Date activeOnDateDt = DATE_FORMAT.parse(activeOnDate);
								if (activeOnDateDt != null) {
									filter.setActiveDate(activeOnDateDt);
								}
							}
						}
						catch (ParseException e) {
							returnStruct.getErrorMessages().add("Invalid date format entered: " + activeOnDate + ". Date format pattern must be " + DATE_FORMAT.toPattern());
							return returnStruct;
						}
					}
					// A unique ID is needed for deployment 
					int id;
					try {
						id = DBIdGenerator.getInstance().nextSequentialID();
						LOG.info("Generated id: " + id);
					}
					catch (SapphireException _ex) {
						returnStruct.getErrorMessages().add("Error - Could not obtain new ID to generate.");
						return returnStruct;
					}

					boolean flag = !deploymentmanager.deploy(id, filter, exportPolicies, userID);

					// Deploy is spawned on a thread so we must monitor 
					// the process instead of returning immediately.
					int runID = deploymentmanager.getCurrentRunId();
					GenerateStats generateStats;
					boolean deployDoneFlag = false;
					try {
						while (!deployDoneFlag) {
							try {
								Thread.sleep(500L);
								generateStats = deploymentmanager.monitor(runID);
								deployDoneFlag = generateStats != null ? !generateStats.isRunning() : true;
							}
							catch (InterruptedException interruptedexception) {
								returnStruct.getErrorMessages().add("Deploy process was interrupted. " + interruptedexception);
							}
						} // while
					}
					catch (Exception ex) {
						returnStruct.getErrorMessages().add("General runtime exception waiting for deploy. " + ex);
					}

					if (flag)
						returnStruct.getErrorMessages().add("Error - A generate run is currently in progress!!! Please wait!");
					else
						try {
							if (runID < 0) {
								returnStruct.getErrorMessages().add("Error generating rules.");
							}
							else {
								returnStruct.getErrorMessages().add(deploymentmanager.getDeployErrorStr(runID));
							}
						}
						catch (Exception ex) {
							returnStruct.getErrorMessages().add("Exception caught: " + ex.getMessage());
						}
					returnStruct.getGeneralMessages().add("Deployment completed.");
				}
				catch (RuleGenerationException ex) {
					returnStruct.getErrorMessages().add("Error generating rules " + ex.getMessage());
				}
			}
			else {
				returnStruct.getErrorMessages().add("WebService API Access denieid for user " + user.getName());
			}
		}
		else {
			returnStruct.getErrorMessages().add("Invalid user; user not found: " + userID);
		}
		return returnStruct;
	}

	private PowerEditorInterfaceReturnStructure processExportData(boolean exportEntities, boolean exportSecurity, boolean exportGuidelines, boolean exportParameters, boolean exportTemplates,
			boolean exportGuidelineActions, boolean exportTestConditions, boolean exportDateSynonyms, boolean includeEmptyContexts, boolean includeParentCategories, boolean includeChildrenCategories,
			String status, List<String> usageTypes, List<Integer> guidelineTemplateIDs, List<Integer> paramTemplateIDs, boolean useDaysAgo, int daysAgo, String activeOnDate, String contextElements,
			String userID, String password, User user) {

		PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		if (user == null) {
			if (!checkCredentials(userID, password)) {
				returnStruct.getErrorMessages().add("Login failed for user " + userID);
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

					for (final String usageType : usageTypes) {
						filter.addUsageType(TemplateUsageType.valueOf(usageType));
					}

					if (contextElements != null) {
						ErrorMessageStorage errorMessageStorage = new SimpleErrorMessageStorage();
						GuidelineContext[] contexts = ReportFilterDataHolder.createContexts(contextElements, errorMessageStorage);
						if (contexts != null) {
							for (int cnt = 0; cnt < contexts.length; cnt++) {
								filter.addContext(contexts[cnt]);
							}
						}
					}

					if (activeOnDate != null) {
						try {
							synchronized (DATE_FORMAT) {
								Date activeOnDateDt = DATE_FORMAT.parse(activeOnDate);
								if (activeOnDateDt != null) {
									filter.setActiveDate(activeOnDateDt);
								}
							}
						}
						catch (ParseException e) {
							returnStruct.getErrorMessages().add("Invalid date format entered: " + activeOnDate + ". Date format pattern must be " + DATE_FORMAT.toPattern());
							return returnStruct;
						}
					}

					if (status != null) {
						filter.setThisStatusAndAbove(status);
					}
					if (guidelineTemplateIDs != null) {
						for (int id : guidelineTemplateIDs) {
							filter.addGuidelineTemplateID(id);
						}
					}
					if (useDaysAgo) {
						filter.setDaysAgo(daysAgo);
					}
					if (paramTemplateIDs != null) {
						for (int id : paramTemplateIDs) {
							filter.addParameterTemplateID(id);
						}
					}

					final StringWriter writer = new StringWriter();
					ExportService es = ExportService.getInstance();
					es.export(writer, filter, userID);
					returnStruct.setContent(writer.toString());
					writer.close();
				}
				catch (ExportException ee) {
					LOG.error("ERROR - Export Exception", ee);
					returnStruct.getErrorMessages().add("ERROR - Export exception: " + ee.getMessage());
				}
				catch (Exception ee) {
					LOG.error("ERROR - general error in Export", ee);
					returnStruct.getErrorMessages().add("ERROR - general error in Export: " + ee.getMessage());
				}
			}
			else {
				returnStruct.getErrorMessages().add("WebService API Access denieid for user " + user.getName());
			}
		}
		else {
			returnStruct.getErrorMessages().add("Invalid user; user not found: " + userID);
		}

		return returnStruct;
	}

	private PowerEditorInterfaceReturnStructure processImportEntities(final String content, final boolean merge, final String username, final String password, final User user) {
		final PowerEditorInterfaceReturnStructure returnStruct = new PowerEditorInterfaceReturnStructure();
		User userToUse = user;
		if (userToUse == null) {
			if (!checkCredentials(username, password)) {
				returnStruct.getErrorMessages().add("Login failed for user " + username);
			}
			userToUse = SecurityCacheManager.getInstance().getUser(username);
		}

		if (userToUse != null) {
			final ImportService importService = new ImportService();
			final File tempFile = new File(System.getProperty("user.home"), String.format("pe-ws-%d-temp.xml", System.currentTimeMillis()));
			try {
				IOUtil.saveToFile(new ByteArrayInputStream(content.getBytes("UTF-8")), tempFile, true);

				ImportSpec importSpec = new ImportSpec(tempFile.getAbsolutePath(), content, merge);
				if (hasPrivilege(username, PrivilegeConstants.PRIV_ACCESS_WEBSERVICE)) {
					String errMsgs = null;
					try {
						importService.importDataXML(importSpec, true, userToUse);
					}
					catch (ImportException ie) {
						errMsgs = "Error encountered importing file.  Message: " + ie.getMessage();
						returnStruct.getErrorMessages().add(errMsgs);
						LOG.error(errMsgs);
						return returnStruct;
					}
					catch (Exception e) {
						errMsgs = "General error encountered importing file.  Message: " + e.getMessage();
						returnStruct.getErrorMessages().add(errMsgs);
						LOG.error(errMsgs);
						return returnStruct;
					}

					for (final MessageDetail messageDetail : importService.getImportResult().getErrorMessages()) {
						returnStruct.getErrorMessages().add(messageDetail.toString());
					}

					errMsgs = UtilBase.toString(importService.getImportResult().getErrorMessages());

					logDebug(LOG, "Return ERROR value from web service call (importDataWithCredentials): " + errMsgs);
					for (final MessageDetail messageDetail : importService.getImportResult().getMessages()) {
						returnStruct.getGeneralMessages().add(messageDetail.toString());
					}
				}
				else {
					returnStruct.getErrorMessages().add("WebService API Access denieid for user " + userToUse.getName());
				}
			}
			catch (IOException e) {
				LOG.error("Failed to import", e);
				returnStruct.getErrorMessages().add(String.format("Failed to import due to I/O error: %s", e.getMessage()));
			}
			finally {
				IOUtil.delete(tempFile);
			}
		}
		else {
			returnStruct.getErrorMessages().add("Invalid user; user not found: " + username);
		}
		return returnStruct;
	}


}

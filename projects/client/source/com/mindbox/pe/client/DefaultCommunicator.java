package com.mindbox.pe.client;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.BulkUpdateGridDataRequest;
import com.mindbox.pe.communication.ByteArrayResponse;
import com.mindbox.pe.communication.CheckDeployRuleRequest;
import com.mindbox.pe.communication.CheckExistingCompatibilityRequest;
import com.mindbox.pe.communication.CheckForUniqueNameRequest;
import com.mindbox.pe.communication.CloneCBRRequest;
import com.mindbox.pe.communication.CloneCBRResponse;
import com.mindbox.pe.communication.CloneGuidelineRequest;
import com.mindbox.pe.communication.CloneRequest;
import com.mindbox.pe.communication.CompatibilityResponselessActionRequest;
import com.mindbox.pe.communication.DateSynonymInUseRequest;
import com.mindbox.pe.communication.DeleteTemplateRequest;
import com.mindbox.pe.communication.DeployRequest;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.EnableUserRequest;
import com.mindbox.pe.communication.ExportRequest;
import com.mindbox.pe.communication.ExportRequestToServer;
import com.mindbox.pe.communication.ExportRequestToServerResponse;
import com.mindbox.pe.communication.FetchAllEnumerationValuesRequest;
import com.mindbox.pe.communication.FetchApplicableEnumerationValuesRequest;
import com.mindbox.pe.communication.FetchCompatibilityRequest;
import com.mindbox.pe.communication.FetchCustomReportsRequest;
import com.mindbox.pe.communication.FetchDomainDefinitionXMLRequest;
import com.mindbox.pe.communication.FetchEnumerationSourceDetailsRequest;
import com.mindbox.pe.communication.FetchFullGuidelineContextRequest;
import com.mindbox.pe.communication.FetchGridDataRequest;
import com.mindbox.pe.communication.FetchGridSummaryRequest;
import com.mindbox.pe.communication.FetchNextRuleIDRequest;
import com.mindbox.pe.communication.FetchParameterGridRequest;
import com.mindbox.pe.communication.GenericCategoryResponselessActionRequest;
import com.mindbox.pe.communication.GenericEntityResponselessActionRequest;
import com.mindbox.pe.communication.GetIDEntityRequest;
import com.mindbox.pe.communication.GetLastDeployErrorRequest;
import com.mindbox.pe.communication.GridActionRequest;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.IDEntityResponselessActionRequest;
import com.mindbox.pe.communication.ImportRequest;
import com.mindbox.pe.communication.ImportResponse;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.LogoutRequest;
import com.mindbox.pe.communication.MonitorDeployRequest;
import com.mindbox.pe.communication.MonitorDeployResponse;
import com.mindbox.pe.communication.NamedEntityResponselessActionRequest;
import com.mindbox.pe.communication.NewTemplateVersionRequest;
import com.mindbox.pe.communication.PrintRequest;
import com.mindbox.pe.communication.ReloadConfigurationRequest;
import com.mindbox.pe.communication.ReloadConfigurationResponse;
import com.mindbox.pe.communication.ReloadUserDataRequest;
import com.mindbox.pe.communication.ReplaceDateSynonymsRequest;
import com.mindbox.pe.communication.ReportRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveRequest;
import com.mindbox.pe.communication.SaveResponse;
import com.mindbox.pe.communication.SearchRequest;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.communication.SingleEntityResponse;
import com.mindbox.pe.communication.StringResponse;
import com.mindbox.pe.communication.TemplateHasGuidelineRequest;
import com.mindbox.pe.communication.UpdateGridContextRequest;
import com.mindbox.pe.communication.UpdateGridDataRequest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ExternalEnumSourceDetail;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.filter.TemplateFilter;
import com.mindbox.pe.model.report.AbstractReportSpec;

/**
 * Default concrete implementation of {@link Communicator}.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 1.0
 */
public final class DefaultCommunicator implements Communicator {

	private final String userID;
	private final String sessionID;
	private final Logger logger;

	/**
	 * Constructs a new default communication for the specified frame base.
	 * 
	 * @param frameBase
	 *            the root frame
	 */
	public DefaultCommunicator(MainApplication frameBase) {
		this.logger = Logger.getLogger(Communicator.class);
		this.userID = frameBase.getUserID();
		this.sessionID = frameBase.getSessionID();
	}

	@Override
	public List<ExternalEnumSourceDetail> getEnumerationSourceDetails() throws ServerException {
		FetchEnumerationSourceDetailsRequest request = new FetchEnumerationSourceDetailsRequest(userID, sessionID);
		return request.sendRequest().getResultList();
	}

	@Override
	public List<EnumValue> getAllEnumValuesFromEnumerationSource(String sourceName) throws ServerException {
		FetchAllEnumerationValuesRequest request = new FetchAllEnumerationValuesRequest(sourceName, userID, sessionID);
		return request.sendRequest().getResultList();
	}

	@Override
	public List<EnumValue> getApplicableEnumValuesFromEnumerationSource(String sourceName, String selectorValue) throws ServerException {
		FetchApplicableEnumerationValuesRequest request = new FetchApplicableEnumerationValuesRequest(
				sourceName,
				selectorValue,
				userID,
				sessionID);
		return request.sendRequest().getResultList();
	}

	public boolean isExistingCompatibility(GenericEntityType type1, int id1, GenericEntityType type2, int id2) throws ServerException {
		CheckExistingCompatibilityRequest request = new CheckExistingCompatibilityRequest(userID, sessionID, type1, id2, type2, id2);
		return request.sendRequest().isTrue();
	}

	public boolean checkNameForUniqueness(EntityType entityType, String name) throws ServerException {
		CheckForUniqueNameRequest request = new CheckForUniqueNameRequest(userID, sessionID, entityType, name);
		return request.sendRequest().isTrue();
	}

	public long getNextRuleID() throws ServerException {
		FetchNextRuleIDRequest request = new FetchNextRuleIDRequest(userID, sessionID);
		return request.sendRequest().getLong().longValue();
	}

	public List<UserData> reloadUserData() throws ServerException {
		ReloadUserDataRequest request = new ReloadUserDataRequest();
		ListResponse<UserData> response = (ListResponse<UserData>) request.sendRequest();
		return response.getResultList();
	}

	public String reloadConfiguration() throws ServerException {
		ReloadConfigurationRequest reloadRequestComm = new ReloadConfigurationRequest();
		ReloadConfigurationResponse reloadResponseComm = (ReloadConfigurationResponse) reloadRequestComm.sendRequest();
		if (reloadResponseComm.succeeded())
			return null;
		else
			return reloadResponseComm.getReloadFailureMsg();
	}

	public List<String> fetchCustomReportNames() throws ServerException {
		logger.debug(">>> fetchCustomReportNames");
		FetchCustomReportsRequest request = new FetchCustomReportsRequest(userID, sessionID);
		ListResponse<String> response = (ListResponse<String>) request.sendRequest();
		logger.debug("<<< fetchCustomReportNames");
		return response.getResultList();
	}

	public boolean hasGuidelines(int templateID) throws ServerException {
		logger.debug(">>> hasGuidelines: " + templateID);
		TemplateHasGuidelineRequest request = new TemplateHasGuidelineRequest(userID, sessionID, templateID);
		BooleanResponse response = (BooleanResponse) request.sendRequest();
		logger.debug("<<< hasGuidelines: " + response);
		return response.isTrue();
	}

	public void deleteTemplate(int templateID, boolean deleteGuidelines) throws ServerException {
		logger.debug(">>> deleteTemplate: " + templateID + ",deleteGuidelines=" + deleteGuidelines);
		DeleteTemplateRequest request = new DeleteTemplateRequest(userID, sessionID, templateID, deleteGuidelines);
		request.sendRequest();
		logger.debug("<<< deleteTemplate");
	}

	public GuidelineContext[] fetchFullContext(int templateID, GuidelineContext[] subContext) throws ServerException {
		logger.debug(">>> fetchFullContext for " + templateID + ", " + subContext);
		List<GuidelineContext> list = null;
		FetchFullGuidelineContextRequest request = new FetchFullGuidelineContextRequest(userID, sessionID, templateID, subContext);
		ListResponse<GuidelineContext> response = request.sendRequest();
		list = response.getResultList();
		return (list == null ? null : list.toArray(new GuidelineContext[0]));
	}

	public void updateGridContext(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts) throws ServerException {
		logger.debug(">>> updateGridContext: " + templateID + ",newc=" + newContexts);
		UpdateGridContextRequest request = new UpdateGridContextRequest(userID, sessionID, templateID, grids, newContexts);
		request.sendRequest();
		logger.debug("<<< updateGridContext");
	}

	public List<List<GuidelineReportData>> findCutoverGuidelines(int templateID, DateSynonym cutoverDate) throws ServerException {
		logger.debug(">>> findCutoverGuidelines for " + templateID + ", " + cutoverDate);
		List<List<GuidelineReportData>> list = null;
		NewTemplateVersionRequest<ListResponse<List<GuidelineReportData>>> request = NewTemplateVersionRequest.createScanInstance(
				userID,
				sessionID,
				templateID,
				cutoverDate);
		ListResponse<List<GuidelineReportData>> response = request.sendRequest();
		list = response.getResultList();
		return list;
	}

	public int makeNewVersion(int oldTemplateID, GridTemplate newVersion, DateSynonym cutoverDate) throws ServerException {
		logger.debug(">>> makeNewVersion for " + oldTemplateID + ", " + newVersion.getVersion() + ", " + cutoverDate);
		NewTemplateVersionRequest<SaveResponse> request = NewTemplateVersionRequest.createCommitInstance(
				userID,
				sessionID,
				oldTemplateID,
				newVersion,
				cutoverDate);
		SaveResponse response = (SaveResponse) request.sendRequest();
		return response.getPersistentID();
	}

	public String generateReportURL(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException {
		logger.debug(">>> generateReportURL: " + reportSpec);
		ReportRequest request = new ReportRequest(userID, sessionID, reportSpec, guidelines);
		StringResponse response = (StringResponse) request.sendRequest();
		logger.debug("<<< generateReportURL: " + response.getString());
		return response.getString();
	}

	public byte[] generatePolicySummaryReport(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException {
		logger.debug(">>> generatePolicySummaryReport: " + reportSpec);
		PrintRequest request = new PrintRequest(userID, sessionID, reportSpec, guidelines);
		ByteArrayResponse response = (ByteArrayResponse) request.sendRequest();
		logger.debug("<<< generatePolicySummaryReport: " + response.getByteArray().length);
		return response.getByteArray();

	}

	public void cloneGuidelines(int oldTemplateID, int newTemplateID) throws ServerException {
		logger.debug(">>> cloneGuidelines for " + oldTemplateID + " -> " + newTemplateID);
		CloneGuidelineRequest request = new CloneGuidelineRequest(userID, sessionID, oldTemplateID, newTemplateID);
		request.sendRequest();
	}

	public ImportResult importData(ImportSpec importSpec) throws ServerException, IOException {
		logger.debug(">>> importData for " + importSpec);
		ImportRequest request = new ImportRequest(userID, sessionID, importSpec);
		ImportResponse response = (ImportResponse) request.sendRequest();
		logger.debug("<<< importData: " + response.getImportResult());
		return response.getImportResult();
	}

	public byte[] exportDataToClient(GuidelineReportFilter filter) throws ServerException {
		logger.debug(">>> exportData");
		ExportRequest request = new ExportRequest(filter, userID, sessionID);
		ByteArrayResponse response = (ByteArrayResponse) request.sendRequest();
		logger.debug("<<< exportData: " + response.getByteArray().length);
		return response.getByteArray();
	}

	public int exportDataToServer(GuidelineReportFilter filter, String filename) throws ServerException {
		logger.debug(">>> exportDataToServer");
		ExportRequestToServer request = new ExportRequestToServer(filter, filename, userID, sessionID);
		ResponseComm responsecomm = request.sendRequest();
		ExportRequestToServerResponse exporttoservercomm = (ExportRequestToServerResponse) responsecomm;
		logger.debug("<<< exportDataToServer: " + exporttoservercomm.getGenerateRunId());
		return exporttoservercomm.getGenerateRunId();
	}

	public byte[] getDomainDefintionXML() throws ServerException {
		logger.debug(">>> getDomainDefintionXML");
		FetchDomainDefinitionXMLRequest request = new FetchDomainDefinitionXMLRequest(userID, sessionID);
		ByteArrayResponse response = (ByteArrayResponse) request.sendRequest();
		logger.debug("<<< getDomainDefintionXML: " + response.getByteArray().length);
		return response.getByteArray();
	}

	public boolean hasDeployRule(int templateID, int columnID) throws ServerException {
		logger.debug(">>> hasDeployRule for " + templateID + "; " + columnID);
		CheckDeployRuleRequest request = new CheckDeployRuleRequest(userID, sessionID, templateID, columnID);
		BooleanResponse response = (BooleanResponse) request.sendRequest();
		logger.debug("<<< hasReployRule: " + response.isTrue());
		return response.isTrue();
	}

	public void deleteUser(UserData user) throws ServerException {
		NamedEntityResponselessActionRequest request = new NamedEntityResponselessActionRequest(
				userID,
				sessionID,
				user.getUserID(),
				EntityType.USER_DATA,
				SessionRequest.ACTION_TYPE_DELETE);
		request.sendRequest();
	}

	public DeployResponse deploy(GuidelineReportFilter filter, boolean exportPolicies) throws ServerException {
		DeployRequest deployrequestcomm = new DeployRequest(userID, sessionID, filter, exportPolicies);

		ResponseComm responsecomm = deployrequestcomm.sendRequest();
		DeployResponse deployresponsecomm = (DeployResponse) responsecomm;
		return deployresponsecomm;
	}

	public List<GridTemplate> fetchTemplateSummaries(TemplateUsageType s) throws ServerException {
		SearchRequest<GridTemplate> request = new SearchRequest<GridTemplate>(userID, sessionID, new TemplateFilter(s));
		ListResponse<GridTemplate> response = (ListResponse<GridTemplate>) request.sendRequest();
		return response.getResultList();
	}

	public void logout() throws ServerException {
		LogoutRequest logoutrequestcomm = new LogoutRequest(userID, sessionID);
		ResponseComm responsecomm = logoutrequestcomm.sendRequest();
		logger.debug("> response from logout request: " + responsecomm);
	}

	@Override
	public List<GenerateStats> retrieveDeployStats(int i) throws ServerException {
		MonitorDeployRequest monitordeployrequestcomm = new MonitorDeployRequest(userID, sessionID, i);
		ResponseComm responsecomm = monitordeployrequestcomm.sendRequest();

		MonitorDeployResponse monitordeployresponsecomm = (MonitorDeployResponse) responsecomm;

		return monitordeployresponsecomm.getStats();
	}

	@Override
	public void delete(int entityID, EntityType entityType) throws ServerException {
		IDEntityResponselessActionRequest request = new IDEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				entityType,
				SessionRequest.ACTION_TYPE_DELETE);
		request.sendRequest();
	}

	@Override
	public void delete(int entityID, GenericEntityType type) throws ServerException {
		GenericEntityResponselessActionRequest request = new GenericEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				type,
				SessionRequest.ACTION_TYPE_DELETE);
		request.sendRequest();
	}

	@Override
	public Persistent fetch(int entityID, EntityType entityType, boolean lockEntity) throws ServerException {
		GetIDEntityRequest request = new GetIDEntityRequest(userID, sessionID, entityID, entityType, lockEntity);
		SingleEntityResponse response = request.sendRequest();
		return response.getPersistent();
	}

	@Override
	public void lock(int entityID, EntityType entityType) throws ServerException {
		IDEntityResponselessActionRequest request = new IDEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				entityType,
				SessionRequest.ACTION_TYPE_LOCK);
		request.sendRequest();
	}

	@Override
	public void lockUser(String userIDToLock) throws ServerException {
		NamedEntityResponselessActionRequest request = new NamedEntityResponselessActionRequest(
				this.userID,
				sessionID,
				userIDToLock,
				EntityType.USER_DATA,
				SessionRequest.ACTION_TYPE_LOCK);
		request.sendRequest();
	}

	@Override
	public void enableUser(String userID) throws ServerException {
		EnableUserRequest request = new EnableUserRequest(userID, sessionID);
		request.sendRequest();
	}

	@Override
	public int save(Persistent object, boolean lock) throws ServerException {
		return save(object, lock, true);
	}

	@Override
	public int save(Persistent object, boolean lock, boolean validate) throws ServerException {
		SaveRequest request = new SaveRequest(userID, sessionID, object, lock, false, validate);
		SaveResponse response = (SaveResponse) request.sendRequest();
		return response.getPersistentID();
	}

	@Override
	public int clone(GenericEntity object, boolean copyPolicies, boolean lock) throws ServerException {
		CloneRequest request = new CloneRequest(userID, sessionID, object, lock, true, copyPolicies);
		SaveResponse response = (SaveResponse) request.sendRequest();
		return response.getPersistentID();
	}

	/**
	 * Clone case bases.
	 * @param oldCaseBaseID
	 * @param newCaseBaseName
	 * @return New Case Base ID.
	 * @throws ServerException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	@Override
	public int cloneCaseBases(int oldCaseBaseID, String newCaseBaseName) throws ServerException {
		CloneCBRRequest request = new CloneCBRRequest(userID, sessionID, oldCaseBaseID, newCaseBaseName);
		CloneCBRResponse response = (CloneCBRResponse) request.sendRequest();
		return response.getPersistentID();
	}

	@Override
	public <T extends Persistent> List<T> search(SearchFilter<T> searchFilter) throws ServerException {
		SearchRequest<T> request = new SearchRequest<T>(userID, sessionID, searchFilter);
		ListResponse<T> response = request.sendRequest();
		return response.getResultList();
	}

	public void unlock(int entityID, EntityType entityType) throws ServerException {
		IDEntityResponselessActionRequest request = new IDEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				entityType,
				SessionRequest.ACTION_TYPE_UNLOCK);
		request.sendRequest();
	}

	public void unlockUser(String userIDToUnlock) throws ServerException {
		NamedEntityResponselessActionRequest request = new NamedEntityResponselessActionRequest(
				this.userID,
				sessionID,
				userIDToUnlock,
				EntityType.USER_DATA,
				SessionRequest.ACTION_TYPE_UNLOCK);
		request.sendRequest();
	}

	public List<GridSummary> fetchGridSummaries(TemplateUsageType usageType, GuidelineContext[] contexts) throws ServerException {
		logger.debug(">>> fetchGridSummaries with " + usageType + ", " + contexts);
		FetchGridSummaryRequest request = new FetchGridSummaryRequest(userID, sessionID, usageType, contexts);
		ListResponse<GridSummary> response = (ListResponse<GridSummary>) request.sendRequest();
		return response.getResultList();
	}

	public GridDataResponse fetchGridData(int templateID, GuidelineContext[] contexts) throws ServerException {
		logger.debug(">>> fetchGridData with " + templateID + ", " + contexts);
		FetchGridDataRequest request = new FetchGridDataRequest(userID, sessionID, templateID, contexts);
		GridDataResponse response = (GridDataResponse) request.sendRequest();
		return response;
	}

	public void bulkSaveGridData(List<GuidelineReportData> data, String status, DateSynonym eff, DateSynonym exp) throws ServerException {
		logger.debug(">>> bulkSaveGridData with " + data.toString() + ", " + status + ", " + eff + ", " + exp);
		BulkUpdateGridDataRequest request = new BulkUpdateGridDataRequest(userID, sessionID, data, status, eff, exp);
		request.sendRequest();
		logger.debug("<<< bulkSaveGridData");
	}

	public void saveGridData(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids) throws ServerException {
		logger.debug(">>> saveGridData with " + templateID + ", " + grids);
		logger.debug("... saveGridData: grid.size=" + (grids == null ? 0 : grids.size()));
		logger.debug("... saveGridData: removedGrids.size=" + (removedGrids == null ? 0 : removedGrids.size()));
		UpdateGridDataRequest request = new UpdateGridDataRequest(userID, sessionID, templateID, grids, removedGrids);
		request.sendRequest();
		logger.debug("<<< saveGridData");
	}

	public void lockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		GridActionRequest request = new GridActionRequest(userID, sessionID, templateID, contexts, GridActionRequest.ACTION_TYPE_LOCK);
		request.sendRequest();
	}

	public void unlockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		GridActionRequest request = new GridActionRequest(userID, sessionID, templateID, contexts, GridActionRequest.ACTION_TYPE_UNLOCK);
		request.sendRequest();
	}

	public List<ParameterGrid> fetchParameters(int templateID) throws ServerException {
		logger.debug(">>> fetchParameters for " + templateID);
		FetchParameterGridRequest request = new FetchParameterGridRequest(userID, sessionID, templateID);
		ListResponse<ParameterGrid> response = (ListResponse<ParameterGrid>) request.sendRequest();
		logger.debug("<<< fetchParameters: " + response.getResultList().size());
		return response.getResultList();
	}

	public String getDeployErrorString(int runID) throws ServerException {
		logger.debug(">>> getDeployErrorString for " + runID);
		GetLastDeployErrorRequest request = new GetLastDeployErrorRequest(userID, sessionID, runID);
		StringResponse response = (StringResponse) request.sendRequest();
		logger.debug("<<< getDeployErrorString: " + response.getString().length());
		return response.getString();
	}

	public void lock(int entityID, GenericEntityType type) throws ServerException {
		GenericEntityResponselessActionRequest request = new GenericEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				type,
				SessionRequest.ACTION_TYPE_LOCK);
		request.sendRequest();
	}

	public void unlock(int entityID, GenericEntityType type) throws ServerException {
		GenericEntityResponselessActionRequest request = new GenericEntityResponselessActionRequest(
				userID,
				sessionID,
				entityID,
				type,
				SessionRequest.ACTION_TYPE_UNLOCK);
		request.sendRequest();
	}

	public void delete(GenericEntityCompatibilityData data) throws ServerException {
		CompatibilityResponselessActionRequest request = new CompatibilityResponselessActionRequest(
				userID,
				sessionID,
				data,
				SessionRequest.ACTION_TYPE_DELETE);
		request.sendRequest();
	}

	public List<GenericEntityCompatibilityData> fetchCompatibilityData(GenericEntityType entityType1, GenericEntityType entityType2)
			throws ServerException {
		logger.debug(">>> fetchCompatibilityData for " + entityType1 + "," + entityType2);
		FetchCompatibilityRequest request = new FetchCompatibilityRequest(userID, sessionID, entityType1, entityType2);
		ListResponse<GenericEntityCompatibilityData> response = (ListResponse<GenericEntityCompatibilityData>) request.sendRequest();
		logger.debug("<<< fetchCompatibilityData: " + response.getResultList().size());
		return response.getResultList();
	}

	public void deleteGenericCategory(int categoryType, int categoryID) throws ServerException {
		GenericCategoryResponselessActionRequest request = new GenericCategoryResponselessActionRequest(
				userID,
				sessionID,
				categoryID,
				categoryType,
				SessionRequest.ACTION_TYPE_DELETE);
		request.sendRequest();
	}

	public void replace(DateSynonym[] toBeReplaced, DateSynonym replacement) throws ServerException {
		ReplaceDateSynonymsRequest request = new ReplaceDateSynonymsRequest(userID, sessionID, toBeReplaced, replacement);
		request.sendRequest();
	}

	public boolean isInUse(DateSynonym dateSynonym) throws ServerException {
		DateSynonymInUseRequest request = new DateSynonymInUseRequest(userID, sessionID, dateSynonym);
		return ((BooleanResponse) request.sendRequest()).isTrue();
	}
}
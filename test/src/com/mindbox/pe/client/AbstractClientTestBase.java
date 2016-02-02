package com.mindbox.pe.client;

import java.awt.Cursor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DomainClass;
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
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.report.AbstractReportSpec;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;

/**
 * Client test base. Provides simple implementation of {@link com.mindbox.pe.client.MainApplication}
 * and initializes {@link com.mindbox.pe.client.ClientUtil} properly.
 * This uses {@link com.mindbox.pe.client.PELoggedAppletWithNoComm} as an instasnce of  {@link com.mindbox.pe.client.applet.PowerEditorLoggedApplet},
 * and sets its stub to an instance of {@link com.mindbox.pe.client.AppletStubForTesting}.
 * If another implementation is required for testing, overwrite {@link #createAppletInstance()}.
 * @author Geneho Kim
 * @see {@link com.mindbox.pe.client.AppletStubForTesting}
 * @see {@link com.mindbox.pe.client.PELoggedAppletWithNoComm}
 */
public abstract class AbstractClientTestBase extends AbstractTestWithTestConfig {

	private final static class CommunicatorImpl implements Communicator {
		@Override
		public void enableUser(String userID) throws ServerException {
		}

		public boolean isExistingCompatibility(GenericEntityType type1, int id1, GenericEntityType type2, int id2) throws ServerException {
			return false;
		}

		public boolean checkNameForUniqueness(EntityType entityType, String name) throws ServerException {
			return false;
		}

		public long getNextRuleID() throws ServerException {
			return 0;
		}

		public List<UserData> reloadUserData() throws ServerException {
			return null;
		}

		public String reloadConfiguration() throws ServerException {
			return null;
		}

		public List<String> fetchCustomReportNames() throws ServerException {
			return null;
		}

		public boolean hasGuidelines(int templateID) throws ServerException {
			return false;
		}

		public void deleteTemplate(int templateID, boolean deleteGuidelines) throws ServerException {
		}

		public GuidelineContext[] fetchFullContext(int templateID, GuidelineContext[] subContext) throws ServerException {
			return null;
		}

		public void updateGridContext(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts) throws ServerException {
		}

		public int makeNewVersion(int oldTemplateID, GridTemplate newVersion, DateSynonym cutoverDate) throws ServerException {
			return 0;
		}

		public List<List<GuidelineReportData>> findCutoverGuidelines(int templateID, DateSynonym cutoverDate) throws ServerException {
			return null;
		}

		public String generateReportURL(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException {
			return null;
		}

		public byte[] generatePolicySummaryReport(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines)
				throws ServerException {
			return null;
		}

		public void cloneGuidelines(int oldTemplateID, int newTemplateID) throws ServerException {
		}

		public void delete(GenericEntityCompatibilityData data) throws ServerException {
		}

		public List<GenericEntityCompatibilityData> fetchCompatibilityData(GenericEntityType entityType1, GenericEntityType entityType2)
				throws ServerException {
			return null;
		}

		public ImportResult importData(ImportSpec importSpec) throws ServerException, IOException {
			return null;
		}

		public byte[] exportDataToClient(GuidelineReportFilter filter) throws ServerException {
			return null;
		}

		public int exportDataToServer(GuidelineReportFilter filter, String filename) throws ServerException {
			return 0;
		}

		public String getDeployErrorString(int runID) throws ServerException {
			return null;
		}

		public byte[] getDomainDefintionXML() throws ServerException {
			return null;
		}

		public boolean hasDeployRule(int templateID, int columnID) throws ServerException {
			return false;
		}

		public List<ParameterGrid> fetchParameters(int templateID) throws ServerException {
			return null;
		}

		public void delete(int entityID, EntityType entityType) throws ServerException {
		}

		public void delete(int entityID, GenericEntityType type) throws ServerException {
		}

		public void deleteGenericCategory(int categoryType, int categoryID) throws ServerException {
		}

		public Persistent fetch(int entityID, EntityType entityType, boolean lockEntity) throws ServerException {
			return null;
		}

		public void lock(int entityID, EntityType entityType) throws ServerException {
		}

		public void lock(int entityID, GenericEntityType type) throws ServerException {
		}

		public int clone(GenericEntity object, boolean copyPolicies, boolean lock) throws ServerException {
			return 0;
		}

		@Override
		public int save(Persistent object, boolean lock) throws ServerException {
			return 0;
		}

		@Override
		public int save(Persistent object, boolean lock, boolean validate) throws ServerException {
			return 0;
		}

		public int cloneCaseBases(int oldCaseBaseID, String newCaseBaseName) throws ServerException {
			return 0;
		}

		public <T extends Persistent> List<T> search(SearchFilter<T> searchFilter) throws ServerException {
			return null;
		}

		public void unlock(int entityID, EntityType entityType) throws ServerException {
		}

		public void unlock(int entityID, GenericEntityType entityType) throws ServerException {
		}

		public void unlockUser(String userID) throws ServerException {
		}

		public void deleteUser(UserData user) throws ServerException {
		}

		public DeployResponse deploy(GuidelineReportFilter filter, boolean exportPolicies) throws ServerException {
			return null;
		}

		public List<GridTemplate> fetchTemplateSummaries(TemplateUsageType s) throws ServerException {
			return null;
		}

		public List<GridSummary> fetchGridSummaries(TemplateUsageType usageType, GuidelineContext[] contexts) throws ServerException {
			return null;
		}

		public GridDataResponse fetchGridData(int templateID, GuidelineContext[] contexts) throws ServerException {
			return null;
		}

		public void bulkSaveGridData(List<GuidelineReportData> data, String status, DateSynonym eff, DateSynonym exp)
				throws ServerException {
		}

		public void saveGridData(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids) throws ServerException {
		}

		public void lockUser(String userID) throws ServerException {
		}

		public void logout() throws ServerException {
		}

		public List<GenerateStats> retrieveDeployStats(int i) throws ServerException {
			return null;
		}

		public void lockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		}

		public void unlockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		}

		public void replace(DateSynonym[] toBeReplaced, DateSynonym replacement) throws ServerException {
		}

		public boolean isInUse(DateSynonym dateSynonym) throws ServerException {
			return false;
		}

		@Override
		public List<ExternalEnumSourceDetail> getEnumerationSourceDetails() throws ServerException {
			return null;
		}

		@Override
		public List<EnumValue> getAllEnumValuesFromEnumerationSource(String sourceName) throws ServerException {
			return null;
		}

		@Override
		public List<EnumValue> getApplicableEnumValuesFromEnumerationSource(String sourceName, String selectorValue) throws ServerException {
			return null;
		}

	}

	protected final static class MainAppImpl implements MainApplication {

		private final String userID;
		private final String sessionID;
		private final UserProfile userProfile;
		private final EntityConfiguration entityConfig;
		private final Communicator communicator;

		protected MainAppImpl(String userID, String sessionID, EntityConfiguration entityConfig, UserProfile userProfile) {
			this.userID = userID;
			this.sessionID = sessionID;
			this.userProfile = userProfile;
			this.entityConfig = entityConfig;
			communicator = new CommunicatorImpl();
		}

		public boolean checkPermissionByPrivilegeName(String s) {
			return true;
		}

		public boolean checkViewOrEditGuidelinePermission(GuidelineTabConfig gtConfig) {
			return true;
		}

		public boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType) {
			return true;
		}

		public boolean checkViewOrEditTemplatePermission(GuidelineTabConfig gtConfig) {
			return true;
		}

		public boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType) {
			return true;
		}

		public void handleRuntimeException(Exception ex) {
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			else {
				throw new RuntimeException(ex);
			}
		}

		public boolean confirmExit() {
			return false;
		}

		public void dispose() {
		}

		public void showTemplateEditPanel(GridTemplate template) throws CanceledException {
		}

		public Communicator getCommunicator() {
			return communicator;
		}

		public String getUserID() {
			return userID;
		}

		public String getSessionID() {
			return sessionID;
		}

		public UserProfile getUserSession() {
			return userProfile;
		}

		public EntityConfiguration getEntityConfiguration() {
			return entityConfig;
		}

		public void setCursor(Cursor cursor) {
		}

		public void setStatusMsg(String msg) {
		}

		public void reloadTemplates() throws ServerException {
		}


	}

	protected String userID;
	protected String sessionID;
	protected PowerEditorLoggedApplet peLoggedApplet;
	protected DomainRetrieverProxy domainRetrieverProxyMock;
	protected MockControl domainRetrieverProxyMockControl;

	protected AbstractClientTestBase(String name) {
		super(name);
	}

	protected PowerEditorLoggedApplet createAppletInstance() {
		AppletStubForTesting appStub = new AppletStubForTesting();
		appStub.setParameter("ssid", sessionID);
		PowerEditorLoggedApplet applet = new PELoggedAppletWithNoComm();
		applet.setStub(new AppletStubForTesting());
		return applet;
	}

	/**
	 * This sets up {@link #peLoggedApplet}. 
	 * However, this does not call any of the Applet lifecycle methods, including <code>Applet.init()</code> and <code>Applet.start()</code>.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		userID = "demo";
		sessionID = String.valueOf(System.currentTimeMillis());
		Set<com.mindbox.pe.model.admin.Privilege> privSet = new HashSet<com.mindbox.pe.model.admin.Privilege>();
		for (Iterator<com.mindbox.pe.model.admin.Privilege> iter = SecurityCacheManager.getInstance().getPrivileges(); iter.hasNext();) {
			privSet.add(iter.next());
		}
		MainApplication app = new MainAppImpl(
				userID,
				sessionID,
				ConfigurationManager.getInstance().getEntityConfiguration(),
				new UserProfile(
						userID,
						privSet,
						ConfigurationManager.getInstance().getFeatureConfiguration(),
						ConfigurationManager.getInstance().getUIConfiguration(),
						ConfigurationManager.getInstance().getCondMsgDelims(),
						ConfigurationManager.getInstance().getLDAPConnectionConfig().asUserManagementConfig(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getHistoryConfig().getLookback(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getLockoutConfig().getMaxAttempts(),
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfig().getExpirationConfig().getExpirationDays()));
		ClientUtil.getInstance().setParent(app);

		ClientUtil.getInstance().setApplet(createAppletInstance());
		this.peLoggedApplet = createAppletInstance();

		if (EntityModelCacheFactory.getInstance().getTypeEnumComboModel(TypeEnumValue.TYPE_STATUS, false, false).getSize() == 0) {
			TypeEnumValue typeEnumValue = new TypeEnumValue(1, "value", "Value");
			List<TypeEnumValue> list = new ArrayList<TypeEnumValue>();
			list.add(typeEnumValue);
			Map<String, List<TypeEnumValue>> map = new HashMap<String, List<TypeEnumValue>>();
			map.put(TypeEnumValue.TYPE_STATUS, list);
			ClientUtil.getInstance().resetCachedTypeEnumValueMap(map);
		}

		domainRetrieverProxyMockControl = MockControl.createControl(DomainRetrieverProxy.class);
		domainRetrieverProxyMock = (DomainRetrieverProxy) domainRetrieverProxyMockControl.getMock();
		domainRetrieverProxyMockControl.expectAndReturn(domainRetrieverProxyMock.fetchAllDomainClasses(), new DomainClass[0]);
		domainRetrieverProxyMockControl.replay();

		DomainModel.initInstance(domainRetrieverProxyMock);
	}

	/**
	 * Note: this does not call any of the Applet lifecycle methods, including <code>Applet.stop()</code> and <code>Applet.destroy()</code>.
	 */
	protected void tearDown() throws Exception {
		// Tear downs for AbstractClientTestBase
		if (peLoggedApplet != null) {
			peLoggedApplet.stop();
			peLoggedApplet.destroy();
			peLoggedApplet = null;
		}
		ClientUtil.getInstance().setParent(null);
		config.resetConfiguration();
		userID = null;
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
		super.tearDown();
	}
}

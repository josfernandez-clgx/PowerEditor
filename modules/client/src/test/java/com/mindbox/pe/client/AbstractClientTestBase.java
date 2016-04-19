package com.mindbox.pe.client;

import static com.mindbox.pe.client.ClientTestObjectMother.createEntityTypeDefinition;
import static com.mindbox.pe.client.ClientTestObjectMother.createUsageType;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.awt.Cursor;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.DomainRetrieverProxy;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ExternalEnumSourceDetail;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.report.AbstractReportSpec;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.EntityConfig;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.config.GuidelineTab;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;

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
public abstract class AbstractClientTestBase extends AbstractTestBase {

	private final static class CommunicatorImpl implements Communicator {

		public void bulkSaveGridData(List<GuidelineReportData> data, String status, DateSynonym eff, DateSynonym exp) throws ServerException {
		}

		public boolean checkNameForUniqueness(PeDataType entityType, String name) throws ServerException {
			return false;
		}

		@Override
		public void clearFailedLoginCounter(String userID) throws ServerException {
		}

		public int clone(GenericEntity object, boolean copyPolicies, boolean lock) throws ServerException {
			return 0;
		}

		public int cloneCaseBases(int oldCaseBaseID, String newCaseBaseName) throws ServerException {
			return 0;
		}

		public void cloneGuidelines(int oldTemplateID, int newTemplateID) throws ServerException {
		}

		public void delete(GenericEntityCompatibilityData data) throws ServerException {
		}

		public void delete(int entityID, GenericEntityType type) throws ServerException {
		}

		public void delete(int entityID, PeDataType entityType) throws ServerException {
		}

		public void deleteGenericCategory(int categoryType, int categoryID) throws ServerException {
		}

		public void deleteTemplate(int templateID, boolean deleteGuidelines) throws ServerException {
		}

		public void deleteUser(UserData user) throws ServerException {
		}

		public DeployResponse deploy(GuidelineReportFilter filter, boolean exportPolicies) throws ServerException {
			return null;
		}

		@Override
		public void enableUser(String userID) throws ServerException {
		}

		public byte[] exportDataToClient(GuidelineReportFilter filter) throws ServerException {
			return null;
		}

		public int exportDataToServer(GuidelineReportFilter filter, String filename) throws ServerException {
			return 0;
		}

		public Persistent fetch(int entityID, PeDataType entityType, boolean lockEntity) throws ServerException {
			return null;
		}

		public List<GenericEntityCompatibilityData> fetchCompatibilityData(GenericEntityType entityType1, GenericEntityType entityType2) throws ServerException {
			return null;
		}

		public List<String> fetchCustomReportNames() throws ServerException {
			return null;
		}

		public GuidelineContext[] fetchFullContext(int templateID, GuidelineContext[] subContext) throws ServerException {
			return null;
		}

		public GridDataResponse fetchGridData(int templateID, GuidelineContext[] contexts) throws ServerException {
			return null;
		}

		public List<GridSummary> fetchGridSummaries(TemplateUsageType usageType, GuidelineContext[] contexts) throws ServerException {
			return null;
		}

		public List<ParameterGrid> fetchParameters(int templateID) throws ServerException {
			return null;
		}

		public List<GridTemplate> fetchTemplateSummaries(TemplateUsageType s) throws ServerException {
			return null;
		}

		public List<List<GuidelineReportData>> findCutoverGuidelines(int templateID, DateSynonym cutoverDate) throws ServerException {
			return null;
		}

		public byte[] generatePolicySummaryReport(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException {
			return null;
		}

		public String generateReportURL(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelines) throws ServerException {
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

		public String getDeployErrorString(int runID) throws ServerException {
			return null;
		}

		public byte[] getDomainDefintionXML() throws ServerException {
			return null;
		}

		@Override
		public List<ExternalEnumSourceDetail> getEnumerationSourceDetails() throws ServerException {
			return null;
		}

		public long getNextRuleID() throws ServerException {
			return 0;
		}

		public boolean hasDeployRule(int templateID, int columnID) throws ServerException {
			return false;
		}

		public boolean hasGuidelines(int templateID) throws ServerException {
			return false;
		}

		public ImportResult importData(ImportSpec importSpec) throws ServerException, IOException {
			return null;
		}

		public boolean isExistingCompatibility(GenericEntityType type1, int id1, GenericEntityType type2, int id2) throws ServerException {
			return false;
		}

		public boolean isInUse(DateSynonym dateSynonym) throws ServerException {
			return false;
		}

		public void lock(int entityID, GenericEntityType type) throws ServerException {
		}

		public void lock(int entityID, PeDataType entityType) throws ServerException {
		}

		public void lockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		}

		public void lockUser(String userID) throws ServerException {
		}

		public void logout() throws ServerException {
		}

		public int makeNewVersion(int oldTemplateID, GridTemplate newVersion, DateSynonym cutoverDate, List<GuidelineReportData> guidelinesToCutOver) throws ServerException {
			return 0;
		}

		public String reloadConfiguration() throws ServerException {
			return null;
		}

		public List<UserData> reloadUserData() throws ServerException {
			return null;
		}

		public void replace(DateSynonym[] toBeReplaced, DateSynonym replacement) throws ServerException {
		}

		public GenerateStats retrieveDeployStats(int i) throws ServerException {
			return null;
		}

		@Override
		public int save(Persistent object, boolean lock) throws ServerException {
			return 0;
		}

		@Override
		public int save(Persistent object, boolean lock, boolean validate) throws ServerException {
			return 0;
		}

		public void saveGridData(int templateID, List<ProductGrid> grids, List<ProductGrid> removedGrids) throws ServerException {
		}

		public <T extends Persistent> List<T> search(SearchFilter<T> searchFilter) throws ServerException {
			return null;
		}

		public void unlock(int entityID, GenericEntityType entityType) throws ServerException {
		}

		public void unlock(int entityID, PeDataType entityType) throws ServerException {
		}

		public void unlockGrid(int templateID, GuidelineContext[] contexts) throws ServerException {
		}

		public void unlockUser(String userID) throws ServerException {
		}

		public void updateGridContext(int templateID, List<ProductGrid> grids, GuidelineContext[] newContexts) throws ServerException {
		}

		@Override
		public List<GuidelineReportData> validateDateSynonymDateChange(int dateSynonymId, Date newDate) throws ServerException {
			return new ArrayList<GuidelineReportData>();
		}
	}

	protected final static class MainAppImpl implements MainApplication {

		private final String userID;
		private final String sessionID;
		private final UserProfile userProfile;
		private final EntityConfigHelper entityConfig;
		private final Communicator communicator;

		protected MainAppImpl(String userID, String sessionID, EntityConfigHelper entityConfig, UserProfile userProfile) {
			this.userID = userID;
			this.sessionID = sessionID;
			this.userProfile = userProfile;
			this.entityConfig = entityConfig;
			communicator = new CommunicatorImpl();
		}

		public boolean checkPermissionByPrivilegeName(String s) {
			return true;
		}

		public boolean checkViewOrEditGuidelinePermission(GuidelineTab gtConfig) {
			return true;
		}

		public boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType) {
			return true;
		}

		public boolean checkViewOrEditTemplatePermission(GuidelineTab gtConfig) {
			return true;
		}

		public boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType) {
			return true;
		}

		public boolean confirmExit() {
			return false;
		}

		public void dispose() {
		}

		public Communicator getCommunicator() {
			return communicator;
		}

		public EntityConfigHelper getEntityConfiguration() {
			return entityConfig;
		}

		public String getSessionID() {
			return sessionID;
		}

		public String getUserID() {
			return userID;
		}

		public UserProfile getUserSession() {
			return userProfile;
		}

		public void handleRuntimeException(Exception ex) {
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			else {
				throw new RuntimeException(ex);
			}
		}

		public void reloadTemplates() throws ServerException {
		}

		public void setCursor(Cursor cursor) {
		}

		public void setStatusMsg(String msg) {
		}

		public void showTemplateEditPanel(GridTemplate template) throws CanceledException {
		}
	}

	protected String userID;
	protected String sessionID;
	protected PowerEditorLoggedApplet peLoggedApplet;
	protected DomainRetrieverProxy domainRetrieverProxyMock;
	protected EntityType entityTypeDefinition1;
	protected EntityType entityTypeDefinition2;
	protected EntityType entityTypeDefinition3;
	protected GenericEntityType entityType1;
	protected GenericEntityType entityType2;
	protected GenericEntityType entityType3;
	protected TemplateUsageType templateUsageType;

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
	@Before
	public void setUp() throws Exception {
		userID = "demo";
		sessionID = String.valueOf(System.currentTimeMillis());

		templateUsageType = createUsageType();
		TemplateUsageType.createInstance(templateUsageType.toString(), templateUsageType.getDisplayName(), templateUsageType.getPrivilege());

		entityTypeDefinition1 = createEntityTypeDefinition(1, 1);
		entityTypeDefinition1.setUseInContext(Boolean.TRUE);
		entityType1 = GenericEntityType.makeInstance(entityTypeDefinition1);
		entityTypeDefinition2 = createEntityTypeDefinition(2, 2);
		entityTypeDefinition2.setUseInContext(Boolean.TRUE);
		entityType2 = GenericEntityType.makeInstance(entityTypeDefinition2);
		entityTypeDefinition3 = createEntityTypeDefinition(3, 3);
		entityTypeDefinition3.setUseInContext(Boolean.TRUE);
		entityType3 = GenericEntityType.makeInstance(entityTypeDefinition3);

		final EntityConfig entityConfig = new EntityConfig();
		entityConfig.getEntityType().add(entityTypeDefinition1);
		entityConfig.getEntityType().add(entityTypeDefinition2);
		entityConfig.getEntityType().add(entityTypeDefinition3);

		CategoryType categoryType = new CategoryType();
		categoryType.setName(entityTypeDefinition1.getName() + " category");
		categoryType.setShowInSelectionTable(Boolean.TRUE);
		categoryType.setTypeID(entityTypeDefinition1.getCategoryType().intValue());
		entityConfig.getCategoryType().add(categoryType);

		categoryType = new CategoryType();
		categoryType.setName(entityTypeDefinition2.getName() + " category");
		categoryType.setShowInSelectionTable(Boolean.TRUE);
		categoryType.setTypeID(entityTypeDefinition2.getCategoryType().intValue());
		entityConfig.getCategoryType().add(categoryType);

		categoryType = new CategoryType();
		categoryType.setName(entityTypeDefinition3.getName() + " category");
		categoryType.setShowInSelectionTable(Boolean.TRUE);
		categoryType.setTypeID(entityTypeDefinition3.getCategoryType().intValue());
		entityConfig.getCategoryType().add(categoryType);

		final UserManagementConfig userManagementConfig = new UserManagementConfig();

		final PowerEditorConfiguration powerEditorConfiguration = XmlUtil.unmarshal(new FileReader("src/test/data/PowerEditorConfiguration.xml"), PowerEditorConfiguration.class);
		powerEditorConfiguration.setEntityConfig(entityConfig);

		ClientUtil.resetPowerEditorConfiguration(powerEditorConfiguration, userManagementConfig);

		final UserProfile userProfile = new UserProfile(userID, new HashSet<Privilege>(), new HashMap<Object, MessageConfiguration>(), 3, 3, 365, null, 365, false);

		final MainApplication app = new MainAppImpl(userID, sessionID, ClientUtil.getEntityConfigHelper(), userProfile);

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

		domainRetrieverProxyMock = createMock(DomainRetrieverProxy.class);
		expect(domainRetrieverProxyMock.fetchAllDomainClasses()).andReturn(new DomainClass[0]);
		replay(domainRetrieverProxyMock);

		DomainModel.initInstance(domainRetrieverProxyMock);
	}

	@After
	public void tearDown() throws Exception {
		// Tear downs for AbstractClientTestBase
		if (peLoggedApplet != null) {
			peLoggedApplet.stop();
			peLoggedApplet.destroy();
			peLoggedApplet = null;
		}
		ClientUtil.getInstance().setParent(null);
		userID = null;
		ReflectionUtil.setPrivate(DomainModel.class, "instance", null);
		ReflectionUtil.setPrivate(ClientUtil.class, "configHelper", null);
	}
}

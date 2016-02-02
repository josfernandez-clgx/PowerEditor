package com.mindbox.pe.server.imexport;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.CBRAttributeType;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseFactory;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.Grid;
import com.mindbox.pe.server.model.User;

public class ImportServiceTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ImportServiceTest Tests");
		suite.addTestSuite(ImportServiceTest.class);
		return suite;
	}

	private GenericCategory rootCategory;
	private ImportService importService;

	public ImportServiceTest(String name) {
		super(name);
	}

	public void testImportDataXMLWithEntitiesOnlySetRejectsNonEntityElements() throws Exception {
		ImportSpec importSpec = new ImportSpec(
				"somefile.xml",
				"<powereditor-data><next-id-data><next-id id=\"10\"/></next-id-data></powereditor-data>",
				ImportSpec.IMPORT_DATA_REQUEST,
				false);
		assertThrowsException(importService, "importDataXML", new Class[] {
				ImportSpec.class,
				boolean.class,
				com.mindbox.pe.server.model.User.class }, new Object[] { importSpec, Boolean.TRUE, null }, ImportException.class);
	}

	public void testBuildEntityIDMapForEntityOfCategoryTypeWithNoParentIDUsesRootCategoryFromCache() throws Exception {
		DigestedObjectHolder objectHolder = ObjectMother.attachEntities(ObjectMother.createDigestedObjectHolder(), "category", 1);
		String expectedKey = ObjectConverter.asCategoryIDMapKey("product", objectHolder.getObjects(Entity.class).get(0).getId());
		Map<String, Integer> idMap = invokeBuildEntityIDMapWithNewMap(objectHolder);
		assertEquals(1, idMap.size());
		assertEquals(rootCategory.getID(), idMap.get(expectedKey).intValue());
	}

	/**
	 * This test requires that test/config/PowerEditorConfiguration.xml defines an entity named
	 * 'nocategory' which has no category defined for it.
	 */
	public void testBuildEntityIDMapForNonMergeWithCategoryWithNoCategoryDefIsNoOp() throws Exception {
		// sanity check
		GenericEntityType type = GenericEntityType.forName("nocategory");
		assertNotNull(type);
		assertFalse(type.hasCategory());

		CategoryDigest categoryDigest = new CategoryDigest();
		categoryDigest.setId(1);
		categoryDigest.setType(type.getName());
		categoryDigest.setParentID(-1);

		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		objectHolder.addObject(categoryDigest);

		Map<String, Integer> map = new HashMap<String, Integer>();
		invokeBuildEntityIDMapForNonMerge(objectHolder, map);
		assertTrue(map.isEmpty());
	}

	private Map<String, Integer> invokeBuildEntityIDMapWithNewMap(DigestedObjectHolder objectHolder) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		invokeBuildEntityIDMap(objectHolder, map);
		return map;
	}

	private void invokeBuildEntityIDMap(DigestedObjectHolder objectHolder, Map<String, Integer> entityIDMap) {
		ReflectionUtil.executePrivate(
				importService,
				"buildEntityIDMap",
				new Class[] { DigestedObjectHolder.class, Map.class },
				new Object[] { objectHolder, entityIDMap });
	}

	private void invokeBuildEntityIDMapForNonMerge(DigestedObjectHolder objectHolder, Map<String, Integer> entityIDMap) {
		ReflectionUtil.executePrivate(new ImportService(), "buildEntityIDMapForNonMerge", new Class[] {
				DigestedObjectHolder.class,
				Map.class }, new Object[] { objectHolder, entityIDMap });
	}

	public void testFilterOutDataForDisabledFeaturesForCBR() throws Exception {
		String filename = "filename";
		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		objectHolder.addObject(new CBRCase("name", "desc"));
		objectHolder.addObject(new CBRAttributeType(1, "symbol", "displayName"));
		objectHolder.addObject(new Entity());

		Map<String, DigestedObjectHolder> objectHolderMap = new HashMap<String, DigestedObjectHolder>();
		objectHolderMap.put(filename, objectHolder);

		ConfigurationManager.getInstance().getFeatureConfiguration().getFeatureConfig(FeatureConfiguration.CBR_FEATURE).setEnable(false);
		resetImportService();
		invokeFilterOutDataForDisableFeatures(objectHolderMap);

		ImportResult importResult = importService.getImportResult();
		assertEquals(1, importResult.getErrorMessages().size());
		assertTrue(objectHolder.getObjects(CBRCase.class).isEmpty());
		assertTrue(objectHolder.getObjects(CBRAttributeType.class).isEmpty());
		assertFalse(objectHolder.getObjects(Entity.class).isEmpty());
	}

	public void testFilterOutDataForDisabledFeaturesForPhase() throws Exception {
		String filename = "filename";
		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		objectHolder.addObject(new ProcessRequest(1, "name", "desc"));
		objectHolder.addObject(PhaseFactory.createPhase(PhaseFactory.TYPE_SEQUENCE, 1, "name", "displayName"));
		objectHolder.addObject(new Entity());

		Map<String, DigestedObjectHolder> objectHolderMap = new HashMap<String, DigestedObjectHolder>();
		objectHolderMap.put(filename, objectHolder);

		ConfigurationManager.getInstance().getFeatureConfiguration().getFeatureConfig(FeatureConfiguration.PHASE_FEATURE).setEnable(false);
		resetImportService();
		invokeFilterOutDataForDisableFeatures(objectHolderMap);

		ImportResult importResult = importService.getImportResult();
		assertEquals(1, importResult.getErrorMessages().size());
		assertTrue(objectHolder.getObjects(ProcessRequest.class).isEmpty());
		assertTrue(objectHolder.getObjects(Phase.class).isEmpty());
		assertFalse(objectHolder.getObjects(Entity.class).isEmpty());
	}

	public void testFilterOutDataForDisabledFeaturesForParameter() throws Exception {
		String filename = "filename";
		DigestedObjectHolder objectHolder = new DigestedObjectHolder();
		objectHolder.addObject(new ParameterTemplate(1, "name", 1, "desc"));
		Grid grid = new Grid();
		grid.setTemplateID(1);
		grid.setType(Grid.GRID_TYPE_PARAMETER);
		objectHolder.addObject(grid);
		grid = new Grid();
		grid.setTemplateID(2);
		grid.setType(Grid.GRID_TYPE_GUIDELINE);
		objectHolder.addObject(grid);
		objectHolder.addObject(new Entity());

		Map<String, DigestedObjectHolder> objectHolderMap = new HashMap<String, DigestedObjectHolder>();
		objectHolderMap.put(filename, objectHolder);

		ConfigurationManager.getInstance().getFeatureConfiguration().getFeatureConfig(FeatureConfiguration.PARAMETER_FEATURE).setEnable(false);
		resetImportService();
		invokeFilterOutDataForDisableFeatures(objectHolderMap);

		ImportResult importResult = importService.getImportResult();
		assertEquals(1, importResult.getErrorMessages().size());
		assertTrue(objectHolder.getObjects(CBRCase.class).isEmpty());
		assertEquals(1, objectHolder.getObjects(Grid.class).size());
		assertFalse(objectHolder.getObjects(Entity.class).isEmpty());
	}

	private void invokeFilterOutDataForDisableFeatures(Map<String, DigestedObjectHolder> objectHolderMap) {
		ReflectionUtil.executePrivate(
				importService,
				"filterOutDataForDisabledFeatures",
				new Class[] { Map.class },
				new Object[] { objectHolderMap });
	}

	private void resetImportService() throws Exception {
		User user = ObjectMother.createUser();
		ReflectionUtil.executePrivate(importService, "reset", new Class[] { user.getClass() }, new Object[] { user });
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		// Set up for ImportServiceTest
		rootCategory = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(rootCategory.getType(), rootCategory.getId(), rootCategory.getName());
		importService = new ImportService();
	}

	protected void tearDown() throws Exception {
		// Tear downs for ImportServiceTest
		EntityManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}
}

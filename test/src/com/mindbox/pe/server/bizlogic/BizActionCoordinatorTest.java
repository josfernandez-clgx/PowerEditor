package com.mindbox.pe.server.bizlogic;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;

public class BizActionCoordinatorTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("BizActionCoordinatorTest Tests");
		suite.addTestSuite(BizActionCoordinatorTest.class);
		return suite;
	}

	public BizActionCoordinatorTest(String name) {
		super(name);
	}

	// TODO [GKIM] Merge with 6.0 branch

	public void testIsUniqueTemplateNameAndVersion() throws Exception {
		String name = ObjectMother.createString();
		String version = ObjectMother.createString();
		GridTemplate gridTemplate = new GridTemplate(1, name, TemplateUsageType.getAllInstances()[0]);
		gridTemplate.setVersion(version);


		GuidelineTemplateManager.getInstance().addTemplate(gridTemplate);

		assertTrue(BizActionCoordinator.getInstance().isUniqueTemplateNameAndVersion(name, version));
	}

	/**
	 * TODO: Complete implementation once framework is in place 
	 *       to unit test methods involving persistance.
	 * @throws Exception
	 */
	public void testSaveExistingEntity() throws Exception {
		// uncomment when ready to test
		//ActionTypeDefinition actionTypeDefinition = new ActionTypeDefinition();        
		//BizActionCoordinator.getInstance().save(actionTypeDefinition, user);
	}

	/**
	 * TODO: Complete implementation once framework is in place 
	 *       to unit test methods involving persistance.
	 * @throws Exception
	 */
	public void testimportDateSynonym() throws Exception {
		// uncomment when ready to test        
		//DateSynonym dateSynonym = new DateSynonym(100, "date1", "", new Date());
		//BizActionCoordinator.getInstance().importDateSynonym(dateSynonym, merge, user)         
	}

	public void testExtractIDsWithNullReturnsNull() throws Exception {
		assertNull(BizActionCoordinator.extractIDs(null));
	}

	public void testExtractIDsWithEmptyArrayReturnsEmptyArray() throws Exception {
		assertEquals(0, BizActionCoordinator.extractIDs(new Persistent[0]).length);
	}

	public void testExtractIDsWithValidArrayReturnsCorrectIDs() throws Exception {
		assertEquals(
				new int[] { 1, 2, 3 },
				BizActionCoordinator.extractIDs(new Persistent[] {
						new SimpleEntityData(1, "name1"),
						new SimpleEntityData(3, "name3"),
						new SimpleEntityData(2, "name2") }));
	}

	// TODO: Complete implementation once framework is in place to unit test methods involving persistance.
	public void testImportDataReloadsCache() throws Exception {
	}

	public void testIsDateSynonymInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(
				BizActionCoordinator.getInstance(),
				"isDateSynonymInUse",
				new Class[] { DateSynonym.class });
	}

	public void testIsDateSynonymInUsePositiveCase() throws Exception {
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");
		ParameterGrid grid = ParameterManager.getInstance().getGrid(1000);
		DateSynonym ds = ObjectMother.createDateSynonym();
		grid.setEffectiveDate(ds);
		grid.setExpirationDate(null);
		assertTrue(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));

		grid.setEffectiveDate(null);

		ProductGrid productGrid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		GridManager.getInstance().addProductGrid(productGrid);
		productGrid.setEffectiveDate(ds);
		assertTrue(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));
	}

	public void testIsDateSynonymInUseNegativeCase() throws Exception {
		ProductGrid productGrid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		GridManager.getInstance().addProductGrid(productGrid);
		ParameterManager.getInstance().addParameterGrid(1000, 1, "", 0, null, null, "Draft");

		DateSynonym ds = ObjectMother.createDateSynonym();
		assertFalse(BizActionCoordinator.getInstance().isDateSynonymInUse(ds));
	}


	protected void setUp() throws Exception {
		super.setUp();
		ParameterTemplateManager.getInstance().addParameterTemplate(new ParameterTemplate(1, "param template", 99, "desc"));
		config.initServer("test/config/PowerEditorConfiguration-NoProgram.xml");
	}

	protected void tearDown() throws Exception {
		// Tear downs for BizActionCoordinatorTest
		ParameterTemplateManager.getInstance().removeFromCache(1);
		ParameterManager.getInstance().startLoading();
		GridManager.getInstance().startLoading();
		super.tearDown();
		config.resetConfiguration();
	}

	/**
	 * TODO: Complete implementation once framework is in place 
	 *       to unit test methods involving persistance.
	 * @throws Exception
	 */
	public void testDeleteGenericEntityCompatibilityData() throws Exception {
		// create an instance of GenericEntityCompatibilityData, save it, try
		// to delete it by calling BizActionCoordinator.delete(compData, user)
		// with a copy of the GenericEntityCompatibilityData with the CompatibilityKey order reversed
	}


}

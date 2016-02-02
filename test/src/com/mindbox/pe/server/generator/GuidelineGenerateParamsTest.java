package com.mindbox.pe.server.generator;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;


public class GuidelineGenerateParamsTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineGenerateParamsTest Tests");
		suite.addTestSuite(GuidelineGenerateParamsTest.class);
		return suite;
	}

	public GuidelineGenerateParamsTest(String name) {
		super(name);
	}

	public void testHasGenericCategoryAsCellValuePositiveCase() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		ObjectMother.attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		grid.setValue(1, grid.getTemplate().getColumn(1).getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 1));
		grid.setValue(1, grid.getTemplate().getColumn(2).getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 2));
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(null, null, grid, -1, 1);
		assertTrue(generateParams.hasGenericCategoryAsCellValue());
	}
	
	public void testHasGenericCategoryAsCellValueNegativeCase() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setNumRows(1);
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(null, null, grid, -1, 1);
		assertFalse(generateParams.hasGenericCategoryAsCellValue());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

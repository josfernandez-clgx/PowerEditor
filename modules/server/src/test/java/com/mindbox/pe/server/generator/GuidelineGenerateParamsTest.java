package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.template.GridTemplate;

public class GuidelineGenerateParamsTest extends com.mindbox.pe.server.AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testHasGenericCategoryAsCellValueNegativeCase() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		grid.setNumRows(1);
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(null, null, grid, -1, 1);
		assertFalse(generateParams.hasGenericCategoryAsCellValue());
	}

	@Test
	public void testHasGenericCategoryAsCellValuePositiveCase() throws Exception {
		ProductGrid grid = createGuidelineGrid(createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		attachGridTemplateColumns((GridTemplate) grid.getTemplate(), 2);
		grid.setNumRows(1);
		grid.setValue(1, grid.getTemplate().getColumn(1).getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 1));
		grid.setValue(1, grid.getTemplate().getColumn(2).getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 2));
		GuidelineGenerateParams generateParams = new GuidelineGenerateParams(null, null, grid, -1, 1);
		assertTrue(generateParams.hasGenericCategoryAsCellValue());
	}
}

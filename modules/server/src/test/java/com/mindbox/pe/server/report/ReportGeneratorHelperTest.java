package com.mindbox.pe.server.report;

import static com.mindbox.pe.server.ServerTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createGenericEntity;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;

public class ReportGeneratorHelperTest extends AbstractTestWithTestConfig {

	private ReportGeneratorHelper helper;
	private GridTemplate template;
	private ProductGrid grid;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		template = createGridTemplate(TemplateUsageType.getAllInstances()[0]);
		GuidelineTemplateManager.getInstance().addTemplate(template);
		grid = createGuidelineGrid(template);
		GridManager.getInstance().addProductGrid(grid);
	}

	@After
	public void tearDown() throws Exception {
		GuidelineTemplateManager.getInstance().removeFromCache(template.getID());
		GridManager.getInstance().removeFromCache(grid);
		EntityManager.getInstance().startLoading();
		helper = null;
		template = null;
		grid = null;
		super.tearDown();
	}

	@Test
	public void testGetActivationsFilterByDate() throws Exception {
		String dateStr = "8/23/2006 11:01:31";
		Date date = null;

		try {
			date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateStr);
		}
		catch (ParseException e) {
		}

		grid.setEffectiveDate(DateSynonym.createUnnamedInstance(date));
		helper = new ReportGeneratorHelper(null, null, null, null, null, null, null, null, null, null, dateStr);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		List<AbstractGuidelineGrid> activations = helper.getActivations();
		assertNotNull(activations);
		assertTrue(activations.size() == 1);

		try {
			date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse("8/23/2020 11:01:31");
		}
		catch (ParseException e) {
		}
		grid.setEffectiveDate(DateSynonym.createUnnamedInstance(date));
		activations = helper.getActivations();
		assertTrue(activations.size() == 0);
	}

	@Test
	public void testGetActivationsFilterByStatus() throws Exception {
		grid.setStatus("Draft");
		helper = new ReportGeneratorHelper(null, null, null, null, null, null, null, null, null, "Draft", null);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		List<AbstractGuidelineGrid> activations = helper.getActivations();
		assertNotNull(activations);
		assertTrue(activations.size() == 1);

		helper = new ReportGeneratorHelper(null, null, null, null, null, null, null, null, null, "Prod", null);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		assertTrue(helper.getErrorMessages().size() == 1);
	}

	@Test
	public void testGetActivationsFilterByUsageType() throws Exception {
		helper = new ReportGeneratorHelper(null, null, template.getUsageType().toString(), null, null, null, null, null, null, null, null);
		List<GridTemplate> templates = helper.getTemplateList();
		assertTrue(templates.size() == 1);

		helper = new ReportGeneratorHelper(null, null, TemplateUsageType.getAllInstances()[1].toString(), null, null, null, null, null, null, null, null);
		templates = helper.getTemplateList();
		assertTrue(templates.size() == 0);
	}

	@Test
	public void testGetActivationsFilterForContextWithEntityColumnsDontIncludeEmptyHappyCase() throws Exception {
		GridTemplateColumn entityColumn = createGridTemplateColumn(1, createUsageType());
		entityColumn.setDataSpecDigest(createEntityColumnDataSpecDigest("product", true, true, false));
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(2, createUsageType());
		entityColumn.setDataSpecDigest(createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(3, createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		grid.setNumRows(3);
		grid.setValue(1, 1, new CategoryOrEntityValue(type, true, entityID + 1));
		grid.setValue(1, 3, new CategoryOrEntityValue(type, true, entityID + 2));
		grid.setValue(2, 1, new CategoryOrEntityValue(type, true, entityID + 3));
		grid.setValue(2, 3, new CategoryOrEntityValue(type, true, entityID));
		grid.setValue(3, 1, new CategoryOrEntityValue(type, true, entityID + 4));
		grid.setValue(3, 3, null);

		// test with two columns
		helper = new ReportGeneratorHelper(null, null, null, null, "product:entity:" + entity.getName(), null, null, "true", "false", null, null);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		List<AbstractGuidelineGrid> activations = helper.getActivations();
		assertEquals(1, activations.size());
		//assertEquals(2, activations.get(0).getNumRows());
		Object[][] dataObject = activations.get(0).getDataObjects();
		assertEquals(1, dataObject.length);
	}

	@Test
	public void testGetActivationsFilterForContextWithEntityColumnsIncludeEmptyHappyCase() throws Exception {
		GridTemplateColumn entityColumn = createGridTemplateColumn(1, createUsageType());
		entityColumn.setDataSpecDigest(createEntityColumnDataSpecDigest("product", true, true, false));
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(2, createUsageType());
		entityColumn.setDataSpecDigest(createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(3, createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		grid.setNumRows(3);
		grid.setValue(1, 1, new CategoryOrEntityValue(type, true, entityID + 1));
		grid.setValue(1, 3, new CategoryOrEntityValue(type, true, entityID + 2));
		grid.setValue(2, 1, new CategoryOrEntityValue(type, true, entityID + 3));
		grid.setValue(2, 3, new CategoryOrEntityValue(type, true, entityID));
		grid.setValue(3, 1, new CategoryOrEntityValue(type, true, entityID + 4));
		grid.setValue(3, 3, null);

		// test with two columns
		helper = new ReportGeneratorHelper(null, null, null, null, "product:entity:" + entity.getName(), null, null, "true", "true", null, null);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		List<AbstractGuidelineGrid> activations = helper.getActivations();
		assertEquals(1, activations.size());
		//assertEquals(2, activations.get(0).getNumRows());
		Object[][] dataObject = activations.get(0).getDataObjects();
		assertEquals(2, dataObject.length);
	}

	@Test
	public void testGetActivationsFilterForContextWithNoEntityColumnsHappyCase() throws Exception {
		GridTemplateColumn entityColumn = createGridTemplateColumn(1, createUsageType());
		entityColumn.setDataSpecDigest(createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(2, createUsageType());
		entityColumn.setDataSpecDigest(createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = createGridTemplateColumn(3, createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		grid.setNumRows(3);

		// test with two columns
		helper = new ReportGeneratorHelper(null, null, null, null, "product:entity:" + entity.getName(), null, null, "true", "true", null, null);
		helper.setCurrentTemplate((GridTemplate) helper.getTemplateList().get(0));
		List<AbstractGuidelineGrid> activations = helper.getActivations();
		assertEquals(1, activations.size());
		//assertEquals(2, activations.get(0).getNumRows());
		Object[][] dataObject = activations.get(0).getDataObjects();
		assertEquals(3, dataObject.length);
	}

	@Test
	public void testGetActivationsFilterNoFilterDoNotReturnTemplateWithNoGrids() throws Exception {
		GridManager.getInstance().removeGuidelinesForTemplate(template.getID());
		helper = new ReportGeneratorHelper(null, null, null, null, null, null, null, null, null, null, null);
		List<GridTemplate> templates = helper.getTemplateList();
		assertNotNull(templates);
		assertEquals(0, templates.size());
	}

	@Test
	public void testGetActivationsFilterNoFilterHappyCase() throws Exception {
		// sanity check
		assertTrue(GridManager.getInstance().hasGrids(template.getID()));

		helper = new ReportGeneratorHelper(null, null, null, null, null, null, null, null, null, null, null);
		List<GridTemplate> templates = helper.getTemplateList();
		assertNotNull(templates);
		assertEquals(1, templates.size());
	}
}

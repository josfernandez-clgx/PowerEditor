package com.mindbox.pe.server;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static com.mindbox.pe.unittest.UnitTestHelper.assertContains;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.model.GenericCategoryIdentity;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.GridCellDetail;

public class ContextUtilTest extends AbstractTestWithTestConfig {

	private ProductGrid grid1;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		grid1 = new ProductGrid(1, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid1.addGenericEntityID(GenericEntityType.forName("product"), 2000);
		grid1.addGenericCategoryID(GenericEntityType.forName("channel"), 3000);
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testAddContextAddsContextToEmptyGrid() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });
		ServerContextUtil.addContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

	@Test
	public void testAddContextAddsIDsToExistingContext() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 1, 2, 3 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 2, 3, 4, 5 });
		ServerContextUtil.addContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 100, 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

	@Test
	public void testAddContextWithNullContextIsNoOp() throws Exception {
		ProductGrid grid = createGuidelineGrid(TemplateUsageType.getAllInstances()[0]);
		ServerContextUtil.addContext(grid, null);
	}

	@Test(expected = NullPointerException.class)
	public void testAddContextWithNullGridThrowsNullPointerException() throws Exception {
		ServerContextUtil.addContext(null, null);
	}

	@Test
	public void testCategoriesContained() {
		GuidelineContext c = new GuidelineContext(10);
		c.setIDs(new int[] { 1, 2, 3 });
		assertTrue(ServerContextUtil.entitiesContained(c, new int[] { 1, 2, 3 }, false, false));
		assertTrue(ServerContextUtil.entitiesContained(c, new int[] { 1, 2, 3, 4, 5 }, true, true));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 2, 3, 4, 5 }, false, false));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 1, 3, 4, 5 }, true, true));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 1, 2 }, true, true));
	}

	@Test
	public void testContainsContextWithArrays() {
		GuidelineContext c1, c2, c3;
		c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c2 = new GuidelineContext(GenericEntityType.forName("product"));
		c3 = new GuidelineContext(GenericEntityType.forName("product"));

		GuidelineContext[] container = new GuidelineContext[] { c1 };
		GuidelineContext[] contexts = new GuidelineContext[] { c2 };

		c1.setIDs(new int[] { 10, 20, 30 });
		c2.setIDs(new int[] { 9000 });
		assertFalse(ContextUtil.containsContext(container, contexts));

		container = new GuidelineContext[] { c2 };
		contexts = new GuidelineContext[] { c3 };
		// same types, no match
		c2.setIDs(new int[] { 10, 20, 30 });
		c3.setIDs(new int[] { 9000 });
		assertFalse(ContextUtil.containsContext(container, contexts));

		// same types, match
		c2.setIDs(new int[] { 10, 20, 30 });
		c3.setIDs(new int[] { 10 });
		assertTrue(ContextUtil.containsContext(container, contexts));

		// same types, no match (context contains container)
		c2.setIDs(new int[] { 10, 20 });
		contexts = new GuidelineContext[] { c3 };
		c3.setIDs(new int[] { 10, 20, 30 });
		assertFalse(ContextUtil.containsContext(container, contexts));

		// same types, match (container context plus other contexts
		container = new GuidelineContext[] { c1, c2 };
		c2.setIDs(new int[] { 10, 20 });
		contexts = new GuidelineContext[] { c3 };
		c3.setIDs(new int[] { 10, 20, 30 });
		assertFalse(ContextUtil.containsContext(container, contexts));

	}

	@Test
	public void testContainsContextWithoutArray() {
		GuidelineContext c1, c2, c3;
		c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c2 = new GuidelineContext(GenericEntityType.forName("product"));
		c3 = new GuidelineContext(GenericEntityType.forName("product"));

		GuidelineContext[] container = new GuidelineContext[] { c1 };
		c1.setIDs(new int[] { 10, 20, 30 });
		c2.setIDs(new int[] { 9000 });
		assertFalse(ContextUtil.containsContext(container, c2));

		container = new GuidelineContext[] { c2 };
		// same types, no match
		c2.setIDs(new int[] { 10, 20, 30 });
		c3.setIDs(new int[] { 9000 });
		assertFalse(ContextUtil.containsContext(container, c3));

		// same types, match
		c2.setIDs(new int[] { 10, 20, 30 });
		c3.setIDs(new int[] { 10 });
		assertTrue(ContextUtil.containsContext(container, c3));

		// same types, no match (context contains container)
		c2.setIDs(new int[] { 10, 20 });
		c3.setIDs(new int[] { 10, 20, 30 });
		assertFalse(ContextUtil.containsContext(container, c3));

		// same types, match (container context plus other contexts
		container = new GuidelineContext[] { c1, c2 };
		c2.setIDs(new int[] { 10, 20 });
		c3.setIDs(new int[] { 10, 20, 30 });
		assertFalse(ContextUtil.containsContext(container, c3));

	}

	@Test
	public void testContextContainsCategory() {
		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("product"));
		assertFalse(ContextUtil.contextContainsCategory(c));
		c = new GuidelineContext(10);
		assertTrue(ContextUtil.contextContainsCategory(c));
	}

	@Test
	public void testContextContainsEntity() {
		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("product"));
		assertTrue(ContextUtil.contextContainsEntity(c));
		c = new GuidelineContext(10);
		assertFalse(ContextUtil.contextContainsEntity(c));
	}

	@Test
	public void testEntitiesContained() {
		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("product"));
		c.setIDs(new int[] { 1, 2, 3 });
		assertTrue(ServerContextUtil.entitiesContained(c, new int[] { 1, 2, 3 }, false, false));
		assertTrue(ServerContextUtil.entitiesContained(c, new int[] { 1, 2, 3, 4, 5 }, true, true));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 2, 3, 4, 5 }, false, false));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 1, 3, 4, 5 }, true, true));
		assertFalse(ServerContextUtil.entitiesContained(c, new int[] { 1, 2 }, true, true));
	}

	@Test
	public void testExtractCategoryIdentitiesProdGridDoesNotSupportEntities() throws Exception {
		grid1.addGenericEntityID(GenericEntityType.forName("channel"), 30);
		assertArrayEquals(new GenericEntityIdentity[0], ServerContextUtil.extractGenericCategoryIdentities(grid1, GenericEntityType.forName("product").getID()));
	}

	@Test
	public void testExtractCategoryIdentitiesProdGridWithNullGridThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.extractGenericCategoryIdentities(null, 1);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testExtractCategoryIdentitiesProdGridWithUnusedEntityTypeReturnsEmptyArray() throws Exception {
		assertEquals(0, ServerContextUtil.extractGenericCategoryIdentities(grid1, 777).length);
	}

	@Test
	public void testExtractCategoryIdentitiesProdGridWithValidEntityTypeReturnsCorrectObject() throws Exception {
		grid1.addGenericCategoryID(GenericEntityType.forName("channel"), 3002);
		GenericCategoryIdentity expected1 = new GenericCategoryIdentity(GenericEntityType.forName("channel").getCategoryType(), 3000);
		GenericCategoryIdentity expected2 = new GenericCategoryIdentity(GenericEntityType.forName("channel").getCategoryType(), 3002);
		assertArrayEquals(new GenericCategoryIdentity[] { expected1, expected2 }, ServerContextUtil.extractGenericCategoryIdentities(grid1, GenericEntityType.forName("channel").getCategoryType()));
	}

	@Test
	public void testExtractEntityIdentitiesProdGridDoesNotSupportCategories() throws Exception {
		grid1.addGenericCategoryID(GenericEntityType.forName("channel"), 30);
		assertArrayEquals(new GenericEntityIdentity[0], ServerContextUtil.extractGenericEntityIdentities(grid1, GenericEntityType.forName("channel").getID()));
	}

	@Test
	public void testExtractEntityIdentitiesProdGridWithNullGridThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.extractGenericEntityIdentities(null, 1);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testExtractEntityIdentitiesProdGridWithUnusedEntityTypeReturnsEmptyArray() throws Exception {
		assertEquals(0, ServerContextUtil.extractGenericEntityIdentities(grid1, 777).length);
	}

	@Test
	public void testExtractEntityIdentitiesProdGridWithValidEntityTypeReturnsCorrectObject() throws Exception {
		grid1.addGenericEntityID(GenericEntityType.forName("product"), 2002);
		GenericEntityIdentity expected1 = new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 2000);
		GenericEntityIdentity expected2 = new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 2002);
		assertArrayEquals(new GenericEntityIdentity[] { expected1, expected2 }, ServerContextUtil.extractGenericEntityIdentities(grid1, GenericEntityType.forName("product").getID()));
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForContextArrayWithEmptyContextReturnsEmptyArray() throws Exception {
		assertEquals(0, ServerContextUtil.extractGenericCategoryIdentities(new GuidelineContext[0]).length);
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForContextArrayWithEntityContextReturnsEmptyArray() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		assertEquals(0, ServerContextUtil.extractGenericCategoryIdentities(new GuidelineContext[] { c1 }).length);
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForContextArrayWithNullThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.extractGenericCategoryIdentities((GuidelineContext[]) null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
		}
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForContextArrayWithValidGridReturnsCorrectArray() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c1.setIDs(new int[] { 100 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("program").getCategoryType());
		c2.setIDs(new int[] { 2, 3, 4 });

		GenericCategoryIdentity[] ids = ServerContextUtil.extractGenericCategoryIdentities(new GuidelineContext[] { c1, c2 });
		assertEquals(4, ids.length);
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("product").getCategoryType(), 100));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("program").getCategoryType(), 2));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("program").getCategoryType(), 3));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("program").getCategoryType(), 4));
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForGridWithEmptyContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertEquals(0, ServerContextUtil.extractGenericCategoryIdentities(grid).length);
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForGridWithEntityContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 11, 22, 33, 44 });
		assertEquals(0, ServerContextUtil.extractGenericCategoryIdentities(grid).length);
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForGridWithNullThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.extractGenericCategoryIdentities((ProductGrid) null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
		}
	}

	@Test
	public void testExtractGenericCategoryIdentitiesForGridWithValidGridReturnsCorrectArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200, 300 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("channel"), new int[] { 5 });

		GenericCategoryIdentity[] ids = ServerContextUtil.extractGenericCategoryIdentities(grid);
		assertEquals(4, ids.length);
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("product").getCategoryType(), 100));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("product").getCategoryType(), 200));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("product").getCategoryType(), 300));
		assertContains(ids, new GenericCategoryIdentity(GenericEntityType.forName("channel").getCategoryType(), 5));
	}

	@Test
	public void testExtractGenericEntityIdentitiesForContextArrayWithCategoryContextReturnsEmptyArray() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c1.setIDs(new int[] { 200, 300 });
		assertEquals(0, ServerContextUtil.extractGenericEntityIdentities(new GuidelineContext[] { c1 }).length);
	}

	@Test
	public void testExtractGenericEntityIdentitiesForContextArrayWithEmptyContextReturnsEmptyArray() throws Exception {
		assertEquals(0, ServerContextUtil.extractGenericEntityIdentities(new GuidelineContext[0]).length);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractGenericEntityIdentitiesForContextArrayWithNullThrowsNullPointerException() throws Exception {
		ServerContextUtil.extractGenericEntityIdentities((GuidelineContext[]) null);
	}

	@Test
	public void testExtractGenericEntityIdentitiesForContextArrayWithValidGridReturnsCorrectArray() throws Exception {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 100 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("program"));
		c2.setIDs(new int[] { 2, 3, 4 });

		GenericEntityIdentity[] ids = ServerContextUtil.extractGenericEntityIdentities(new GuidelineContext[] { c1, c2 });
		assertEquals(4, ids.length);
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 100));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("program").getID(), 2));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("program").getID(), 3));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("program").getID(), 4));
	}

	@Test
	public void testExtractGenericEntityIdentitiesForGridWithCategoryContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		assertEquals(0, ServerContextUtil.extractGenericEntityIdentities(grid).length);
	}

	@Test
	public void testExtractGenericEntityIdentitiesForGridWithEmptyContextReturnsEmptyArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertEquals(0, ServerContextUtil.extractGenericEntityIdentities(grid).length);
	}

	@Test(expected = NullPointerException.class)
	public void testExtractGenericEntityIdentitiesForGridWithNullThrowsNullPointerException() throws Exception {
		ServerContextUtil.extractGenericEntityIdentities((ProductGrid) null);
	}

	@Test
	public void testExtractGenericEntityIdentitiesForGridWithValidGridReturnsCorrectArray() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200, 300 });
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 5 });

		GenericEntityIdentity[] ids = ServerContextUtil.extractGenericEntityIdentities(grid);
		assertEquals(4, ids.length);
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 100));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 200));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("product").getID(), 300));
		assertContains(ids, new GenericEntityIdentity(GenericEntityType.forName("channel").getID(), 5));
	}

	@Test
	public void testFindMatchingGridRows() {
		GridTemplate template = createGridTemplate(createUsageType());
		ProductGrid grid = createGuidelineGrid(template);
		GridTemplateColumn entityColumn = createGridTemplateColumn(1, createUsageType());
		entityColumn.setDataSpecDigest(createEntityColumnDataSpecDigest("product", false, false, false));
		template.addGridTemplateColumn(entityColumn);
		GenericEntityType type = GenericEntityType.forName("product");
		CategoryOrEntityValue value = new CategoryOrEntityValue(type, true, 10);
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		List<Object> rowList = new ArrayList<Object>();
		rowList.add(value);
		dataList.add(rowList);
		grid.setDataList(dataList);

		GuidelineContext c = new GuidelineContext(type);
		GuidelineContext[] contexts = new GuidelineContext[] { c };
		c.setIDs(new int[] { 10 });
		List<Integer> rowsFound = ServerContextUtil.findMatchingGridRows(contexts, grid, false, false, false);
		assertNotNull(rowsFound);
		assertEquals(rowsFound.size(), 1);
		assertEquals(rowsFound.get(0), new Integer(1));

		value.setId(2);
		rowsFound = ServerContextUtil.findMatchingGridRows(contexts, grid, false, false, false);
		assertNotNull(rowsFound);
		assertEquals(rowsFound.size(), 0);
	}

	@Test
	public void testFindReferencedGridCellsForEntitiesForCategoryMultiSelectHappyCase() throws Exception {
		AbstractTemplateCore<GridTemplateColumn> template = grid1.getTemplate();
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(3, template.getUsageType())));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(2).getColumnDataSpecDigest().setEntityType("channel");
		template.getColumn(2).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		grid1.setNumRows(2);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 100));
		grid1.setValue(1, template.getColumn(1).getName(), value);
		grid1.setValue(1, template.getColumn(2).getName(), value);
		value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 101));
		grid1.setValue(2, template.getColumn(1).getName(), value);

		List<GridCellDetail> list = ServerContextUtil.findReferencedGridCellsForEntities(grid1, GenericEntityType.forName("product"), 100, false);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getRowID());
		assertEquals(template.getColumn(1).getName(), list.get(0).getColumnName());
		assertEquals(1, ((CategoryOrEntityValues) list.get(0).getCellValue()).size());
	}

	@Test
	public void testFindReferencedGridCellsForEntitiesForCategorySingleSelectHappyCase() throws Exception {
		AbstractTemplateCore<GridTemplateColumn> template = grid1.getTemplate();
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(3, template.getUsageType())));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsCategoryAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(2).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(2).getColumnDataSpecDigest().setIsCategoryAllowed(false);
		template.getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		grid1.setNumRows(2);
		CategoryOrEntityValue value = new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 100);
		grid1.setValue(1, template.getColumn(1).getName(), value);
		grid1.setValue(1, template.getColumn(2).getName(), value);
		value = new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 101);
		grid1.setValue(2, template.getColumn(1).getName(), value);

		List<GridCellDetail> list = ServerContextUtil.findReferencedGridCellsForEntities(grid1, GenericEntityType.forName("product"), 100, false);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getRowID());
		assertEquals(template.getColumn(1).getName(), list.get(0).getColumnName());
		assertEquals(100, ((CategoryOrEntityValue) list.get(0).getCellValue()).getId());
	}

	@Test
	public void testFindReferencedGridCellsForEntitiesForEntityMultiSelectHappyCase() throws Exception {
		AbstractTemplateCore<GridTemplateColumn> template = grid1.getTemplate();
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(3, template.getUsageType())));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(2).getColumnDataSpecDigest().setEntityType("channel");
		template.getColumn(2).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		grid1.setNumRows(2);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 100));
		grid1.setValue(1, template.getColumn(1).getName(), value);
		grid1.setValue(1, template.getColumn(2).getName(), value);
		value = new CategoryOrEntityValues();
		value.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 101));
		grid1.setValue(2, template.getColumn(1).getName(), value);

		List<GridCellDetail> list = ServerContextUtil.findReferencedGridCellsForEntities(grid1, GenericEntityType.forName("product"), 100, true);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getRowID());
		assertEquals(template.getColumn(1).getName(), list.get(0).getColumnName());
		assertEquals(1, ((CategoryOrEntityValues) list.get(0).getCellValue()).size());
	}

	@Test
	public void testFindReferencedGridCellsForEntitiesForEntitySingleSelectHappyCase() throws Exception {
		AbstractTemplateCore<GridTemplateColumn> template = grid1.getTemplate();
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(2, template.getUsageType())));
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(3, template.getUsageType())));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(1).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(1).getColumnDataSpecDigest().setIsEntityAllowed(true);
		template.getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		template.getColumn(2).getColumnDataSpecDigest().setEntityType("product");
		template.getColumn(2).getColumnDataSpecDigest().setIsEntityAllowed(false);
		template.getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_STRING);

		grid1.setNumRows(2);
		CategoryOrEntityValue value = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 100);
		grid1.setValue(1, template.getColumn(1).getName(), value);
		grid1.setValue(1, template.getColumn(2).getName(), value);
		value = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 101);
		grid1.setValue(2, template.getColumn(1).getName(), value);

		List<GridCellDetail> list = ServerContextUtil.findReferencedGridCellsForEntities(grid1, GenericEntityType.forName("product"), 100, true);
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getRowID());
		assertEquals(template.getColumn(1).getName(), list.get(0).getColumnName());
		assertEquals(100, ((CategoryOrEntityValue) list.get(0).getCellValue()).getId());
	}

	@Test
	public void testFindReferencedGridCellsForEntitiesWithNoEntityListColumnReturnsEmptyList() throws Exception {
		AbstractTemplateCore<GridTemplateColumn> template = grid1.getTemplate();
		template.addColumn(attachColumnDataSpecDigest(createGridTemplateColumn(1, template.getUsageType())));
		template.getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		assertEquals(0, ServerContextUtil.findReferencedGridCellsForEntities(grid1, GenericEntityType.forName("product"), 1, true).size());
	}

	@Test(expected = NullPointerException.class)
	public void testFindReferencedGridCellsForEntitiesWithNullGridThrowsNullPointerException() throws Exception {
		ServerContextUtil.findReferencedGridCellsForEntities(null, GenericEntityType.forName("product"), 1, true);
	}

	@Test
	public void testHasApplicableContextWithCategoryGridGridAndEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		assertFalse(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasApplicableContextWithEmptyGridAndEmptyContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertTrue(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasApplicableContextWithEmptyGridAndNonEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		assertFalse(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[] { c1 }));
	}

	@Test
	public void testHasApplicableContextWithEmptyGridAndNullContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertTrue(ServerContextUtil.hasApplicableContext(grid, null));
	}

	@Test
	public void testHasApplicableContextWithEntityGridGridAndEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		assertFalse(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasApplicableContextWithNonEmptyGridAndBiggerContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 300 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 3, 4, 5, 6 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 11 });
		GuidelineContext c3 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c3.setIDs(new int[] { 200 });

		assertFalse(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[] { c1, c2, c3 }));
	}

	@Test
	public void testHasApplicableContextWithNonEmptyGridAndIdenticalContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 300 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 3, 4, 5, 6, 7 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 11 });
		GuidelineContext c3 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c3.setIDs(new int[] { 200, 300 });

		assertTrue(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[] { c1, c2, c3 }));
	}

	@Test
	public void testHasApplicableContextWithNonEmptyGridAndSubCategoryContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 300 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 4, 3 });
		GuidelineContext c3 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c3.setIDs(new int[] { 200 });

		assertFalse(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[] { c1, c3 }));
	}

	@Test
	public void testHasApplicableContextWithNonEmptyGridAndSubEntityContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 300 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 4, 7, 3 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 11 });
		GuidelineContext c3 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c3.setIDs(new int[] { 200, 300 });

		assertTrue(ServerContextUtil.hasApplicableContext(grid, new GuidelineContext[] { c1, c2, c3 }));
	}

	@Test
	public void testHasApplicableContextWithNullProductGridThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.hasApplicableContext(null, new GuidelineContext[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
		}
	}

	@Test
	public void testHasSameContextWithCategoryGridGridAndEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		assertFalse(ServerContextUtil.hasSameContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasSameContextWithEmptyGridAndEmptyContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertTrue(ServerContextUtil.hasSameContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasSameContextWithEmptyGridAndNonEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		assertFalse(ServerContextUtil.hasSameContext(grid, new GuidelineContext[] { c1 }));
	}

	@Test
	public void testHasSameContextWithEmptyGridAndNullContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertTrue(ServerContextUtil.hasSameContext(grid, null));
	}

	@Test
	public void testHasSameContextWithEntityGridGridAndEmptyContextReturnsFalse() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		assertFalse(ServerContextUtil.hasSameContext(grid, new GuidelineContext[0]));
	}

	@Test
	public void testHasSameContextWithNonEmptyGridAndIdenticalContextReturnsTrue() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 200, 300 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 3, 4, 5, 6, 7 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 11 });
		GuidelineContext c3 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c3.setIDs(new int[] { 200, 300 });

		assertTrue(ServerContextUtil.hasSameContext(grid, new GuidelineContext[] { c1, c2, c3 }));
	}

	@Test(expected = NullPointerException.class)
	public void testHasSameContextWithNullProductGridThrowsNullPointerException() throws Exception {
		ServerContextUtil.hasSameContext(null, new GuidelineContext[0]);
	}

	@Test
	public void testIsParentContextWithDifferentTypes() {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });
		assertFalse(ServerContextUtil.isParentContext(new GuidelineContext[] { c1 }, new GuidelineContext[] { c2 }, false));
	}

	@Test
	public void testIsParentContextWithSameTypes() {
		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		assertTrue(ServerContextUtil.isParentContext(new GuidelineContext[] { c1 }, new GuidelineContext[] { c1 }, false));
	}

	@Test
	public void testIsSameEntityType() {
		GuidelineContext c = new GuidelineContext(GenericEntityType.forName("product"));
		assertTrue(ServerContextUtil.isSameEntityType(c, "product"));
		assertFalse(ServerContextUtil.isSameEntityType(c, "program"));
	}

	@Test
	public void testSetContextWithCategoryContextArrayClearsEntityInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		c1.setIDs(new int[] { 200, 300 });
		ServerContextUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericEntityContext());
		assertTrue(grid.hasGenericCategoryContext(GenericEntityType.forName("product")));
	}

	@Test
	public void testSetContextWithEmptyContextArrayClearsGridContext() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });
		ServerContextUtil.setContext(grid, new GuidelineContext[0]);
		assertTrue(grid.isContextEmpty());
	}

	@Test
	public void testSetContextWithEntityContextArrayClearsCategoryInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		ServerContextUtil.setContext(grid, new GuidelineContext[] { c1 });
		assertFalse(grid.hasAnyGenericCategoryContext());
		assertTrue(grid.hasGenericEntityContext(GenericEntityType.forName("product")));
	}

	@Test
	public void testSetContextWithNullContextArrayClearsGridContext() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });
		ServerContextUtil.setContext(grid, null);
		assertTrue(grid.isContextEmpty());
	}

	@Test
	public void testSetContextWithNullGridThrowsNullPointerException() throws Exception {
		try {
			ServerContextUtil.hasSameContext(null, new GuidelineContext[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
		}
	}

	@Test
	public void testSetContextWithValidContextReplacesExistingContextInGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("investor"), new int[] { 11 });

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ServerContextUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

	@Test
	public void testSetContextWithValidContextSetsContextPropertyOnEmptyGrid() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);

		GuidelineContext c1 = new GuidelineContext(GenericEntityType.forName("product"));
		c1.setIDs(new int[] { 200, 300 });
		GuidelineContext c2 = new GuidelineContext(GenericEntityType.forName("investor").getCategoryType());
		c2.setIDs(new int[] { 1, 2, 3, 4, 5 });

		ServerContextUtil.setContext(grid, new GuidelineContext[] { c1, c2 });
		assertArrayEqualsIgnoresOrder(new int[] { 200, 300 }, grid.getGenericEntityIDs(GenericEntityType.forName("product")));
		assertArrayEqualsIgnoresOrder(new int[] { 1, 2, 3, 4, 5 }, grid.getGenericCategoryIDs(GenericEntityType.forName("investor")));
	}

}

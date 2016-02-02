package com.mindbox.pe.server.cache;

import java.util.List;
import java.util.Map;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.model.GridCellDetail;

public class GridManagerTest extends AbstractTestWithTestConfig {

	private static final String CLASS_NAME = "Borrower";

	private static final String ATTRIBUTE_NAME = "Citizenship";

	private static final String CLASS_ATTRIBUTE_REFERENCE = CLASS_NAME + "." + ATTRIBUTE_NAME;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("GridManager Tests");
		suite.addTestSuite(GridManagerTest.class);
		return suite;
	}

	public GridManagerTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		super.config.initServer();
		// add template
		GridTemplateColumn column = new GridTemplateColumn(1, "col1", "Column 1", 100, TemplateUsageType.valueOf("Global-Qualify"));
		ColumnDataSpecDigest cdsd = new ColumnDataSpecDigest();
		cdsd.setIsBlankAllowed(true);
		cdsd.setIsMultiSelectAllowed(true);
		cdsd.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		cdsd.setAttributeMap(CLASS_ATTRIBUTE_REFERENCE);
		column.setDataSpecDigest(cdsd);
		GridTemplate template = new GridTemplate(1, "test1", TemplateUsageType.valueOf("Global-Qualify"));
		template.addGridTemplateColumn(column);
		GuidelineTemplateManager.getInstance().addTemplate(template);

		column = new GridTemplateColumn(1, "col1", "Column 1", 100, TemplateUsageType.valueOf("Global-Qualify"));
		column.setDataSpecDigest(cdsd);
		template = new GridTemplate(2, "test2", TemplateUsageType.valueOf("Global-Qualify"));
		template.addGridTemplateColumn(column);
		GuidelineTemplateManager.getInstance().addTemplate(template);

		// add a generic category into cache
		EntityManager.getInstance().addGenericEntityCategory(20, 100, "test category");
		EntityManager.getInstance().addGenericEntityCategory(20, 101, "test category");

		// add product category into cache
		EntityManager.getInstance().addGenericEntityCategory(10, 100, "Test Prod Category 100");
		EntityManager.getInstance().addGenericEntityCategory(10, 101, "Test Prod Category 101");

		ProductGrid grid = new ProductGrid(1000, GuidelineTemplateManager.getInstance().getTemplate(1), null, null);
		grid.setValue(1, 1, "US Citizen");
		grid.setValue(2, 1, "1");
		grid.setValue(3, 1, "2,3");
		grid.setNumRows(3);
		GridManager.getInstance().addProductGrid(grid);

		grid = new ProductGrid(1001, GuidelineTemplateManager.getInstance().getTemplate(1), null, null);
		grid.setValue(1, 1, "US Citizen");
		grid.setValue(2, 1, "1");
		grid.setValue(3, 1, "2,3");
		grid.setNumRows(3);
		GridManager.getInstance().addProductGrid(grid);

		grid = new ProductGrid(2000, GuidelineTemplateManager.getInstance().getTemplate(2), null, null);
		grid.setValue(1, 1, "US Citizen");
		grid.setValue(2, 1, "1");
		grid.setNumRows(2);
		GridManager.getInstance().addProductGrid(grid);
	}

	protected void tearDown() throws Exception {
		GridManager.getInstance().removeGuidelinesForTemplate(1);
		GridManager.getInstance().removeGuidelinesForTemplate(2);
		GuidelineTemplateManager.getInstance().removeFromCache(1);
		GuidelineTemplateManager.getInstance().removeFromCache(2);
		EntityManager.getInstance().removeCategory(20, 100);
		EntityManager.getInstance().removeCategory(20, 101);
		EntityManager.getInstance().removeCategory(10, 100);
		EntityManager.getInstance().removeCategory(10, 101);
		DateSynonymManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testIsInUseWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(GridManager.getInstance(), "isInUse", new Class[] { DateSynonym.class });
	}

	public void testIsInUsePositiveCase() throws Exception {
		DateSynonym ds = ObjectMother.createDateSynonym();
		GridManager.getInstance().getProductGrid(2000).setEffectiveDate(ds);
		GridManager.getInstance().getProductGrid(2000).setExpirationDate(null);
		assertTrue(GridManager.getInstance().isInUse(ds));

		GridManager.getInstance().getProductGrid(2000).setEffectiveDate(null);
		GridManager.getInstance().getProductGrid(2000).setExpirationDate(ds);
		assertTrue(GridManager.getInstance().isInUse(ds));
	}

	public void testIsInUseNegativeCase() throws Exception {
		DateSynonym ds = ObjectMother.createDateSynonym();
		assertFalse(GridManager.getInstance().isInUse(ds));
	}

	public void testIsSubContextHappyCase() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101 });
		assertTrue(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testIsSubContextWithEmptyContextAndNonEmptyGridReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[0]));
	}

	public void testIsSubContextWithNonMatchingEntityIDsReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101, 102 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testIsSubContextWithCategoryGridAndEntityContextReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testIsSubContextWithCategoryContextAndEntityGridReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		context.setIDs(new int[] { 101 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testIsSubContextWithSameContextReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101, 100 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testIsSubContextWithNonMatchingCategoryIDsReturnsFalse() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		context.setIDs(new int[] { 101 });
		assertFalse(GridManager.getInstance().isSubContext(2, new GuidelineContext[] { context }));
	}

	public void testToToGuidelineReportDataWithNonExistentTemplateReturnsNull() throws Exception {
		ProductGrid grid = GridManager.getInstance().getProductGrid(1000);
		grid.setTemplateID(1234567);
		assertNull(invokeToGuidelineReportData(grid, "demo"));
	}

	public void testToGuidelineReportDataWithValidGridSetStatus() throws Exception {
		ProductGrid grid = GridManager.getInstance().getProductGrid(1000);
		grid.setStatus("SomeStatus");
		assertEquals("SomeStatus", invokeToGuidelineReportData(grid, "demo").getStatus());
	}

	private GuidelineReportData invokeToGuidelineReportData(ProductGrid prodGrid, String userId) {
		return (GuidelineReportData) ReflectionUtil.executeStaticPrivate(GridManager.class, "toGuidelineReportData", new Class[] { ProductGrid.class,
				String.class }, new Object[] { prodGrid, userId });
	}

	public void testGetProductGridsWithInvalidTemplateIDReturnsEmptyList() throws Exception {
		assertEquals(0, GridManager.getInstance().getProductGrids(-1, new GuidelineContext[0]).size());
	}

	public void testGetProductGridsWithValidTemplateAndContextReturnsApplicableGrids() throws Exception {
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1, 2 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("channel"));
		context.setIDs(new int[] { 2 });
		assertEquals(1, GridManager.getInstance().getProductGrids(1, new GuidelineContext[] { context }).size());
		assertEquals(new int[] { 1, 2 }, GridManager.getInstance().getProductGrid(1000).getGenericEntityIDs(GenericEntityType.forName("channel")));
	}

	public void testGetFullContextHappyCase() throws Exception {
		GridManager.getInstance().getProductGrid(2000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101 });

		GuidelineContext[] fullContext = GridManager.getInstance().getFullContext(2, new GuidelineContext[] { context }).toArray(
				new GuidelineContext[0]);
		assertEquals(1, fullContext.length);
		assertEquals(new int[] { 100, 101 }, fullContext[0].getIDs());
	}

	public void testHasGridWithInvalidGridIDReturnsFalse() throws Exception {
		assertFalse(GridManager.getInstance().hasGrid(9999, 0));
	}

	public void testHasGridWithTemplateIDMismatchReturnsFalse() throws Exception {
		assertFalse(GridManager.getInstance().hasGrid(1000, GridManager.getInstance().getProductGrid(1000).getTemplateID() + 1));
	}

	public void testHasGridHappyCase() throws Exception {
		assertTrue(GridManager.getInstance().hasGrid(1000, GridManager.getInstance().getProductGrid(1000).getTemplateID()));
	}

	public void testIsSameWithInvalidTemplateIDReturnTrue() throws Exception {
		assertTrue(GridManager.getInstance().isSame(-1, new GuidelineContext[0]));
	}

	public void testIsSameWithNonExistentContextReturnsTrue() throws Exception {
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("channel"));
		context.setIDs(new int[] { 2 });
		assertTrue(GridManager.getInstance().isSame(1, new GuidelineContext[] { context }));
	}

	public void testIsSameWithMultipleGridsIgnoresActivationAndExpirationDates() throws Exception {
		DateSynonym ds1 = new DateSynonym(1, "name1", "", getDate(2006, 1, 1));
		DateSynonym ds2 = new DateSynonym(2, "name2", "", getDate(2006, 9, 1));
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1000).setEffectiveDate(ds1);
		GridManager.getInstance().getProductGrid(1000).setExpirationDate(ds2);
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1001).setEffectiveDate(ds2);

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 100, 101 });
		assertTrue(GridManager.getInstance().isSame(1, new GuidelineContext[] { context }));
	}

	public void testIsSameWithMultipleGridsWithDifferentStatusReturnsTrue() throws Exception {
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1000).setStatus("Production");
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1001).setStatus("Draft");

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 100, 101 });
		assertTrue(GridManager.getInstance().isSame(1, new GuidelineContext[] { context }));
	}

	public void testIsSameWithMultipleGridsWithDifferentCommentsReturnsTrue() throws Exception {
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1000).setComments("some comments");
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1001).setComments("some comments2");

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 100, 101 });
		assertTrue(GridManager.getInstance().isSame(1, new GuidelineContext[] { context }));
	}

	public void testIsSameWithMultipleGridsWithDifferentRowCountReturnsTrue() throws Exception {
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1000).setNumRows(1);
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });
		GridManager.getInstance().getProductGrid(1001).setNumRows(2);

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 100, 101 });
		assertTrue(GridManager.getInstance().isSame(1, new GuidelineContext[] { context }));
	}

	public void testSearchGuidelinesWithCategoryCheckWithNullFilterThrowsNullPointerException() throws Exception {
		try {
			GridManager.getInstance().searchGuidelinesWithCategoryCheck(null, "demo");
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testRemoveCategoryFromAllContext() throws Exception {
		// add grids into cache
		GridManager.getInstance().addGridContext(1000, 20, 100);
		GridManager.getInstance().addGridContext(1001, 20, 100);
		GridManager.getInstance().addGridContext(1001, 20, 101);

		GridManager.getInstance().removeCategoryFromAllContext(20, 100);

		// check results
		GuidelineContext context = new GuidelineContext(20);
		context.setIDs(new int[] { 100 });
		assertTrue("hasGrids returned true", !GridManager.getInstance().hasApplicableGrids(1, new GuidelineContext[] { context }));
		assertEquals("getProductGrids returned non-empty list", 0, GridManager.getInstance().getProductGrids(1, context).size());
	}

	public void testRemoveGenericEntityFromAllContextHappyCase() throws Exception {
		// add grids into cache
		int[] entityIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), entityIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });

		GridManager.getInstance().removeGenericEntityFromAllContext(GenericEntityType.forName("product"), 100);

		// check results
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(entityIDs);
		assertTrue("hasGrids returned true", !GridManager.getInstance().hasApplicableGrids(1, new GuidelineContext[] { context }));
		assertEquals("getProductGrids returned non-empty list", 0, GridManager.getInstance().getProductGrids(1, context).size());
	}
	
	public void testRemoveGenericEntityFromAllContext_preserveExistingEntity() throws Exception {
		// add grids into cache
		int[] entityIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("product"), entityIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });

		GridManager.getInstance().removeGenericEntityFromAllContext(GenericEntityType.forName("product"), 100);

		// check results
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product"));
		context.setIDs(new int[] { 101 });

		// check results
		assertEquals(1, GridManager.getInstance().getProductGrids(1, context).size());
	}
	
	public void testRemoveProductCategoryFromAllContext() throws Exception {
		// add grids into cache
		int[] catIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericCategoryIDs(GenericEntityType.forName("product"), catIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });

		GridManager.getInstance().removeGenericCategoryFromAllContext(GenericEntityType.forName("product"), 100);

		// check results
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		context.setIDs(catIDs);
		assertTrue("hasGrids returned true", !GridManager.getInstance().hasApplicableGrids(1, new GuidelineContext[] { context }));
		assertEquals("getProductGrids returned non-empty list", 0, GridManager.getInstance().getProductGrids(1, context).size());
	}

	public void testRemoveProductCategoryFromAllContext_preserveExistingCategory() throws Exception {
		// add grids into cache
		int[] catIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericCategoryIDs(GenericEntityType.forName("product"), catIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });

		GridManager.getInstance().removeGenericCategoryFromAllContext(GenericEntityType.forName("product"), 100);

		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("product").getCategoryType());
		context.setIDs(new int[] { 101 });

		// check results
		assertEquals(1, GridManager.getInstance().getProductGrids(1, context).size());
	}

	public void testReplaceCellValuesWithNullArrayIsNoOp() throws Exception {
		// Test this does not throw exceptions
		GridManager.getInstance().replaceCellValues(null);
	}
	
	public void testReplaceCellValuesWithEmptyArrayIsNoOp() throws Exception {
		GridManager.getInstance().replaceCellValues(new GridCellDetail[0]);
	}
	
	public void testReplaceCellValuesWithInvalidGridIDThrowsIllegalArgumentException() throws Exception {
		GridCellDetail gridCellDetail = new GridCellDetail();
		gridCellDetail.setGridID(-1);
		assertThrowsException(GridManager.getInstance(), "replaceCellValues", new Class[]{GridCellDetail[].class}, 
				new Object[]{new GridCellDetail[]{gridCellDetail}}, IllegalArgumentException.class);
	}
	
	public void testReplaceCellValuesHappyCase() throws Exception {
		String columnName = GuidelineTemplateManager.getInstance().getTemplate(1).getColumn(1).getName();
		GridCellDetail gridCellDetail = new GridCellDetail();
		gridCellDetail.setGridID(1000);
		gridCellDetail.setRowID(1);
		gridCellDetail.setColumnName(columnName);
		gridCellDetail.setCellValue(ObjectMother.createInteger());
		GridCellDetail gridCellDetail2 = new GridCellDetail();
		gridCellDetail2.setGridID(1001);
		gridCellDetail2.setRowID(2);
		gridCellDetail2.setColumnName(columnName);
		gridCellDetail2.setCellValue(ObjectMother.createInteger());
		GridManager.getInstance().replaceCellValues(new GridCellDetail[]{gridCellDetail,gridCellDetail2});
		
		assertEquals(gridCellDetail.getCellValue(), GridManager.getInstance().getProductGrid(1000).getCellValue(1, columnName));
		assertEquals(gridCellDetail2.getCellValue(), GridManager.getInstance().getProductGrid(1001).getCellValue(2, columnName));
	}
	
	public void testGetGridCatgoryMapForProdCategoryDelete() throws Exception {
		// add grids into cache
		int[] catIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericCategoryIDs(GenericEntityType.forName("product"), catIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100, 101 });

		Map<Integer, int[]> map = GridManager.getInstance().getGridCatgoryMapForGenericCategoryDelete(GenericEntityType.forName("product"), 100);
		assertEquals(2, map.size());
		assertTrue(map.containsKey(new Integer(1000)));
		assertNull(map.get(new Integer(1000)));
		assertTrue(map.containsKey(new Integer(1001)));
		assertTrue(UtilBase.equals(new int[] { 101 }, (int[]) map.get(new Integer(1001))));

		map = GridManager.getInstance().getGridCatgoryMapForGenericCategoryDelete(GenericEntityType.forName("product"), 101);
		assertEquals(1, map.size());
		assertTrue(map.containsKey(new Integer(1001)));
		assertTrue(UtilBase.equals(new int[] { 100 }, (int[]) map.get(new Integer(1001))));

	}

	public void testGetGridCatgoryMapForProdCategoryDelete_emptyMap() throws Exception {
		Map<Integer, int[]> map = GridManager.getInstance().getGridCatgoryMapForGenericCategoryDelete(GenericEntityType.forName("product"), 104);
		assertNotNull(map);
		assertEquals(0, map.size());
	}

	public void testConsolidateGridsMergeForProdCategoryUsingGetAllConsolidatedGuidelineReportDataForTemplate() throws Exception {
		int[] catIDs = new int[] { 100 };
		GridManager.getInstance().getProductGrid(1000).setGenericCategoryIDs(GenericEntityType.forName("product"), catIDs);
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1 });
		GridManager.getInstance().getProductGrid(1001).setGenericCategoryIDs(GenericEntityType.forName("product"), catIDs);
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 2 });

		List<GuidelineReportData> list = GridManager.getInstance().getAllGuidelineReportDataForTemplate(1, "demo");
		assertEquals(2, list.size());
	}

	public void testConsolidateGridsSplitForProdCategoryUsingGetAllConsolidatedGuidelineReportDataForTemplate() throws Exception {
		GridManager.getInstance().getProductGrid(1000).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 100 });
		GridManager.getInstance().getProductGrid(1000).setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 1 });
		GridManager.getInstance().getProductGrid(1001).setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 101 });
		GridManager.getInstance().getProductGrid(1001).setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 2 });

		List<GuidelineReportData> list = GridManager.getInstance().getAllGuidelineReportDataForTemplate(1, "demo");
		assertEquals(2, list.size());
	}

	public void testConsolidateGridsMergeForGenericCategoryUsingGetAllConsolidatedGuidelineReportDataForTemplate() throws Exception {
		GridManager.getInstance().addGridContext(1000, 20, 100);
		GridManager.getInstance().addGridContext(1001, 20, 100);

		List<GuidelineReportData> list = GridManager.getInstance().getAllGuidelineReportDataForTemplate(1, "demo");
		assertEquals(2, list.size());
	}

	public void testConsolidateGridsSplitForGenericCategoryUsingGetAllConsolidatedGuidelineReportDataForTemplate() throws Exception {
		GridManager.getInstance().addGridContext(1000, 20, 100);
		GridManager.getInstance().addGridContext(1001, 20, 101);

		List<GuidelineReportData> list = GridManager.getInstance().getAllGuidelineReportDataForTemplate(1, "demo");
		assertEquals(2, list.size());
	}

	public void testDateSynonymUpdateGridCache() throws Exception {
		DateSynonym cachedEffDate = new DateSynonym(1, "Test 1 Name", "Test 1 Desc", getDate(2006, 10, 10));
		DateSynonym cachedExpDate = new DateSynonym(2, "Test 2 Name", "Test 2 Desc", getDate(2006, 11, 11));
		DateSynonymManager.getInstance().insert(cachedEffDate);
		DateSynonymManager.getInstance().insert(cachedExpDate);

		DateSynonym newEffDate = new DateSynonym(1, "Test 3 Name", "Test 3 Desc", getDate(2006, 12, 12));
		DateSynonym newExpDate = new DateSynonym(2, "Test 4 Name", "Test 4 Desc", getDate(2006, 13, 13));

		ProductGrid cachedGrid = new ProductGrid(1000, GuidelineTemplateManager.getInstance().getTemplate(1), null, null);
		ProductGrid newGrid = new ProductGrid(1000, GuidelineTemplateManager.getInstance().getTemplate(1), newEffDate, newExpDate);
		//cachedGrid should get the cached DS eff date and exp date
		assertTrue(GridManager.getInstance().updateCache(cachedGrid, newGrid));
		assertTrue(cachedGrid.getEffectiveDate().getDate().equals(cachedEffDate.getDate()));
		assertTrue(cachedGrid.getExpirationDate().getDate().equals(cachedExpDate.getDate()));
		assertTrue(cachedGrid.getEffectiveDate().getName().equals(cachedEffDate.getName()));
		assertTrue(cachedGrid.getExpirationDate().getName().equals(cachedExpDate.getName()));
	}

}

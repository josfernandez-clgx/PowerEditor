package com.mindbox.pe.server.bizlogic;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.GridCellDetail;

public class GridActionCoordinatorTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridActionCoordinatorTest Tests");
		suite.addTestSuite(GridActionCoordinatorTest.class);
		return suite;
	}

	private ProductGrid grid1, grid2;

	public GridActionCoordinatorTest(String name) {
		super(name);
	}

	private Object createGridKeyInstance(int gridID, AbstractGrid<GridTemplateColumn> grid) throws Exception {
		try {
			return ReflectionUtil.createInstance(GridActionCoordinator.class.getName() + "$GridIDKey", new Class[] {
					int.class,
					AbstractGrid.class }, new Object[] { new Integer(gridID), grid });
		}
		catch (RuntimeException ex) {
			if (ex.getCause() instanceof InvocationTargetException) {
				throw (Exception) ((InvocationTargetException) ex.getCause()).getCause();
			}
			else {
				throw ex;
			}
		}
	}

	public void testParameterGridSetCellValuesWithInvalidDataThrowsInvalidDataException() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.attachParameterTemplate(ObjectMother.createParameterGrid());
		paramGrid1.getTemplate().addColumn(new ParameterTemplateColumn(1, "col1", "", 100, ObjectMother.createUsageType()));
		ColumnDataSpecDigest columnDataSpecDigest = new ColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		columnDataSpecDigest.setIsBlankAllowed(true);
		paramGrid1.getTemplate().getColumn(1).setDataSpecDigest(columnDataSpecDigest);
		assertThrowsException(
				GridActionCoordinator.class,
				"setParameterGridCellValues",
				new Class[] { ParameterGrid.class, String[][].class },
				new Object[] { paramGrid1, new String[][] { new String[] { "bogusValue" } } },
				InvalidDataException.class);
	}

	public void testGridKeyConstructorWithNullGridThrowsNullPointerException() throws Exception {
		try {
			createGridKeyInstance(0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGridKeyHashCodeReturnsUniqueIDForDifferentGridID() throws Exception {
		Object gridIDKey = createGridKeyInstance(1, grid1);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	public void testGridKeyHashCodeReturnsUniqueIDForDifferentTemplateID() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid2).hashCode());
	}

	public void testGridKeyHashCodeReturnsUniqueIDForDifferentActivationDate() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		DateSynonym ds = new DateSynonym(1, "name", "", new Date());
		grid1.setEffectiveDate(ds);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	public void testGridKeyHashCodeReturnsUniqueIDForDifferentExpirationDate() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		DateSynonym ds = new DateSynonym(1, "name", "", new Date());
		grid1.setExpirationDate(ds);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	public void testGridKeyHashCodeReturnsSameIDForGridsWithDifferentContext() throws Exception {
		grid1.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	// TODO complete implementation when persistence testing framework is complete 
	public void testRemoveGenericEntityFromContextAndEntityListColumnsHappyCase() throws Exception {
	}

	// TODO complete implementation when persistence testing framework is complete 
	public void testSyncGridsWithNoGridsToDelete() throws Exception {
		//List gridsToSave = new ArrayList();
		//gridsToSave.add(grid1);
		//gridsToSave.add(grid2);
		//GridActionCoordinator gac = GridActionCoordinator.getInstance();
		// Invoke gac.syncGridData(templateID, gridsToSave, null, user)
		// with list of grids then retrieve the list from storage and ensure 
		// they have been persisted. Then delete the same list with 
		// invoke gac.syncGridData(templateID, new ArrayList(), gridsToSave, user)
		// and make sure they are deleted.  
	}

	public void testSaveAndDeletedGrids() throws Exception {
		grid1.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	// TODO when persistance framework is available test this method
	// It should that the new effective dates from the cutoverdate is the cache
	// date synonym and not the instance
	public void testDateSynonymUpdateCacheForCutoverForAndStoreTemplate() throws Exception {
		//        DateSynonym cutoverDate= new DateSynonym(1, "Test 1 Name", "Test 1 Desc", getDate(2006, 10, 10));        
		//        DateSynonym cachedExpDate = new DateSynonym(1, "Test 2 Name", "Test 2 Desc", getDate(2006, 11, 11));
		//        DateSynonymManager.getInstance().insert(cachedExpDate);
		//        ProductGrid newGrid = new ProductGrid(1000, new GridTemplate(1, "test2", TemplateUsageType.valueOf("Global-Qualify")), newEffDate, newExpDate);
		//        GridManager.getInstance().addProductGrid(newGrid);
		//        GridActionCoordinator gac = GridActionCoordinator.getInstance();        

	}

	public void testBuildEntityListGridCellDetailsToUpdateHappyCaseForSingleSelect() throws Exception {
		GridTemplateColumn column = ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(
				1,
				grid1.getTemplate().getUsageType()));
		grid1.getTemplate().addColumn(column);
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		column.getColumnDataSpecDigest().setEntityType("product");
		column.getColumnDataSpecDigest().setIsCategoryAllowed(true);
		column.getColumnDataSpecDigest().setIsEntityAllowed(true);
		grid1.setNumRows(3);
		grid1.setValue(1, column.getName(), null);
		grid1.setValue(2, column.getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 1));
		grid1.setValue(3, column.getName(), new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1));

		// check for category
		List<GridCellDetail> list = invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType.forName("product"), 1, false);
		assertEquals(1, list.size());
		GridCellDetail detail = list.get(0);
		assertEquals(2, detail.getRowID());
		assertNull(detail.getCellValue());

		// check for entity
		list = invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType.forName("product"), 1, true);
		assertEquals(1, list.size());
		detail = list.get(0);
		assertEquals(3, detail.getRowID());
		assertNull(detail.getCellValue());
	}

	public void testBuildEntityListGridCellDetailsToUpdateHappyCaseForMultiSelect() throws Exception {
		GridTemplateColumn column = ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(
				1,
				grid1.getTemplate().getUsageType()));
		grid1.getTemplate().addColumn(column);
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENTITY);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		column.getColumnDataSpecDigest().setEntityType("product");
		column.getColumnDataSpecDigest().setIsCategoryAllowed(true);
		column.getColumnDataSpecDigest().setIsEntityAllowed(true);
		grid1.setNumRows(3);
		grid1.setValue(1, column.getName(), null);
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 1));
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 2));
		grid1.setValue(2, column.getName(), values);
		values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1));
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 2));
		grid1.setValue(3, column.getName(), values);

		// check for category
		List<GridCellDetail> list = invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType.forName("product"), 1, false);
		assertEquals(1, list.size());
		GridCellDetail detail = list.get(0);
		assertEquals(2, detail.getRowID());
		values = (CategoryOrEntityValues) detail.getCellValue();
		assertEquals(1, values.size());
		assertFalse(values.hasID(false, 1));

		// check for entity
		list = invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType.forName("product"), 2, true);
		assertEquals(1, list.size());
		detail = list.get(0);
		assertEquals(3, detail.getRowID());
		values = (CategoryOrEntityValues) detail.getCellValue();
		assertEquals(1, values.size());
		assertFalse(values.hasID(true, 2));
	}

	@SuppressWarnings("unchecked")
	private List<GridCellDetail> invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType type, int id, boolean forEntity)
			throws Exception {
		return (List<GridCellDetail>) ReflectionUtil.executePrivate(
				GridActionCoordinator.getInstance(),
				"buildEntityListGridCellDetailsToUpdate",
				new Class[] { GenericEntityType.class, int.class, boolean.class },
				new Object[] { type, new Integer(id), new Boolean(forEntity) });
	}

	protected void setUp() throws Exception {
		super.setUp();
		super.config.initServer();

		GridTemplate template = ObjectMother.createGridTemplate(TemplateUsageType.valueOf("Global-Qualify"));
		GuidelineTemplateManager.getInstance().addTemplate(template);
		grid1 = new ProductGrid(1000, template, null, null);
		GridManager.getInstance().addProductGrid(grid1);

		template = ObjectMother.createGridTemplate(TemplateUsageType.valueOf("Global-Qualify"));
		GuidelineTemplateManager.getInstance().addTemplate(template);
		grid2 = new ProductGrid(1001, template, null, null);
		GridManager.getInstance().addProductGrid(grid2);
	}

	protected void tearDown() throws Exception {
		GridManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().startLoading();
		super.config.resetConfiguration();
		super.tearDown();
	}
}

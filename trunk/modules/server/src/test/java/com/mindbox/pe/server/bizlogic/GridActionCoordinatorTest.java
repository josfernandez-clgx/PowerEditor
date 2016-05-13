package com.mindbox.pe.server.bizlogic;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.attachParameterTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplate;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createParameterGrid;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.UnitTestHelper.assertNotEquals;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.ParameterTemplateColumn;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.GridCellDetail;

public class GridActionCoordinatorTest extends AbstractTestWithTestConfig {

	private ProductGrid grid1, grid2;

	private Object createGridKeyInstance(int gridID, AbstractGrid<GridTemplateColumn> grid) throws Exception {
		try {
			return ReflectionUtil.createInstance(GridActionCoordinator.class.getName() + "$GridIDKey", new Class[] { int.class, AbstractGrid.class }, new Object[] { new Integer(gridID), grid });
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

	@SuppressWarnings("unchecked")
	private List<GridCellDetail> invokeBuildEntityListGridCellDetailsToUpdate(GenericEntityType type, int id, boolean forEntity) throws Exception {
		return (List<GridCellDetail>) ReflectionUtil.executePrivate(GridActionCoordinator.getInstance(), "buildEntityListGridCellDetailsToUpdate", new Class[] {
				GenericEntityType.class,
				int.class,
				boolean.class }, new Object[] { type, new Integer(id), new Boolean(forEntity) });
	}

	public void setUp() throws Exception {
		super.setUp();
		super.config.initServer();

		GridTemplate template = createGridTemplate(TemplateUsageType.valueOf("Global-Qualify"));
		GuidelineTemplateManager.getInstance().addTemplate(template);
		grid1 = new ProductGrid(1000, template, null, null);
		GridManager.getInstance().addProductGrid(grid1);

		template = createGridTemplate(TemplateUsageType.valueOf("Global-Qualify"));
		GuidelineTemplateManager.getInstance().addTemplate(template);
		grid2 = new ProductGrid(1001, template, null, null);
		GridManager.getInstance().addProductGrid(grid2);
	}

	public void tearDown() throws Exception {
		GridManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().startLoading();
		super.config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testBuildEntityListGridCellDetailsToUpdateHappyCaseForMultiSelect() throws Exception {
		GridTemplateColumn column = attachColumnDataSpecDigest(createGridTemplateColumn(1, grid1.getTemplate().getUsageType()));
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

	@Test
	public void testBuildEntityListGridCellDetailsToUpdateHappyCaseForSingleSelect() throws Exception {
		GridTemplateColumn column = attachColumnDataSpecDigest(createGridTemplateColumn(1, grid1.getTemplate().getUsageType()));
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

	@Test
	public void testGridKeyConstructorWithNullGridThrowsNullPointerException() throws Exception {
		try {
			createGridKeyInstance(0, null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGridKeyHashCodeReturnsSameIDForGridsWithDifferentContext() throws Exception {
		grid1.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	@Test
	public void testGridKeyHashCodeReturnsUniqueIDForDifferentActivationDate() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		DateSynonym ds = new DateSynonym(1, "name", "", new Date());
		grid1.setEffectiveDate(ds);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	@Test
	public void testGridKeyHashCodeReturnsUniqueIDForDifferentExpirationDate() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		DateSynonym ds = new DateSynonym(1, "name", "", new Date());
		grid1.setExpirationDate(ds);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	@Test
	public void testGridKeyHashCodeReturnsUniqueIDForDifferentGridID() throws Exception {
		Object gridIDKey = createGridKeyInstance(1, grid1);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}

	@Test
	public void testGridKeyHashCodeReturnsUniqueIDForDifferentTemplateID() throws Exception {
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertNotEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid2).hashCode());
	}

	@Test
	public void testParameterGridSetCellValuesWithInvalidDataThrowsInvalidDataException() throws Exception {
		ParameterGrid paramGrid1 = attachParameterTemplate(createParameterGrid());
		paramGrid1.getTemplate().addColumn(new ParameterTemplateColumn(1, "col1", "", 100, createUsageType()));
		ColumnDataSpecDigest columnDataSpecDigest = new ColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		columnDataSpecDigest.setIsBlankAllowed(true);
		paramGrid1.getTemplate().getColumn(1).setDataSpecDigest(columnDataSpecDigest);
		assertThrowsException(GridActionCoordinator.class, "setParameterGridCellValues", new Class[] { ParameterGrid.class, String[][].class }, new Object[] {
				paramGrid1,
				new String[][] { new String[] { "bogusValue" } } }, InvalidDataException.class);
	}

	@Test
	public void testSaveAndDeletedGrids() throws Exception {
		grid1.setGenericEntityIDs(GenericEntityType.forName("product"), new int[] { 100, 200 });
		Object gridIDKey = createGridKeyInstance(-1, grid1);
		assertEquals(gridIDKey.hashCode(), createGridKeyInstance(-1, grid1).hashCode());
	}
}

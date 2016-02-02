package com.mindbox.pe.server.db.loaders;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;

public class GridLoaderTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridLoaderTest Tests");
		suite.addTestSuite(GridLoaderTest.class);
		return suite;
	}

	private GridLoader gridLoader;

	public GridLoaderTest(String name) {
		super(name);
	}

	public void testSetGridCellValuesUsesValueObjectFromUtil_ConvertToCellValueMethod() throws Exception {
		GridTemplate template = ObjectMother.attachGridTemplateColumns(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]), 1);
		GridTemplateColumn column = (GridTemplateColumn) template.getColumn(1);
		ObjectMother.attachColumnDataSpecDigest(column);
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		column.getColumnDataSpecDigest().setAttributeMap("Class.Attribute");
		ProductGrid grid = ObjectMother.createGuidelineGrid(template.getUsageType());
		grid.setTemplate(template);
		
		UIConfiguration.addEnumValue("Class.Attribute", ObjectMother.createEnumValue());
		invokeSetGridCellValue(grid, 1, column, "1234");
		assertTrue(grid.getCellValueObject(1, 1, null) instanceof Integer);
		assertEquals(1234, ((Integer)grid.getCellValueObject(1,1,null)).intValue());
	}

	private void invokeSetGridCellValue(ProductGrid grid, int rowID, GridTemplateColumn column, String value) throws Exception {
		Class<?> c = gridLoader.getClass();
		Method[] methods = c.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals("setGridCellValue")) {
				methods[i].setAccessible(true);
				methods[i].invoke(gridLoader, new Object[] { grid, new Integer(rowID), column, value });
				return;
			}
		}
		fail("No method named setGridCellValue found");
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		gridLoader = new GridLoader();
	}

	protected void tearDown() throws Exception {
		gridLoader = null;
		config.resetConfiguration();
		super.tearDown();
	}
}

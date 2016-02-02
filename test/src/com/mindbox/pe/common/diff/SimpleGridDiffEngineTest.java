package com.mindbox.pe.common.diff;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;

public class SimpleGridDiffEngineTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("SimpleGridDiffEngineTest Tests");
		suite.addTestSuite(SimpleGridDiffEngineTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private SimpleGridDiffEngine simpleGridDiffEngine;
	private GridTemplate template;

	public SimpleGridDiffEngineTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		template = ObjectMother.attachGridTemplateColumn(ObjectMother.createGridTemplate(ObjectMother.createUsageType()), 1);
		ObjectMother.attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		simpleGridDiffEngine = new SimpleGridDiffEngine();
	}

	public void testDiffWithNullGridThrowsNullPointerException() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(template);
		assertThrowsNullPointerException(
				simpleGridDiffEngine,
				"diff",
				new Class[] { AbstractGrid.class, AbstractGrid.class },
				new Object[] { null, grid });
		assertThrowsNullPointerException(
				simpleGridDiffEngine,
				"diff",
				new Class[] { AbstractGrid.class, AbstractGrid.class },
				new Object[] { grid, null });
	}

	public void testDiffHappyCaseForInsertedRowsFromEmptyGridSingleRow() throws Exception {
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(0);

		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getInsertedRows().length);
	}

	public void testDiffHappyCaseForInsertedRowsFromEmptyGridMultipleRows() throws Exception {
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(0);

		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(3);
		grid2.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(3, diffResult.getInsertedRows().length);
	}

	public void testDiffHappyCaseForInsertedRowsNonEmptyGridSingleRow() throws Exception {
		String grid1CellValue = ObjectMother.createString();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(1);
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		// new row at the beginning
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getInsertedRows().length);
		assertEquals(1, diffResult.getInsertedRows()[0]);

		// new row at the end
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);
		grid2.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getInsertedRows().length);
		assertEquals(2, diffResult.getInsertedRows()[0]);
	}

	public void testDiffHappyCaseForInsertedRowsNonEmptyGridMultipleRows() throws Exception {
		String grid1CellValue1 = ObjectMother.createString();
		String grid1CellValue2 = ObjectMother.createString();
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// new rows at the beginning
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(4);
		grid2.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(3, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getInsertedRows().length);
		assertEquals(1, diffResult.getInsertedRows()[0]);
		assertEquals(2, diffResult.getInsertedRows()[1]);

		// new rows at the end
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid2.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(4, template.getColumn(1).getName(), ObjectMother.createString());
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getInsertedRows().length);
		assertEquals(3, diffResult.getInsertedRows()[0]);
		assertEquals(4, diffResult.getInsertedRows()[1]);

		// new rows in the middle
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(4, template.getColumn(1).getName(), grid1CellValue2);
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getInsertedRows().length);
		assertEquals(2, diffResult.getInsertedRows()[0]);
		assertEquals(3, diffResult.getInsertedRows()[1]);

		// new rows in the mixed
		grid2.setNumRows(4);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid2.setValue(3, template.getColumn(1).getName(), grid1CellValue2);
		grid2.setValue(4, template.getColumn(1).getName(), ObjectMother.createString());
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getInsertedRows().length);
		assertEquals(2, diffResult.getInsertedRows()[0]);
		assertEquals(4, diffResult.getInsertedRows()[1]);
	}

	public void testDiffHappyCaseForDeletedRowsFromEmptyGridMultipleRows() throws Exception {
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());

		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(0);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(4, diffResult.getDeletedRows().length);
	}

	public void testDiffHappyCaseForDeletedRowsSingleRowWithMatchingRows() throws Exception {
		String grid1CellValue = ObjectMother.createString();
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		// deleted row at the beginning
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getDeletedRows().length);
		assertEquals(1, diffResult.getDeletedRows()[0]);

		// deleted row at the end
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue);
		grid1.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getDeletedRows().length);
		assertEquals(2, diffResult.getDeletedRows()[0]);
	}

	public void testDiffHappyCaseForDeletedRowsSingleRowWithNonMatchingRows() throws Exception {
		String grid1CellValue = ObjectMother.createString();
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getDeletedRows().length);
		assertEquals(2, diffResult.getDeletedRows()[0]);
	}

	public void testDiffHappyCaseForDeletedRowsMultipleRowsWithMatchingRows() throws Exception {
		String grid1CellValue1 = ObjectMother.createString();
		String grid1CellValue2 = ObjectMother.createString();
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// deleted rows at the beginning
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(3, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(1, diffResult.getDeletedRows()[0]);
		assertEquals(2, diffResult.getDeletedRows()[1]);

		// deleted rows at the end
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(4, template.getColumn(1).getName(), ObjectMother.createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(3, diffResult.getDeletedRows()[0]);
		assertEquals(4, diffResult.getDeletedRows()[1]);

		// deleted rows in the middle
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(2, diffResult.getDeletedRows()[0]);
		assertEquals(3, diffResult.getDeletedRows()[1]);

		// deleted rows mixed
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(1, diffResult.getDeletedRows()[0]);
		assertEquals(3, diffResult.getDeletedRows()[1]);
	}

	public void testDiffHappyCaseForDeletedRowsMultipleRowsWithNonMatchingRows() throws Exception {
		String grid1CellValue1 = ObjectMother.createString();
		String grid1CellValue2 = ObjectMother.createString();
		ProductGrid grid2 = ObjectMother.createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// one non matching row
		ProductGrid grid1 = ObjectMother.createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(4, template.getColumn(1).getName(), ObjectMother.createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(3, diffResult.getDeletedRows()[0]);
		assertEquals(4, diffResult.getDeletedRows()[1]);

		// no matching rows
		grid1.setValue(1, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(2, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(3, template.getColumn(1).getName(), ObjectMother.createString());
		grid1.setValue(4, template.getColumn(1).getName(), ObjectMother.createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRows().length);
		assertEquals(3, diffResult.getDeletedRows()[0]);
		assertEquals(4, diffResult.getDeletedRows()[1]);
	}
}
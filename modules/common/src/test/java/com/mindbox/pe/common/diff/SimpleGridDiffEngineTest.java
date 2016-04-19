package com.mindbox.pe.common.diff;

import static com.mindbox.pe.common.CommonTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

public class SimpleGridDiffEngineTest extends AbstractTestBase {

	private SimpleGridDiffEngine simpleGridDiffEngine;
	private GridTemplate template;

	@Before
	public void setUp() throws Exception {
		template = attachGridTemplateColumn(createGridTemplate(createUsageType()), 1);
		attachColumnDataSpecDigest((GridTemplateColumn) template.getColumn(1));
		simpleGridDiffEngine = new SimpleGridDiffEngine();
	}

	@Test
	public void testDiffHappyCaseForDeletedRowsFromEmptyGridMultipleRows() throws Exception {
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(3, template.getColumn(1).getName(), createString());

		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(0);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(4, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
	}

	@Test
	public void testDiffHappyCaseForDeletedRowsMultipleRowsWithMatchingRows() throws Exception {
		String grid1CellValue1 = createString();
		String grid1CellValue2 = createString();
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// deleted rows at the beginning
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), createString());
		grid1.setValue(3, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// deleted rows at the end
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid1.setValue(3, template.getColumn(1).getName(), createString());
		grid1.setValue(4, template.getColumn(1).getName(), createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(3, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(4, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// deleted rows in the middle
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), createString());
		grid1.setValue(3, template.getColumn(1).getName(), createString());
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(3, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// deleted rows mixed
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(3, template.getColumn(1).getName(), createString());
		grid1.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRowChangeDetails().size());
		assertEquals(1, diffResult.getDeletedRowChangeDetails().get(0).getRowNumber().intValue());
		assertEquals(3, diffResult.getDeletedRowChangeDetails().get(1).getRowNumber().intValue());
	}

	@Test
	public void testDiffHappyCaseForDeletedRowsMultipleRowsWithNonMatchingRows() throws Exception {
		String grid1CellValue1 = createString();
		String grid1CellValue2 = createString();
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// one non matching row
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(4);
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid1.setValue(3, template.getColumn(1).getName(), createString());
		grid1.setValue(4, template.getColumn(1).getName(), createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRowChangeDetails().size());
		assertEquals(3, diffResult.getDeletedRowChangeDetails().get(0).getRowNumber().intValue());
		assertEquals(4, diffResult.getDeletedRowChangeDetails().get(1).getRowNumber().intValue());

		// no matching rows
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), createString());
		grid1.setValue(3, template.getColumn(1).getName(), createString());
		grid1.setValue(4, template.getColumn(1).getName(), createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getDeletedRowChangeDetails().size());
		assertEquals(3, diffResult.getDeletedRowChangeDetails().get(0).getRowNumber().intValue());
		assertEquals(4, diffResult.getDeletedRowChangeDetails().get(1).getRowNumber().intValue());
	}

	@Test
	public void testDiffHappyCaseForDeletedRowsSingleRowWithMatchingRows() throws Exception {
		String grid1CellValue = createString();
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		// deleted row at the beginning
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());

		// deleted row at the end
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue);
		grid1.setValue(2, template.getColumn(1).getName(), createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
	}

	@Test
	public void testDiffHappyCaseForDeletedRowsSingleRowWithNonMatchingRows() throws Exception {
		String grid1CellValue = createString();
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), createString());
		grid1.setValue(2, template.getColumn(1).getName(), createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertFalse(diffResult.hasInsertedRow());
		assertTrue(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getDeletedRowChangeDetails().size());
		assertEquals(2, diffResult.getDeletedRowChangeDetails().get(0).getRowNumber().intValue());
	}

	@Test
	public void testDiffHappyCaseForInsertedRowsFromEmptyGridMultipleRows() throws Exception {
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(0);

		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(3);
		grid2.setValue(1, template.getColumn(1).getName(), createString());
		grid2.setValue(3, template.getColumn(1).getName(), createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(3, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
	}

	@Test
	public void testDiffHappyCaseForInsertedRowsFromEmptyGridSingleRow() throws Exception {
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(0);

		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(1);
		grid2.setValue(1, template.getColumn(1).getName(), createString());

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
	}

	@Test
	public void testDiffHappyCaseForInsertedRowsNonEmptyGridMultipleRows() throws Exception {
		String grid1CellValue1 = createString();
		String grid1CellValue2 = createString();
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(2);
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid1.setValue(2, template.getColumn(1).getName(), grid1CellValue2);

		// new rows at the beginning
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(4);
		grid2.setValue(1, template.getColumn(1).getName(), createString());
		grid2.setValue(2, template.getColumn(1).getName(), createString());
		grid2.setValue(3, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(4, template.getColumn(1).getName(), grid1CellValue2);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// new rows at the end
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue2);
		grid2.setValue(3, template.getColumn(1).getName(), createString());
		grid2.setValue(4, template.getColumn(1).getName(), createString());
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(3, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(4, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// new rows in the middle
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), createString());
		grid2.setValue(3, template.getColumn(1).getName(), createString());
		grid2.setValue(4, template.getColumn(1).getName(), grid1CellValue2);
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(3, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());

		// new rows in the mixed
		grid2.setNumRows(4);
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue1);
		grid2.setValue(2, template.getColumn(1).getName(), createString());
		grid2.setValue(3, template.getColumn(1).getName(), grid1CellValue2);
		grid2.setValue(4, template.getColumn(1).getName(), createString());
		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
		assertEquals(4, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(1).getRowNumber().intValue());
	}

	@Test
	public void testDiffHappyCaseForInsertedRowsNonEmptyGridSingleRow() throws Exception {
		String grid1CellValue = createString();
		ProductGrid grid1 = createGuidelineGrid(template);
		grid1.setNumRows(1);
		grid1.setValue(1, template.getColumn(1).getName(), grid1CellValue);

		// new row at the beginning
		ProductGrid grid2 = createGuidelineGrid(template);
		grid2.setNumRows(2);
		grid2.setValue(1, template.getColumn(1).getName(), createString());
		grid2.setValue(2, template.getColumn(1).getName(), grid1CellValue);

		GridDiffResult diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());

		// new row at the end
		grid2.setValue(1, template.getColumn(1).getName(), grid1CellValue);
		grid2.setValue(2, template.getColumn(1).getName(), createString());

		diffResult = simpleGridDiffEngine.diff(grid1, grid2);
		assertTrue(diffResult.hasInsertedRow());
		assertFalse(diffResult.hasDeletedRow());
		assertEquals(1, diffResult.getGridCellValueChangeDetailSet().getDetailList().size());
		assertEquals(2, diffResult.getGridCellValueChangeDetailSet().getDetailList().get(0).getRowNumber().intValue());
	}

	@Test
	public void testDiffWithNullGridThrowsNullPointerException() throws Exception {
		ProductGrid grid = createGuidelineGrid(template);
		assertThrowsNullPointerException(simpleGridDiffEngine, "diff", new Class[] { AbstractGrid.class, AbstractGrid.class }, new Object[] { null, grid });
		assertThrowsNullPointerException(simpleGridDiffEngine, "diff", new Class[] { AbstractGrid.class, AbstractGrid.class }, new Object[] { grid, null });
	}
}
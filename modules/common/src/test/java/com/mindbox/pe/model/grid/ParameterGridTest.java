package com.mindbox.pe.model.grid;

import static com.mindbox.pe.common.CommonTestObjectMother.attachParameterTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createParameterGrid;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInteger;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ParameterTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ParameterGridTest extends AbstractTestBase {

	@Test
	public void testIsParameterGridRetunsTrue() throws Exception {
		assertTrue(attachParameterTemplate(createParameterGrid()).isParameterGrid());
	}

	@Test
	public void testGetCellValueObjectHappyCase() throws Exception {
		ParameterGrid paramGrid1 = attachParameterTemplate(createParameterGrid());
		paramGrid1.getTemplate().addColumn(new ParameterTemplateColumn(1, "col1", "", 100, createUsageType()));
		String value = createString();
		paramGrid1.setCellValues(value);
		paramGrid1.setNumRows(1);
		assertEquals(value, paramGrid1.getCellValueObject(1, 1, null));
	}

	@Test
	public void testGetCellValueObjectReturnsDefaultObjectIfEmpty() throws Exception {
		ParameterGrid paramGrid1 = createParameterGrid();
		Object defaultObj = createInteger();
		assertSame(defaultObj, paramGrid1.getCellValueObject(0, 0, defaultObj));
	}

	@Test
	public void testHasSameCellValuesWithNullReturnsFalse() throws Exception {
		ParameterGrid paramGrid1 = createParameterGrid();
		assertFalse(paramGrid1.hasSameCellValues(null));
	}

	@Test
	public void testHasSameCellValuesWithDifferentCellValuesReturnsFalse() throws Exception {
		ParameterGrid paramGrid1 = createParameterGrid();
		paramGrid1.setCellValues("value1");

		ParameterGrid paramGrid2 = createParameterGrid();
		paramGrid2.setCellValues("value2");

		assertFalse(paramGrid1.hasSameCellValues(paramGrid2));
		assertFalse(paramGrid2.hasSameCellValues(paramGrid1));
	}

	@Test
	public void testHasSameCellValuesHappyCase() throws Exception {
		ParameterGrid paramGrid1 = createParameterGrid();
		paramGrid1.setCellValues("value1,value2");

		ParameterGrid paramGrid2 = createParameterGrid();
		paramGrid2.setCellValues("value1,value2");

		assertTrue(paramGrid1.hasSameCellValues(paramGrid2));
		assertTrue(paramGrid2.hasSameCellValues(paramGrid1));
	}

}

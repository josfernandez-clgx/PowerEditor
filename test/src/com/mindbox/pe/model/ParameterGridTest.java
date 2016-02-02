package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class ParameterGridTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ParameterGridTest Tests");
		suite.addTestSuite(ParameterGridTest.class);
		return suite;
	}

	public ParameterGridTest(String name) {
		super(name);
	}

	public void testIsParameterGridRetunsTrue() throws Exception {
		assertTrue(ObjectMother.attachParameterTemplate(ObjectMother.createParameterGrid()).isParameterGrid());
	}

	public void testGetCellValueObjectHappyCase() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.attachParameterTemplate(ObjectMother.createParameterGrid());
		paramGrid1.getTemplate().addColumn(new ParameterTemplateColumn(1, "col1", "", 100, ObjectMother.createUsageType()));
		String value = ObjectMother.createString();
		paramGrid1.setCellValues(value);
		paramGrid1.setNumRows(1);
		assertEquals(value, paramGrid1.getCellValueObject(1, 1, null));
	}

	public void testGetCellValueObjectReturnsDefaultObjectIfEmpty() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.createParameterGrid();
		Object defaultObj = ObjectMother.createInteger();
		assertSame(defaultObj, paramGrid1.getCellValueObject(0, 0, defaultObj));
	}

	public void testHasSameCellValuesWithNullReturnsFalse() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.createParameterGrid();
		assertFalse(paramGrid1.hasSameCellValues(null));
	}

	public void testHasSameCellValuesWithDifferentCellValuesReturnsFalse() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.createParameterGrid();
		paramGrid1.setCellValues("value1");

		ParameterGrid paramGrid2 = ObjectMother.createParameterGrid();
		paramGrid2.setCellValues("value2");

		assertFalse(paramGrid1.hasSameCellValues(paramGrid2));
		assertFalse(paramGrid2.hasSameCellValues(paramGrid1));
	}

	public void testHasSameCellValuesHappyCase() throws Exception {
		ParameterGrid paramGrid1 = ObjectMother.createParameterGrid();
		paramGrid1.setCellValues("value1,value2");

		ParameterGrid paramGrid2 = ObjectMother.createParameterGrid();
		paramGrid2.setCellValues("value1,value2");

		assertTrue(paramGrid1.hasSameCellValues(paramGrid2));
		assertTrue(paramGrid2.hasSameCellValues(paramGrid1));
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

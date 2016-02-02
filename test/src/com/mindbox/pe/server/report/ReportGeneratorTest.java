package com.mindbox.pe.server.report;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.EnumValues;

public class ReportGeneratorTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ReportGeneratorTest Tests");
		suite.addTestSuite(ReportGeneratorTest.class);
		return suite;
	}

	public ReportGeneratorTest(String name) {
		super(name);
	}
	
	public void testAsGridCellValueWithNullOrEmptyStringReturnsEmptyString() throws Exception {
		assertEquals("", ReportGenerator.asGridCellValue(null));
		assertEquals("", ReportGenerator.asGridCellValue(""));
	}

	public void testAsGridCellValueHappyCaseForCategoryOrEntityValue() throws Exception {
		CategoryOrEntityValue value = new CategoryOrEntityValue();
		value.setEntityType(GenericEntityType.getAllGenericEntityTypes()[0]);
	}
	
	public void testAsGridCellValue_EnumValuesWithNonEnumValueContents() throws Exception {
		EnumValues<Object> evs = new EnumValues<Object>();
		Object value1 = new Object();
		Object value2 = new Object();
		evs.add(value1);
		evs.add(value2);
		assertEquals(value1.toString() + ", " + value2, ReportGenerator.asGridCellValue(evs));
		evs.setSelectionExclusion(true);
		assertEquals("NOT " + value1 + ", " + value2, ReportGenerator.asGridCellValue(evs));
	}
	
	public void testAsGridCellValue_EnumValuesWithEnumValueContents() throws Exception {
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>();
		EnumValue value1 = ObjectMother.createEnumValue();
		EnumValue value2 = ObjectMother.createEnumValue();
		evs.add(value1);
		evs.add(value2);
		assertEquals(value1.getDisplayLabel() + ", " + value2.getDisplayLabel(), ReportGenerator.asGridCellValue(evs));
		evs.setSelectionExclusion(true);
		assertEquals("NOT " + value1.getDisplayLabel() + ", " + value2.getDisplayLabel(), ReportGenerator.asGridCellValue(evs));
	}
	
	public void testAsGridCellValue_EnumValue() throws Exception {
		EnumValue ev = ObjectMother.createEnumValue();
		assertEquals(ev.getDisplayLabel(), ReportGenerator.asGridCellValue(ev));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

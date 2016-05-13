package com.mindbox.pe.server.report;

import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class ReportGeneratorTest extends AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testAsGridCellValue_EnumValue() throws Exception {
		EnumValue ev = createEnumValue();
		assertEquals(ev.getDisplayLabel(), ReportGenerator.asGridCellValue(ev));
	}

	@Test
	public void testAsGridCellValue_EnumValuesWithEnumValueContents() throws Exception {
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>();
		EnumValue value1 = createEnumValue();
		EnumValue value2 = createEnumValue();
		evs.add(value1);
		evs.add(value2);
		assertEquals(value1.getDisplayLabel() + ", " + value2.getDisplayLabel(), ReportGenerator.asGridCellValue(evs));
		evs.setSelectionExclusion(true);
		assertEquals("NOT " + value1.getDisplayLabel() + ", " + value2.getDisplayLabel(), ReportGenerator.asGridCellValue(evs));
	}

	@Test
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

	@Test
	public void testAsGridCellValueHappyCaseForCategoryOrEntityValue() throws Exception {
		CategoryOrEntityValue value = new CategoryOrEntityValue();
		value.setEntityType(GenericEntityType.getAllGenericEntityTypes()[0]);
	}

	@Test
	public void testAsGridCellValueWithNullOrEmptyStringReturnsEmptyString() throws Exception {
		assertEquals("", ReportGenerator.asGridCellValue(null));
		assertEquals("", ReportGenerator.asGridCellValue(""));
	}
}

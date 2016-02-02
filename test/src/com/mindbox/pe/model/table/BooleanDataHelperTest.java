package com.mindbox.pe.model.table;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class BooleanDataHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("BooleanDataHelperTest Tests");
		suite.addTestSuite(BooleanDataHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public BooleanDataHelperTest(String name) {
		super(name);
	}

	public void testIsValidStringPositiveCase() throws Exception {
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.ANY_VALUE));
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.TRUE_VALUE));
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.FALSE_VALUE));
		assertTrue(BooleanDataHelper.isValidString(Boolean.TRUE.toString()));
		assertTrue(BooleanDataHelper.isValidString(Boolean.FALSE.toString()));
	}

	public void testIsValidStringNegativeCase() throws Exception {
		assertFalse(BooleanDataHelper.isValidString("somestr"));
		assertFalse(BooleanDataHelper.isValidString("Not found"));
		assertFalse(BooleanDataHelper.isValidString("YeS"));
	}

	public void testIsValidStringWithNullOrEmptyStringReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.isValidString(null));
		assertTrue(BooleanDataHelper.isValidString(""));
	}

	public void testMapToBooleanValueWithNullAndAllowBlankReturnsNull() throws Exception {
		assertNull(BooleanDataHelper.mapToBooleanValue(null, true));
	}

	public void testMapToBooleanValueWithNullAndNotAllowBlankReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(null, false).booleanValue());
	}

	public void testMapToBooleanValueWithAnyValueReturnsNull() throws Exception {
		assertNull(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.ANY_VALUE, true));
	}

	public void testMapToBooleanValueWithYesReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.TRUE_VALUE, true).booleanValue());
	}

	public void testMapToBooleanValueWithTrueReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.mapToBooleanValue(Boolean.TRUE.toString(), true).booleanValue());
	}

	public void testMapToBooleanValueWithNoReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.FALSE_VALUE, true).booleanValue());
	}

	public void testMapToBooleanValueWithFalseReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(Boolean.FALSE.toString(), true).booleanValue());
	}
}

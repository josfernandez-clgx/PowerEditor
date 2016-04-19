package com.mindbox.pe.model.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class BooleanDataHelperTest extends AbstractTestBase {

	@Test
	public void testIsValidStringPositiveCase() throws Exception {
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.ANY_VALUE));
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.TRUE_VALUE));
		assertTrue(BooleanDataHelper.isValidString(BooleanDataHelper.FALSE_VALUE));
		assertTrue(BooleanDataHelper.isValidString(Boolean.TRUE.toString()));
		assertTrue(BooleanDataHelper.isValidString(Boolean.FALSE.toString()));
	}

	@Test
	public void testIsValidStringNegativeCase() throws Exception {
		assertFalse(BooleanDataHelper.isValidString("somestr"));
		assertFalse(BooleanDataHelper.isValidString("Not found"));
		assertFalse(BooleanDataHelper.isValidString("YeS"));
	}

	@Test
	public void testIsValidStringWithNullOrEmptyStringReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.isValidString(null));
		assertTrue(BooleanDataHelper.isValidString(""));
	}

	@Test
	public void testMapToBooleanValueWithNullAndAllowBlankReturnsNull() throws Exception {
		assertNull(BooleanDataHelper.mapToBooleanValue(null, true));
	}

	@Test
	public void testMapToBooleanValueWithNullAndNotAllowBlankReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(null, false).booleanValue());
	}

	@Test
	public void testMapToBooleanValueWithAnyValueReturnsNull() throws Exception {
		assertNull(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.ANY_VALUE, true));
	}

	@Test
	public void testMapToBooleanValueWithYesReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.TRUE_VALUE, true).booleanValue());
	}

	@Test
	public void testMapToBooleanValueWithTrueReturnsTrue() throws Exception {
		assertTrue(BooleanDataHelper.mapToBooleanValue(Boolean.TRUE.toString(), true).booleanValue());
	}

	@Test
	public void testMapToBooleanValueWithNoReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(BooleanDataHelper.FALSE_VALUE, true).booleanValue());
	}

	@Test
	public void testMapToBooleanValueWithFalseReturnsFalse() throws Exception {
		assertFalse(BooleanDataHelper.mapToBooleanValue(Boolean.FALSE.toString(), true).booleanValue());
	}
}

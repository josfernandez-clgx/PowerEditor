package com.mindbox.pe.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IRange;

public class UtilBaseTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("UtilBaseTest Tests");
		suite.addTestSuite(UtilBaseTest.class);
		return suite;
	}

	public UtilBaseTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for UtilBaseTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for UtilBaseTest
		super.tearDown();
	}

	public void testIsSamePositiveCase() throws Exception {
		assertTrue(UtilBase.isSameGridCellValue(null, null));
		assertTrue(UtilBase.isSameGridCellValue(null, ""));
		assertTrue(UtilBase.isSameGridCellValue("", null));
		assertTrue(UtilBase.isSameGridCellValue("abc", "abc"));
	}

	public void testIsSamePositiveCaseForRangeValue() throws Exception {
		IRange emptyRange = new FloatRange();
		assertTrue(UtilBase.isSameGridCellValue(null, emptyRange));
		assertTrue(UtilBase.isSameGridCellValue(emptyRange, null));
	}

	public void testIsSameNegativeCase() throws Exception {
		assertFalse(UtilBase.isSameGridCellValue("abc", "ABC"));
		assertFalse(UtilBase.isSameGridCellValue(null, " "));
		assertFalse(UtilBase.isSameGridCellValue(" ", null));
	}

	public void testIsSameNegativeCaseForRangeValue() throws Exception {
		FloatRange range = new FloatRange();
		range.setLowerValue(1.0);
		assertFalse(UtilBase.isSameGridCellValue(null, range));
		assertFalse(UtilBase.isSameGridCellValue(range, null));
	}

	public void testIsValidSymbolWithNullOrEmptyStringReturnsFalse() throws Exception {
		testIsValidSymbol(null, false);
		testIsValidSymbol("", false);
	}

	public void testIsValidSymbolPositiveCase() throws Exception {
		testIsValidSymbol(
				"01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
				true,
				"Alphanumeric characters should be valid");
		testIsValidSymbol("!@$^*_-+={}[:>.?/", true, "!@$^*_-+={}[:>.?/ are legal characters");
		testIsValidSymbol("<123", true, "< is allowed if it's the first character");
	}

	public void testIsValidSymbolNagativeCaseForIllegalChars() throws Exception {
		char[] invalidChars = "`~#%&()]|\\;\"',".toCharArray();
		for (int i = 0; i < invalidChars.length; i++) {
			testIsValidSymbol(String.valueOf(invalidChars[i]), false);
		}
	}

	private void testIsValidSymbol(String string, boolean expectedResult, String failureMessage) throws Exception {
		assertEquals(failureMessage, expectedResult, UtilBase.isValidSymbol(string));
	}

	private void testIsValidSymbol(String string, boolean expectedResult) throws Exception {
		assertEquals(expectedResult, UtilBase.isValidSymbol(string));
	}

	public void testIsEmptyAfterTrimWithNullReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(null));
	}

	public void testIsEmptyAfterTrimWithEmptyStringReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(""));
	}

	public void testIsEmptyAfterTrimWithBlankStringReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(" \t"));
	}

	public void testIsEmptyAfterTrimWithNonBlankStringReturnsFalse() throws Exception {
		assertFalse(UtilBase.isEmptyAfterTrim(" x "));
	}

	public void testObjectArrayToStringHappyPath() throws Exception {
		String[] objs = new String[] { "one", "two", "three" };
		assertEquals("one,two,three", UtilBase.toString(objs));
	}

	public void testObjectArrayToStringNullArray() throws Exception {
		String[] nullArray = null;
		assertEquals("", UtilBase.toString(nullArray));
		assertEquals("", UtilBase.toString(nullArray, null));
	}

	public void testObjectArrayToStringEmptyArray() throws Exception {
		String[] emptyArray = new String[] {};
		assertEquals("", UtilBase.toString(emptyArray));
		assertEquals("", UtilBase.toString(emptyArray, null));
	}

	public void testObjectArrayToStringNullElement() throws Exception {
		String[] objs = new String[] { "one", null, "three" };
		assertEquals("one,null,three", UtilBase.toString(objs));
	}

	public void testObjectArrayToStringNonDefaultSeparator() throws Exception {
		String[] objs = new String[] { "one", "two", "three" };
		assertEquals("one : two : three", UtilBase.toString(objs, " : "));
	}

	public void testStripHappyPath() throws Exception {
		assertEquals("onetwothree", UtilBase.strip("one,two,three", ","));
	}

	public void testStripNullSource() throws Exception {
		assertNull(UtilBase.strip(null, ","));
	}

	public void testStripNullToBeStripped() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", null));
	}

	public void testStripEmptyToBeStripped() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", ""));
	}

	public void testStripToBeStrippedNotFound() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", ":"));
	}

	public void testIsMemberForObjectWithValidValueAndArrayReturnsTrue() throws Exception {
		assertTrue(UtilBase.isMember(new Integer(10), new Integer[] { new Integer(10), new Integer(4) }));
	}

	public void testIsMemberForObjectWithDiffTypeArrayReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember(new Integer(1), new Float[] { new Float(1.0f) }));
	}

	public void testIsMemberForObjectWithNullValueThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(null, new Integer[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testIsMemberForObjectWithEmptyArrayValueReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember(new Integer(1), new Integer[0]));
	}

	public void testIsMemberForObjectWithNullArrayThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(new Integer(1), null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testIsMemberForObjectWithNullInArrayIgnored() throws Exception {
		assertTrue(UtilBase.isMember(new Integer(8), new Integer[] { null, new Integer(8) }));
		assertFalse(UtilBase.isMember(new Integer(8), new Integer[] { new Integer(18), null }));
	}

	public void testIsMemberForStringWithValidValueAndArrayReturnsTrue() throws Exception {
		assertTrue(UtilBase.isMember("str", new String[] { "abc", "str" }));
	}

	public void testIsMemberForStringWithNullValueThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(null, new String[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testIsMemberForStringWithEmptyArrayValueReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember("str", new String[0]));
	}

	public void testIsMemberForStringWithNullArrayThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember("x", null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	public void testIsMemberForStringWithNullInArrayIgnored() throws Exception {
		assertFalse(UtilBase.isMember("x", new String[] { "a", null, "C" }));
		assertTrue(UtilBase.isMember("x", new String[] { null, "x" }));
	}

	public void testNullSafeEquals() throws Exception {
		Object o = new Object();

		assertTrue(UtilBase.nullSafeEquals(null, null));
		assertFalse(UtilBase.nullSafeEquals(o, null));
		assertFalse(UtilBase.nullSafeEquals(null, o));
		assertTrue(UtilBase.nullSafeEquals(o, o));
	}

	public void testContains() throws Exception {
		assertTrue(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4 }));
		assertTrue(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4, 5, 6, 7 }));
		assertFalse(UtilBase.contains(new int[] { 1, 2, 3, 4, 99 }, new int[] { 1, 2, 3, 4, 5, 6, 7 }));
		assertFalse(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 2, 3, 4, 5, 6, 7 }));
	}

}

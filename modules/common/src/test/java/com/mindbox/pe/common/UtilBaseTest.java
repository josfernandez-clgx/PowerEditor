package com.mindbox.pe.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.unittest.AbstractTestBase;

public class UtilBaseTest extends AbstractTestBase {

	@Test
	public void testIsSamePositiveCase() throws Exception {
		assertTrue(UtilBase.isSameGridCellValue(null, null));
		assertTrue(UtilBase.isSameGridCellValue(null, ""));
		assertTrue(UtilBase.isSameGridCellValue("", null));
		assertTrue(UtilBase.isSameGridCellValue("abc", "abc"));
	}

	@Test
	public void testIsSamePositiveCaseForRangeValue() throws Exception {
		IRange emptyRange = new FloatRange();
		assertTrue(UtilBase.isSameGridCellValue(null, emptyRange));
		assertTrue(UtilBase.isSameGridCellValue(emptyRange, null));
	}

	@Test
	public void testIsSameNegativeCase() throws Exception {
		assertFalse(UtilBase.isSameGridCellValue("abc", "ABC"));
		assertFalse(UtilBase.isSameGridCellValue(null, " "));
		assertFalse(UtilBase.isSameGridCellValue(" ", null));
	}

	@Test
	public void testIsSameNegativeCaseForRangeValue() throws Exception {
		FloatRange range = new FloatRange();
		range.setLowerValue(1.0);
		assertFalse(UtilBase.isSameGridCellValue(null, range));
		assertFalse(UtilBase.isSameGridCellValue(range, null));
	}

	@Test
	public void testIsValidSymbolWithNullOrEmptyStringReturnsFalse() throws Exception {
		testIsValidSymbol(null, false);
		testIsValidSymbol("", false);
	}

	@Test
	public void testIsValidSymbolPositiveCase() throws Exception {
		testIsValidSymbol("01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", true, "Alphanumeric characters should be valid");
		testIsValidSymbol("!@$^*_-+={}[:>.?/", true, "!@$^*_-+={}[:>.?/ are legal characters");
		testIsValidSymbol("<123", true, "< is allowed if it's the first character");
	}

	@Test
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

	@Test
	public void testIsEmptyAfterTrimWithNullReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(null));
	}

	@Test
	public void testIsEmptyAfterTrimWithEmptyStringReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(""));
	}

	@Test
	public void testIsEmptyAfterTrimWithBlankStringReturnsTrue() throws Exception {
		assertTrue(UtilBase.isEmptyAfterTrim(" \t"));
	}

	@Test
	public void testIsEmptyAfterTrimWithNonBlankStringReturnsFalse() throws Exception {
		assertFalse(UtilBase.isEmptyAfterTrim(" x "));
	}

	@Test
	public void testObjectArrayToStringHappyPath() throws Exception {
		String[] objs = new String[] { "one", "two", "three" };
		assertEquals("one,two,three", UtilBase.toString(objs));
	}

	@Test
	public void testObjectArrayToStringNullArray() throws Exception {
		String[] nullArray = null;
		assertEquals("", UtilBase.toString(nullArray));
		assertEquals("", UtilBase.toString(nullArray, null));
	}

	@Test
	public void testObjectArrayToStringEmptyArray() throws Exception {
		String[] emptyArray = new String[] {};
		assertEquals("", UtilBase.toString(emptyArray));
		assertEquals("", UtilBase.toString(emptyArray, null));
	}

	@Test
	public void testObjectArrayToStringNullElement() throws Exception {
		String[] objs = new String[] { "one", null, "three" };
		assertEquals("one,null,three", UtilBase.toString(objs));
	}

	@Test
	public void testObjectArrayToStringNonDefaultSeparator() throws Exception {
		String[] objs = new String[] { "one", "two", "three" };
		assertEquals("one : two : three", UtilBase.toString(objs, " : "));
	}

	@Test
	public void testStripHappyPath() throws Exception {
		assertEquals("onetwothree", UtilBase.strip("one,two,three", ","));
	}

	@Test
	public void testStripNullSource() throws Exception {
		assertNull(UtilBase.strip(null, ","));
	}

	@Test
	public void testStripNullToBeStripped() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", null));
	}

	@Test
	public void testStripEmptyToBeStripped() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", ""));
	}

	@Test
	public void testStripToBeStrippedNotFound() throws Exception {
		assertEquals("one,two,three", UtilBase.strip("one,two,three", ":"));
	}

	@Test
	public void testIsMemberForObjectWithValidValueAndArrayReturnsTrue() throws Exception {
		assertTrue(UtilBase.isMember(new Integer(10), new Integer[] { new Integer(10), new Integer(4) }));
	}

	@Test
	public void testIsMemberForObjectWithDiffTypeArrayReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember(new Integer(1), new Float[] { new Float(1.0f) }));
	}

	@Test
	public void testIsMemberForObjectWithNullValueThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(null, new Integer[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testIsMemberForObjectWithEmptyArrayValueReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember(new Integer(1), new Integer[0]));
	}

	@Test
	public void testIsMemberForObjectWithNullArrayThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(new Integer(1), null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testIsMemberForObjectWithNullInArrayIgnored() throws Exception {
		assertTrue(UtilBase.isMember(new Integer(8), new Integer[] { null, new Integer(8) }));
		assertFalse(UtilBase.isMember(new Integer(8), new Integer[] { new Integer(18), null }));
	}

	@Test
	public void testIsMemberForStringWithValidValueAndArrayReturnsTrue() throws Exception {
		assertTrue(UtilBase.isMember("str", new String[] { "abc", "str" }));
	}

	@Test
	public void testIsMemberForStringWithNullValueThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember(null, new String[0]);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testIsMemberForStringWithEmptyArrayValueReturnsFalse() throws Exception {
		assertFalse(UtilBase.isMember("str", new String[0]));
	}

	@Test
	public void testIsMemberForStringWithNullArrayThrowsNullPointerException() throws Exception {
		try {
			UtilBase.isMember("x", null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// success
		}
	}

	@Test
	public void testIsMemberForStringWithNullInArrayIgnored() throws Exception {
		assertFalse(UtilBase.isMember("x", new String[] { "a", null, "C" }));
		assertTrue(UtilBase.isMember("x", new String[] { null, "x" }));
	}

	@Test
	public void testNullSafeEquals() throws Exception {
		Object o = new Object();

		assertTrue(UtilBase.nullSafeEquals(null, null));
		assertFalse(UtilBase.nullSafeEquals(o, null));
		assertFalse(UtilBase.nullSafeEquals(null, o));
		assertTrue(UtilBase.nullSafeEquals(o, o));
	}

	@Test
	public void testContains() throws Exception {
		assertTrue(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4 }));
		assertTrue(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4, 5, 6, 7 }));
		assertFalse(UtilBase.contains(new int[] { 1, 2, 3, 4, 99 }, new int[] { 1, 2, 3, 4, 5, 6, 7 }));
		assertFalse(UtilBase.contains(new int[] { 1, 2, 3, 4 }, new int[] { 2, 3, 4, 5, 6, 7 }));
	}

}

package com.mindbox.pe.common.config;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class ConfigUtilTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("ConfigUtilTest Tests");
		suite.addTestSuite(ConfigUtilTest.class);
		return suite;
	}

	public ConfigUtilTest(String name) {
		super(name);
	}

	public void testToDateXMLStringToDate() throws Exception {
		Date date = new Date();
		String dateStr = ConfigUtil.toDateXMLString(date);
		assertNotNull(dateStr);
		assertEquals(date, ConfigUtil.toDate(dateStr));
	}

	public void testToDateXMLStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", ConfigUtil.toDateXMLString(null));
	}

	public void testToDateWithNullReturnsNull() throws Exception {
		assertNull(ConfigUtil.toDate(null));
	}

	public void testToDateWithEmptyStringReturnsNull() throws Exception {
		assertNull(ConfigUtil.toDate(""));
	}

	public void testToDateWithInvalidStringReturnsNull() throws Exception {
		try {
			assertNull(ConfigUtil.toDate("invalid.00:00:00"));
		}
		catch (Exception ex) {
			fail("toDate() should not throw exception even if invalid string is provided");
		}
	}

	public void testAsBooleanWithNullReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean(null));
	}

	public void testAsBooleanWithEmptyStringReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean(""));
	}

	public void testAsBooleanWithYesReturnsTrue() throws Exception {
		assertTrue(ConfigUtil.asBoolean("YES"));
		assertTrue(ConfigUtil.asBoolean("Yes"));
		assertTrue(ConfigUtil.asBoolean("yEs"));
		assertTrue(ConfigUtil.asBoolean("yeS"));
		assertTrue(ConfigUtil.asBoolean("YeS"));
		assertTrue(ConfigUtil.asBoolean("YEs"));
		assertTrue(ConfigUtil.asBoolean("yeS"));
		assertTrue(ConfigUtil.asBoolean("yes"));
	}

	public void testAsBooleanWithNoReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean("NO"));
		assertFalse(ConfigUtil.asBoolean("No"));
		assertFalse(ConfigUtil.asBoolean("nO"));
		assertFalse(ConfigUtil.asBoolean("no"));
	}

	public void testAsBooleanWithTrueReturnsTrue() throws Exception {
		assertTrue(ConfigUtil.asBoolean("TRUE"));
		assertTrue(ConfigUtil.asBoolean("TrUE"));
		assertTrue(ConfigUtil.asBoolean("TRuE"));
		assertTrue(ConfigUtil.asBoolean("TRUe"));
		assertTrue(ConfigUtil.asBoolean("trUE"));
		assertTrue(ConfigUtil.asBoolean("tRuE"));
		assertTrue(ConfigUtil.asBoolean("tRUe"));
		assertTrue(ConfigUtil.asBoolean("True"));
		assertTrue(ConfigUtil.asBoolean("tRue"));
		assertTrue(ConfigUtil.asBoolean("trUe"));
		assertTrue(ConfigUtil.asBoolean("truE"));
		assertTrue(ConfigUtil.asBoolean("true"));
	}
}

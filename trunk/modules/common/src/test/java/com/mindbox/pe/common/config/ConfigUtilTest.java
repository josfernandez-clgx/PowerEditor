package com.mindbox.pe.common.config;

import static com.mindbox.pe.unittest.UnitTestHelper.assertDateEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class ConfigUtilTest extends AbstractTestBase {

	@Test
	public void testToDateXMLStringToDate() throws Exception {
		Date date = new Date();
		String dateStr = ConfigUtil.toDateXMLString(date);
		assertNotNull(dateStr);
		assertDateEquals(date, ConfigUtil.toDate(dateStr));
	}

	@Test
	public void testToDateXMLStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", ConfigUtil.toDateXMLString(null));
	}

	@Test
	public void testToDateWithNullReturnsNull() throws Exception {
		assertNull(ConfigUtil.toDate(null));
	}

	@Test
	public void testToDateWithEmptyStringReturnsNull() throws Exception {
		assertNull(ConfigUtil.toDate(""));
	}

	@Test
	public void testToDateWithInvalidStringReturnsNull() throws Exception {
		try {
			assertNull(ConfigUtil.toDate("invalid.00:00:00"));
		}
		catch (Exception ex) {
			fail("toDate() should not throw exception even if invalid string is provided");
		}
	}

	@Test
	public void testAsBooleanWithNullReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean(null));
	}

	@Test
	public void testAsBooleanWithEmptyStringReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean(""));
	}

	@Test
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

	@Test
	public void testAsBooleanWithNoReturnsFalse() throws Exception {
		assertFalse(ConfigUtil.asBoolean("NO"));
		assertFalse(ConfigUtil.asBoolean("No"));
		assertFalse(ConfigUtil.asBoolean("nO"));
		assertFalse(ConfigUtil.asBoolean("no"));
	}

	@Test
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

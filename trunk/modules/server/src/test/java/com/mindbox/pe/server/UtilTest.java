package com.mindbox.pe.server;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.model.Constants;

public class UtilTest extends AbstractTestWithTestConfig {

	@Test
	public void testConvertCellValueToStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", Util.convertCellValueToString(null));
	}

	@Test
	public void testConvertCellValueToStringHappyCaseWithDate() throws Exception {
		Date date = new Date();
		assertEquals(Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(date), Util.convertCellValueToString(date));
	}

	@Test
	public void testConvertCellValueToStringHappyCaseWithNonDateObj() throws Exception {
		String str = createString();
		assertEquals(str, Util.convertCellValueToString(str));
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

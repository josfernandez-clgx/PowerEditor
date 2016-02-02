package com.mindbox.pe.server;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.config.UIConfiguration;

public class UtilTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("UtilTest Tests");
		suite.addTestSuite(UtilTest.class);
		return suite;
	}

	public UtilTest(String name) {
		super(name);
	}

	public void testConvertCellValueToStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", Util.convertCellValueToString(null));
	}
	
	public void testConvertCellValueToStringHappyCaseWithDate() throws Exception {
		Date date = new Date();
		assertEquals(UIConfiguration.FORMAT_DATE_TIME_SEC.format(date), Util.convertCellValueToString(date));
	}
	
	public void testConvertCellValueToStringHappyCaseWithNonDateObj() throws Exception {
		String str = ObjectMother.createString();
		assertEquals(str, Util.convertCellValueToString(str));
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

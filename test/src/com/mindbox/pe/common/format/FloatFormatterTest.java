package com.mindbox.pe.common.format;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.format.FloatFormatter;

public class FloatFormatterTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private FloatFormatter formatter;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FloatFormatterTest.class.getName());
		suite.addTestSuite(FloatFormatterTest.class);
		return suite;
	}

	public FloatFormatterTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		formatter = new FloatFormatter(TEST_PRECISION);
	}

	public void testDecorateNull() throws Exception {
		assertNull(formatter.decorate(null));
	}

	public void testNoPrecisionHappyCase() throws Exception {
		testPrecisionValWithPrecision(FloatFormatter.NO_PRECISION, new Double(10.123456789d), "10.123456789");
	}
	
	public void testDecorateNonempty() throws Exception {
		assertEquals("any string", formatter.decorate("any string"));
	}
	
	public void testPrecisionHappyPath() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.11), "1.11");
	}

	public void testPrecisionHappyPath_TT1821() throws Exception {
		testPrecisionValWithPrecision(2, new Double(1999999.01), "1,999,999.01");
	}

	public void testZeroPrecision() throws Exception {
		testPrecisionValWithPrecision(0, new Float(1.11), "1");
	}

	public void testPrecisionPad() throws Exception {
		testPrecisionValWithPrecision(3, new Float(1.11), "1.110");
	}

	public void testPrecisionTrimPrecision1() throws Exception {
		testPrecisionValWithPrecision(1, new Float(1.15), "1.1");
	}

	public void testPrecisionRoundPrecision1() throws Exception {
		testPrecisionValWithPrecision(1, new Float(1.16), "1.2");
	}

	public void testPrecisionTrimPrecision2() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.1149), "1.11");
	}

	public void testPrecisionRoundPrecision2() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.116), "1.12");
	}

	public void testLargeNumber() throws Exception {
		testPrecisionValWithPrecision(2, new Double(15000000.0), "15,000,000.00");
	}

	private void testPrecisionValWithPrecision(int precision, Number val, String expectedString) {
		FloatFormatter formatter = new FloatFormatter(precision);
		assertEquals(expectedString, formatter.format(val));
	}
}

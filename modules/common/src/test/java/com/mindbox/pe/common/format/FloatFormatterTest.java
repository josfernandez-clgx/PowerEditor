package com.mindbox.pe.common.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;


public class FloatFormatterTest extends AbstractTestBase {

	private static final int TEST_PRECISION = 2;

	private FloatFormatter formatter;

	@Before
	public void setUp() throws Exception {
		formatter = new FloatFormatter(TEST_PRECISION);
	}

	@Test
	public void testDecorateNull() throws Exception {
		assertNull(formatter.decorate(null));
	}

	@Test
	public void testNoPrecisionHappyCase() throws Exception {
		testPrecisionValWithPrecision(FloatFormatter.NO_PRECISION, new Double(10.123456789d), "10.123456789");
	}

	@Test
	public void testDecorateNonempty() throws Exception {
		assertEquals("any string", formatter.decorate("any string"));
	}

	@Test
	public void testPrecisionHappyPath() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.11), "1.11");
	}

	@Test
	public void testPrecisionHappyPath_TT1821() throws Exception {
		testPrecisionValWithPrecision(2, new Double(1999999.01), "1,999,999.01");
	}

	@Test
	public void testZeroPrecision() throws Exception {
		testPrecisionValWithPrecision(0, new Float(1.11), "1");
	}

	@Test
	public void testPrecisionPad() throws Exception {
		testPrecisionValWithPrecision(3, new Float(1.11), "1.110");
	}

	@Test
	public void testPrecisionTrimPrecision1() throws Exception {
		testPrecisionValWithPrecision(1, new Float(1.15), "1.1");
	}

	@Test
	public void testPrecisionRoundPrecision1() throws Exception {
		testPrecisionValWithPrecision(1, new Float(1.16), "1.2");
	}

	@Test
	public void testPrecisionTrimPrecision2() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.1149), "1.11");
	}

	@Test
	public void testPrecisionRoundPrecision2() throws Exception {
		testPrecisionValWithPrecision(2, new Float(1.116), "1.12");
	}

	@Test
	public void testLargeNumber() throws Exception {
		testPrecisionValWithPrecision(2, new Double(15000000.0), "15,000,000.00");
	}

	private void testPrecisionValWithPrecision(int precision, Number val, String expectedString) {
		FloatFormatter formatter = new FloatFormatter(precision);
		assertEquals(expectedString, formatter.format(val));
	}
}

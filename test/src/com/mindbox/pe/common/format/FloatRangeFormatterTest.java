package com.mindbox.pe.common.format;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.format.FloatRangeFormatter;
import com.mindbox.pe.model.table.FloatRange;

public class FloatRangeFormatterTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private static final Double posVal = new Double("1.23");
	private static final Double negVal = new Double("-1.23");
	
	private FloatRangeFormatter formatter;
	private FloatRange range;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FloatRangeFormatterTest.class.getName());
		suite.addTestSuite(FloatRangeFormatterTest.class);
		return suite;
	}

	public FloatRangeFormatterTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		formatter = new FloatRangeFormatter(TEST_PRECISION);
		
		range = new FloatRange();
		range.setLowerValue(posVal);
		range.setUpperValue(posVal);
	}

	public void testDecorateEmpty() throws Exception {
		assertNull(formatter.decorate(null));
		assertEquals("", formatter.decorate(""));
		assertEquals("any string", formatter.decorate("any string"));
	}

	public void testFormatNull() throws Exception {
		assertEquals("", formatter.format(null));
	}
	
	public void testFormatNullLo_NullHi() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(null);
		
		assertEquals("", formatter.format(range));
	}
	
	public void testFormatInclusive() throws Exception {
		range.setLowerValueInclusive(true);
		range.setUpperValueInclusive(true);
		
		assertEquals("[1.23-1.23]", formatter.format(range));
	}

	public void testFormatExclusive() throws Exception {
		range.setLowerValueInclusive(false);
		range.setUpperValueInclusive(false);
		
		assertEquals("(1.23-1.23)", formatter.format(range));
	}

	public void testFormatPositiveLo_PositiveHi() throws Exception {
		assertEquals("[1.23-1.23]", formatter.format(range));
	}

	public void testFormatNegativeLo_PositiveHi() throws Exception {
		range.setLowerValue(negVal);
		assertEquals("[-1.23-1.23]", formatter.format(range));
	}

	public void testFormatNegativeLo_NegativeHi() throws Exception {
		range.setLowerValue(negVal);
		range.setUpperValue(negVal);
		assertEquals("[-1.23--1.23]", formatter.format(range));
	}

	public void testFormatPositiveLo_EmptyHi() throws Exception {
		range.setUpperValue(null);
		assertEquals("[1.23- ", formatter.format(range));
	}

	public void testFormatNegativeLo_EmptyHi() throws Exception {
		range.setLowerValue(negVal);
		range.setUpperValue(null);
		assertEquals("[-1.23- ", formatter.format(range));
	}

	public void testFormatEmptyLo_PositiveHi() throws Exception {
		range.setLowerValue(null);
		assertEquals(" -1.23]", formatter.format(range));
	}

	public void testFormatEmptyLo_NegativeHi() throws Exception {
		range.setLowerValue(null);
		range.setUpperValue(negVal);
		assertEquals(" --1.23]", formatter.format(range));
	}
}

package com.mindbox.pe.common.format;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.format.CurrencyRangeFormatter;

public class CurrencyRangeFormatterTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private CurrencyRangeFormatter formatter;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CurrencyRangeFormatterTest.class.getName());
		suite.addTestSuite(CurrencyRangeFormatterTest.class);
		return suite;
	}

	public CurrencyRangeFormatterTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		formatter = new CurrencyRangeFormatter(TEST_PRECISION);
	}

	public void testDecorateEmpty() throws Exception {
		assertNull(formatter.decorate(null));
		assertEquals("", formatter.decorate(""));
	}

	public void testDecoratePositiveLo_PositiveHi() throws Exception {
		assertEquals("($1.23-$4.56)", formatter.decorate("(1.23-4.56)"));
	}

	public void testDecorateNegativeLo_PositiveHi() throws Exception {
		assertEquals("($-1.23-$4.56)", formatter.decorate("(-1.23-4.56)"));
	}

	public void testDecoratePositiveLo_NegativeHi() throws Exception {
		assertEquals("($1.23-$-4.56)", formatter.decorate("(1.23--4.56)"));
	}

	public void testDecorateNegativeLo_NegativeHi() throws Exception {
		assertEquals("($-1.23-$-4.56)", formatter.decorate("(-1.23--4.56)"));
	}

	public void testDecoratePositiveLo_EmptyHi() throws Exception {
		assertEquals("($1.23- ", formatter.decorate("(1.23- "));
	}

	public void testDecorateNegativeLo_EmptyHi() throws Exception {
		assertEquals("($-1.23- ", formatter.decorate("(-1.23- "));
	}

	public void testDecorateEmptyLo_PositiveHi() throws Exception {
		assertEquals(" -$4.56)", formatter.decorate(" -4.56)"));
	}

	public void testDecorateEmptyLo_NegativeHi() throws Exception {
		assertEquals(" -$-4.56)", formatter.decorate(" --4.56)"));
	}
}

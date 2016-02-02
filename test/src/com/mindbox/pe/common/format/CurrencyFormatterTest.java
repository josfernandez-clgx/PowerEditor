package com.mindbox.pe.common.format;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.common.format.CurrencyFormatter;

public class CurrencyFormatterTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private CurrencyFormatter formatter;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CurrencyFormatterTest.class.getName());
		suite.addTestSuite(CurrencyFormatterTest.class);
		return suite;
	}

	public CurrencyFormatterTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		formatter = new CurrencyFormatter(TEST_PRECISION);
	}

	public void testDecorateNull() throws Exception {
		assertNull(formatter.decorate(null));
	}

	public void testDecorateNonempty() throws Exception {
		assertEquals("$any string", formatter.decorate("any string"));
	}
}

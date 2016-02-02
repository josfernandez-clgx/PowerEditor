package com.mindbox.pe.client.common.grid;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.table.FloatRange;

public class CurrencyRangeCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private static final Double val = new Double("1.23");
	
	private FloatRange range;
	private CurrencyRangeCellRenderer renderer;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CurrencyRangeCellRendererTest.class.getName());
		suite.addTestSuite(CurrencyRangeCellRendererTest.class);
		return suite;
	}

	public CurrencyRangeCellRendererTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		renderer = new CurrencyRangeCellRenderer(TEST_PRECISION);
		range = new FloatRange();
		range.setLowerValue(val);
		range.setUpperValue(val);
	}

	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in CurrencyRangeFormatterTest
	// All other rendering tests in FloatRangeRendererTest
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(range, "[$1.23-$1.23]");
	}
	
	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

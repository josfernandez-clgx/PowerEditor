package com.mindbox.pe.client.common.grid;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class CurrencyCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private CurrencyCellRenderer renderer;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CurrencyCellRendererTest.class.getName());
		suite.addTestSuite(CurrencyCellRendererTest.class);
		return suite;
	}

	public CurrencyCellRendererTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		renderer = new CurrencyCellRenderer(TEST_PRECISION);
	}
	
	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in FloatFormatterTest
	// All other rendering tests in FloatRendererTest
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(new Float("1.1"), "$1.10");
	}
	
	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

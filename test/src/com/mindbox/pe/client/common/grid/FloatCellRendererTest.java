package com.mindbox.pe.client.common.grid;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class FloatCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private FloatCellRenderer renderer;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FloatCellRendererTest.class.getName());
		suite.addTestSuite(FloatCellRendererTest.class);
		return suite;
	}

	public FloatCellRendererTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		renderer = new FloatCellRenderer(TEST_PRECISION);
	}

	public void testSetValueNull() throws Exception {
		testSetValue(null, "");
	}
	
	public void testSetValueString() throws Exception {
		testSetValue("any string", "any string");
	}
	
	public void testExceptionCaught() throws Exception {
		testSetValue(new Object(), "Error");
	}
	
	public void testSetValueLargeValue() throws Exception {
		testSetValue(new Double(15000000.0), "15,000,000.00");
	}
	
	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in FloatFormatterTest
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(new Float("1.1"), "1.10");
	}
	
	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

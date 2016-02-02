package com.mindbox.pe.client.common.grid;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.table.FloatRange;

public class FloatRangeCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private static final Double val = new Double("1.23");
	
	private FloatRange range;
	private FloatRangeCellRenderer renderer;
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(FloatRangeCellRendererTest.class.getName());
		suite.addTestSuite(FloatRangeCellRendererTest.class);
		return suite;
	}

	public FloatRangeCellRendererTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		renderer = new FloatRangeCellRenderer(TEST_PRECISION);
		range = new FloatRange();
		range.setLowerValue(val);
		range.setUpperValue(val);
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
	
	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in FloatRangeFormatterTest
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(range, "[1.23-1.23]");
	}
	
	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

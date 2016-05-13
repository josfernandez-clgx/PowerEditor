package com.mindbox.pe.client.common.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class FloatCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private FloatCellRenderer renderer;


	@Before
	public void setUp() throws Exception {
		renderer = new FloatCellRenderer(TEST_PRECISION);
	}

	@Test
	public void testSetValueNull() throws Exception {
		testSetValue(null, "");
	}

	@Test
	public void testSetValueString() throws Exception {
		testSetValue("any string", "any string");
	}

	@Test
	public void testExceptionCaught() throws Exception {
		testSetValue(new Object(), "Error");
	}

	@Test
	public void testSetValueLargeValue() throws Exception {
		testSetValue(new Double(15000000.0), "15,000,000.00");
	}

	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in FloatFormatterTest
	@Test
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(new Float("1.1"), "1.10");
	}

	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

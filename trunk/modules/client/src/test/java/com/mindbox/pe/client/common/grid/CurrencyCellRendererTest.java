package com.mindbox.pe.client.common.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class CurrencyCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private CurrencyCellRenderer renderer;


	@Before
	public void setUp() throws Exception {
		renderer = new CurrencyCellRenderer(TEST_PRECISION);
	}

	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in FloatFormatterTest
	// All other rendering tests in FloatRendererTest
	@Test
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(new Float("1.1"), "$1.10");
	}

	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

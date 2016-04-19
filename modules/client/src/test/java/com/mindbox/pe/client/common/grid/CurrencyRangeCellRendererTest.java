package com.mindbox.pe.client.common.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.unittest.AbstractTestBase;

public class CurrencyRangeCellRendererTest extends AbstractTestBase {
	private static final int TEST_PRECISION = 2;
	private static final Double val = new Double("1.23");

	private FloatRange range;
	private CurrencyRangeCellRenderer renderer;


	@Before
	public void setUp() throws Exception {
		renderer = new CurrencyRangeCellRenderer(TEST_PRECISION);
		range = new FloatRange();
		range.setLowerValue(val);
		range.setUpperValue(val);
	}

	// Just a simple test of formatting here to prove formatter is called.  
	// All other formatting tests in CurrencyRangeFormatterTest
	// All other rendering tests in FloatRangeRendererTest
	@Test
	public void testSetValueFloatHappyPath() throws Exception {
		testSetValue(range, "[$1.23-$1.23]");
	}

	private void testSetValue(Object val, String expectedText) {
		renderer.setValue(val);
		assertEquals(expectedText, renderer.getText());
	}
}

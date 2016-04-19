package com.mindbox.pe.client.common.grid;

import org.junit.Before;
import org.junit.Test;

public class CurrencyCellEditorTest extends FloatCellEditorTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setEditor(new CurrencyCellEditor());
	}

	@Test
	public void testDollarSign() throws Exception {
		testEditTextSetsValue("$5.50", "$10.10", new Double("10.1"));
	}
}

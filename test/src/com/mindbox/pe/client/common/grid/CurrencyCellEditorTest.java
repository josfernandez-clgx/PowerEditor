package com.mindbox.pe.client.common.grid;

import junit.framework.TestSuite;

public class CurrencyCellEditorTest extends FloatCellEditorTest {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(CurrencyCellEditorTest.class.getName());
		suite.addTestSuite(CurrencyCellEditorTest.class);
		return suite;
	}

	public CurrencyCellEditorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		setEditor(new CurrencyCellEditor());
	}
	
	public void testDollarSign() throws Exception {
		testEditTextSetsValue("$5.50", "$10.10", new Double("10.1"));
	}
}

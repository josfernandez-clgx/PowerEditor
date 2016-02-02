package com.mindbox.pe.client.common.formatter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class SymbolDocumentFilterTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SymbolDocumentFilterTest.class.getName());
		suite.addTestSuite(SymbolDocumentFilterTest.class);
		return suite;
	}

	public SymbolDocumentFilterTest(String name) {
		super(name);
	}

	public void testInsertString() throws Exception {
		// TODO implement
	}
}

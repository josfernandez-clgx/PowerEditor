package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class ColumnMessageFragmentDigestTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ColumnMessageFragmentDigestTest Tests");
		suite.addTestSuite(ColumnMessageFragmentDigestTest.class);
		return suite;
	}

	private ColumnMessageFragmentDigest columnMessageFragmentDigest;

	public ColumnMessageFragmentDigestTest(String name) {
		super(name);
	}

	public void testCellSelectionDefaultsToDefaultKeyIfNotSet() throws Exception {
		assertEquals(AbstractMessageKeyList.TYPE_DEFAULT_KEY, columnMessageFragmentDigest.getCellSelection());
	}

	protected void setUp() throws Exception {
		super.setUp();
		columnMessageFragmentDigest = new ColumnMessageFragmentDigest();
	}

	protected void tearDown() throws Exception {
		// Tear downs for ColumnMessageFragmentDigestTest
		super.tearDown();
	}
}

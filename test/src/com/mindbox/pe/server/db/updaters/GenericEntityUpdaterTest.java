package com.mindbox.pe.server.db.updaters;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.db.DBTestBase;

public class GenericEntityUpdaterTest extends DBTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityUpdaterTest Tests");
		suite.addTestSuite(GenericEntityUpdaterTest.class);
		return suite;
	}

	public GenericEntityUpdaterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.clearGrids();
		super.tearDown();
	}

	/**
     * TODO Gaughan 8/22/2006: Complete implementation once framework is in place
	 * @throws Exception
	 */
	public void testUpdateGenericEntity() throws Exception {
	}

}

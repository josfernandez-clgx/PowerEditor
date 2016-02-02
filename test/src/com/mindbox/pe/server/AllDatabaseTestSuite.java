package com.mindbox.pe.server;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.db.AllDBTestSuite;

/**
 * Contains all tests that requires a valid DB connection. See
 * /test/config/PowerEditorConfiguation.xml file to DB connection settings required to run any test
 * in this suite. <b>Note: this suite is not part of normal unit test suite; this is run on as
 * needed based.</b>
 * 
 * @author Geneho Kim
 * @since 4.5.0
 */
public class AllDatabaseTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Database Tests");
		suite.addTest(AllDBTestSuite.suite());
		return suite;
	}
}

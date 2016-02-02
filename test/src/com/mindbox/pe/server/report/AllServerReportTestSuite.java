package com.mindbox.pe.server.report;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * Unit tests for com.mindbox.powereditor.server.report package.
 * @author Geneho Kim
 *
 */public class AllServerReportTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Report Tests");
		suite.addTest(ReportGeneratorTest.suite());
        suite.addTest(ReportGeneratorHelperTest.suite());        
		return suite;
	}

}

package com.mindbox.pe.server.webservices;

//import com.mindbox.pe.server.imexport.digest.AllImportExportDigestTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author nill
 * @author MindBox
 * @since PowerEditor 5.?.?
 */
public class AllWebServicesTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Web Service Tests");
		suite.addTest(WebServicesTest.suite());
		return suite;
	}

}

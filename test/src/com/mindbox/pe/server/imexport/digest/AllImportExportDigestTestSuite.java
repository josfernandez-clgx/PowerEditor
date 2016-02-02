package com.mindbox.pe.server.imexport.digest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllImportExportDigestTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All ImportExport Digest Tests");
		suite.addTest(ActivationDatesTest.suite());
		suite.addTest(CategoryDigestTest.suite());
		suite.addTest(CBRAttributeCreationFactoryTest.suite());
		suite.addTest(CBRAttributeTypeCreationFactoryTest.suite());
		suite.addTest(CBRCaseActionCreationFactoryTest.suite());
		suite.addTest(CBRCaseBaseCreationFactoryTest.suite());
		suite.addTest(CBRCaseClassCreationFactoryTest.suite());
		suite.addTest(CBRScoringFunctionCreationFactoryTest.suite());
		suite.addTest(CBRValueRangeCreationFactoryTest.suite());
		suite.addTest(EntityTest.suite());
		suite.addTest(ImportXMLDigesterTest.suite());
		suite.addTest(UserTest.suite());
		return suite;
	}

}

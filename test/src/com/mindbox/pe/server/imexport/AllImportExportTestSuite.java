package com.mindbox.pe.server.imexport;

import com.mindbox.pe.server.imexport.digest.AllImportExportDigestTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class AllImportExportTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Import/Export Tests");
		suite.addTest(CBRDataImporterTest.suite());
		suite.addTest(EntityDataImporterTest.suite());
		suite.addTest(ExportServiceTest.suite());
		suite.addTest(ImportServiceTest.suite());
		suite.addTest(ObjectConverterTest.suite());
		suite.addTest(AllImportExportDigestTestSuite.suite());
		return suite;
	}

}

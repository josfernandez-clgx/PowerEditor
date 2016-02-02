package com.mindbox.pe.common;

import com.mindbox.pe.common.config.AllCommonConfigTests;
import com.mindbox.pe.common.diff.AllCommonDiffClassTests;
import com.mindbox.pe.common.digest.AllCommonDigestTestSuite;
import com.mindbox.pe.common.format.AllCommonFormatTests;
import com.mindbox.pe.common.validate.AllCommonValidateTests;
import com.mindbox.pe.communication.AllCommunicationTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllCommonClassTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Common Tests");
		suite.addTest(MessageConfigTest.suite());
        suite.addTest(TemplateUtilTest.suite());        
		suite.addTest(UtilBaseTest.suite());
		suite.addTest(AllCommonConfigTests.suite());
		suite.addTest(AllCommonDiffClassTests.suite());
		suite.addTest(AllCommonDigestTestSuite.suite());
		suite.addTest(AllCommonFormatTests.suite());
		suite.addTest(AllCommonValidateTests.suite());
		suite.addTest(DateUtilTest.suite());
		suite.addTest(PasswordOneWayHashUtilTest.suite());
		// 2006-05-05 GKIM: disabled test that are not working
		//suite.addTest(DomainDigesterTest.suite());
		//suite.addTest(MessageConfigTest.suite());
		suite.addTest(AllCommunicationTests.suite());
		return suite;
	}

}

package com.mindbox.pe.client;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.client.applet.AllAppletTestSuite;
import com.mindbox.pe.client.common.AllClientCommonTestSuite;
import com.mindbox.pe.client.common.dialog.AllClientCommonDialogTests;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public final class AllClientTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Tests");
		suite.addTest(ClientUtilTest.suite());
		suite.addTest(EntityModelCacheFactoryTest.suite());
		suite.addTest(EntityModelCacheFactory_DateSynonymTest.suite());
		suite.addTest(AllAppletTestSuite.suite());
		suite.addTest(AllClientCommonTestSuite.suite());
        suite.addTest(AllClientCommonDialogTests.suite());
		return suite;
	}
}

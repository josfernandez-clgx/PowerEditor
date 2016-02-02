/*
 * Created on Sep 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.client.AllClientTestSuite;
import com.mindbox.pe.common.AllCommonClassTests;
import com.mindbox.pe.model.AllModelTestSuite;
import com.mindbox.pe.server.AllServerTestSuite;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
public final class AllPowerEditorTestSuite {

	public static final class GUI {
		public static void main(String[] args) {
			junit.textui.TestRunner.run(AllPowerEditorTestSuite.class);
		}
	}
	
	public static void main(String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All PowerEditor Tests");
		suite.addTest(AllCommonClassTests.suite());
		suite.addTest(AllModelTestSuite.suite());
		suite.addTest(AllServerTestSuite.suite());
		suite.addTest(AllClientTestSuite.suite());
		return suite;
	}
}

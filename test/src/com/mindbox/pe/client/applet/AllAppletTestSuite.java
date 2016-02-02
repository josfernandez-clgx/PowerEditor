/*
 * Created on Sep 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.client.applet.datesynonym.AllAppletDateSynonymTestSuite;
import com.mindbox.pe.client.applet.entities.AllAppletEntitiesTestSuite;
import com.mindbox.pe.client.applet.guidelines.AllAppletGuidelinesSuite;
import com.mindbox.pe.client.applet.validate.AllClientAppletValidateTestSuite;

/**
 * Contains tests for applet packages.
 * @author Gene Kim
 */
public final class AllAppletTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Applet Tests");
		suite.addTest(AllAppletEntitiesTestSuite.suite());
        suite.addTest(AllAppletDateSynonymTestSuite.suite());
        suite.addTest(AllAppletGuidelinesSuite.suite());
        suite.addTest(AllClientAppletValidateTestSuite.suite());
        
		return suite;
	}
}

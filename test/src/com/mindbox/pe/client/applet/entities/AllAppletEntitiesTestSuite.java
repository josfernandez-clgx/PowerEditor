package com.mindbox.pe.client.applet.entities;

import com.mindbox.pe.client.applet.entities.compatibility.AllAppletEntitiesCompatibilityTestSuite;
import com.mindbox.pe.client.applet.entities.generic.AllAppletEntitiesGenericTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllAppletEntitiesTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllAppletEntitiesTestSuite Tests");
		suite.addTest(EntityManagementButtonPanelTest.suite());
		suite.addTest(AllAppletEntitiesGenericTestSuite.suite());
        suite.addTest(AllAppletEntitiesCompatibilityTestSuite.suite());
		return suite;
	}

}

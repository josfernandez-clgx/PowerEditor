package com.mindbox.pe.client.applet.entities.compatibility;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllAppletEntitiesCompatibilityTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllAppletEntitiesCompatibilityTestSuite Tests");
		suite.addTest(CompatibilitySelectionTableModelTest.suite());
		return suite;
	}

}

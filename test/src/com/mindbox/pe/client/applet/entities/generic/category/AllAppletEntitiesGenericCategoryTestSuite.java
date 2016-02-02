package com.mindbox.pe.client.applet.entities.generic.category;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllAppletEntitiesGenericCategoryTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllAppletEntitiesGenericCategoryTestSuite Tests");
		suite.addTest(GenericCategoryEditDialogTest.suite());
        suite.addTest(CategoryToEntitySelectionTableModelTest.suite());
        suite.addTest(CategoryToEntityListPanelTest.suite());
        suite.addTest(CategoryToCategoryListPanelTest.suite());
        suite.addTest(CategoryToCategorySelectionTableModelTest.suite());        
		return suite;
	}

}

package com.mindbox.pe.client.applet.entities.generic;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.applet.entities.generic.category.AllAppletEntitiesGenericCategoryTestSuite;

public class AllAppletEntitiesGenericTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllAppletEntitiesGenericTestSuite Tests");
		suite.addTest(GenericEntityFilterPanelTest.suite());
		suite.addTest(GenericEntityUtilTest.suite());
        suite.addTest(GenericEntityToCategoryBulkAddDialogTest.suite());        
        suite.addTest(GenericEntityToCategoryPanelTest.suite());
        suite.addTest(GenericEntityToCategoryListPanelTest.suite());        
		suite.addTest(AllAppletEntitiesGenericCategoryTestSuite.suite());
		return suite;
	}

}

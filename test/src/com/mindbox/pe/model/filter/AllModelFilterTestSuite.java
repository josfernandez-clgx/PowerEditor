package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllModelFilterTestSuite  {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Model Filter Tests");
		suite.addTest(AbstractGenericCategorySearchFilterTest.suite());
		suite.addTest(AbstractGenericEntitySearchFilterTest.suite());
		suite.addTest(AbstractPersistentFilterSpecTest.suite());
		suite.addTest(AbstractSearchFilterTest.suite());
		suite.addTest(GenericEntityBasicSearchFilterTest.suite());
		suite.addTest(GenericEntityFilterSpecTest.suite());
		suite.addTest(GenericEntityPropertySearchFilterTest.suite());
		suite.addTest(GuidelineReportFilterTest.suite());
		suite.addTest(TemplateByNameVersionFilterTest.suite());
		return suite;
	}

}

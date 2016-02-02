package com.mindbox.pe.model.comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllModelComparatorTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AllModelComparatorTestSuite Tests");
		suite.addTest(ActivationsComparatorTest.suite());
		suite.addTest(DateSynonymComparatorByDateTest.suite());
		suite.addTest(GenericCategoryComparatorTest.suite());
		return suite;
	}

}

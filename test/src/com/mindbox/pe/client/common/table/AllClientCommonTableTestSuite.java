package com.mindbox.pe.client.common.table;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllClientCommonTableTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Common Table Tests");
		suite.addTest(AbstractCategoryEntityCacheTableModelTest.suite());
		suite.addTest(IDNameObjectSelectionTableTest.suite());
		return suite;
	}
}

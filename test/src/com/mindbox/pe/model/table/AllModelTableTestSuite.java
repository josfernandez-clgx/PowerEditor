package com.mindbox.pe.model.table;

import junit.framework.Test;
import junit.framework.TestSuite;

public final class AllModelTableTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Model Table Tests");
		suite.addTest(BooleanDataHelperTest.suite());
		suite.addTest(CategoryOrEntityValueTest.suite());
        suite.addTest(CategoryOrEntityValuesTest.suite());        
		suite.addTest(DateRangeTest.suite());
		suite.addTest(DateTimeRangeTest.suite());
		suite.addTest(DynamicStringValueTest.suite());
		suite.addTest(EnumValuesDataHelperTest.suite());
		suite.addTest(EnumValuesTest.suite());
		suite.addTest(FloatRangeTest.suite());
		suite.addTest(IntegerRangeTest.suite());
		suite.addTest(RangeTest.suite());
		suite.addTest(TimeRangeTest.suite());
		return suite;
	}
}

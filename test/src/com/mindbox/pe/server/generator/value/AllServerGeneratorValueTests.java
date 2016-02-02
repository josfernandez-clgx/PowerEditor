package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.server.generator.value.rhscolref.AllServerGeneratorValueRhsColRefTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllServerGeneratorValueTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("AllServerGeneratorValueTests Tests");
		suite.addTest(ActivationSpanValueSlotHelperTest.suite());
		suite.addTest(CategoryIDValueSlotHelperTest.suite());
		suite.addTest(CategoryNameValueSlotHelperTest.suite());
		suite.addTest(ColumnReferenceRHSValueSlotHelperTest.suite());
		suite.addTest(ComparisonOperatorHelperTest.suite());
		suite.addTest(ContextElementValueSlotHelperTest.suite());
		suite.addTest(EmptyOperatorHelperTest.suite());
		suite.addTest(EntityIDValueSlotHelperTest.suite());
		suite.addTest(EntityMatchFunctionOperatorHelperTest.suite());
		suite.addTest(EqualityOperatorHelperTest.suite());
		suite.addTest(LHSValueHelperFactoryTest.suite());
		suite.addTest(MembershipOperatorHelperTest.suite());
		suite.addTest(OperatorBasedLHSValueSlotHelperTest.suite());
		suite.addTest(RangeOperatorHelperTest.suite());
		suite.addTest(RuleIDValueSlotHelperTest.suite());
		suite.addTest(RuleNameValueSlotHelperTest.suite());
		suite.addTest(AllServerGeneratorValueRhsColRefTests.suite());
		return suite;
	}

}

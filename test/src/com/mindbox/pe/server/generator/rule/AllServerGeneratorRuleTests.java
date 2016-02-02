package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

public final class AllServerGeneratorRuleTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("AllServerGeneratorRuleTests Tests");
		suite.addTest(AbstractAttributePatternTest.suite());
		suite.addTest(AbstractAttributePatternListTest.suite());
		suite.addTest(AbstractLHSPatternListTest.suite());
		suite.addTest(AbstractObjectPatternTest.suite());
		suite.addTest(AbstractPatternValueSlotTest.suite());
		suite.addTest(AbstractValueSlotTest.suite());
		suite.addTest(ActivationSpanValueSlotTest.suite());
		suite.addTest(AttributePatternFactoryTest.suite());
		suite.addTest(CategoryIDValueSlotTest.suite());
		suite.addTest(CategoryNameValueSlotTest.suite());
		suite.addTest(CellValueValueSlotTest.suite());
		suite.addTest(ColumnReferencePatternValueSlotTest.suite());
		suite.addTest(ColumnRefValueSlotAttributePatternTest.suite());
		suite.addTest(ContextElementAttributePatternTest.suite());
		suite.addTest(ContextElementPatternValueSlotTest.suite());
		suite.addTest(ContextValueSlotTest.suite());
		suite.addTest(DatePropertyValueSlotTest.suite());
		suite.addTest(DefaultPatternFactoryHelperTest.suite());
		suite.addTest(EntityIDValueSlotTest.suite());
		suite.addTest(FocusOfAttentionAttributePatternTest.suite());
		suite.addTest(FocusOfAttentionPatternValueSlotTest.suite());
		suite.addTest(FunctionArgumentFactoryTest.suite());
		suite.addTest(FunctionCallPatternFactoryForRHSActionTest.suite());
		suite.addTest(GuidelineRuleTest.suite());
		suite.addTest(LHSPatternListFactoryTest.suite());
		suite.addTest(ObjectPatternFactoryTest.suite());
		suite.addTest(OptimizingLHSPatternListTest.suite());
		suite.addTest(OptimizingObjectPatternTest.suite());
		suite.addTest(RowNumberValueSlotTest.suite());
		suite.addTest(RuleIDValueSlotTest.suite());
		suite.addTest(RuleNameValueSlotTest.suite());
		suite.addTest(StaticTextAttributePatternTest.suite());
		suite.addTest(StringValuePatternValueSlotTest.suite());
		suite.addTest(StringValueSlotAttributePatternTest.suite());
		suite.addTest(TimeSliceAttributePatternTest.suite());
		suite.addTest(TimeSlicePatternValueSlotTest.suite());
		return suite;
	}

}

package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;

public class ActivationSpanValueSlotHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("ActivationSpanValueSlotHelperTest Tests");
		suite.addTestSuite(ActivationSpanValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public ActivationSpanValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueHappyCaseForSingleSpan() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType(), false);
		assertEquals("SINGLE", new ActivationSpanValueSlotHelper().generateValue(
				ruleParams,
				RuleObjectMother.createActivationSpanValueSlot()));
	}

	public void testGenerateValueHappyCaseForMultipleSpan() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType(), true);
		assertEquals("MULTIPLE", new ActivationSpanValueSlotHelper().generateValue(
				ruleParams,
				RuleObjectMother.createActivationSpanValueSlot()));
	}
}

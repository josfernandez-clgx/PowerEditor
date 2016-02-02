package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class RuleNameValueSlotHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("RuleNameValueSlotHelperTest Tests");
		suite.addTestSuite(RuleNameValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public RuleNameValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueHappyCase() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType());
		assertEquals(ruleParams.getName(), new RuleNameValueSlotHelper().generateValue(
				ruleParams,
				RuleObjectMother.createRuleNameValueSlot()));
	}

	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				new RuleNameValueSlotHelper(),
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createRuleNameValueSlot() });
	}
}

package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleNameValueSlotHelperTest extends AbstractTestBase {

	@Test
	public void testGenerateValueHappyCase() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType());
		assertEquals(ruleParams.getName(), new RuleNameValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	@Test
	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				new RuleNameValueSlotHelper(),
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createRuleNameValueSlot() });
	}
}

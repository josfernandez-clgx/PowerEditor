package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.unittest.AbstractTestBase;

public class ActivationSpanValueSlotHelperTest extends AbstractTestBase {

	@Test
	public void testGenerateValueHappyCaseForMultipleSpan() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType(), true);
		assertEquals("MULTIPLE", new ActivationSpanValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createActivationSpanValueSlot()));
	}

	@Test
	public void testGenerateValueHappyCaseForSingleSpan() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType(), false);
		assertEquals("SINGLE", new ActivationSpanValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createActivationSpanValueSlot()));
	}
}

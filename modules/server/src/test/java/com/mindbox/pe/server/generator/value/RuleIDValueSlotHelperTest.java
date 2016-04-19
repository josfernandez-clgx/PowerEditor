package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.attachGridTemplateColumns;
import static com.mindbox.pe.server.ServerTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleIDValueSlotHelperTest extends AbstractTestBase {

	@Test
	public void testGenerateValueHappyCaseWithNoRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType());
		assertEquals(RuleGeneratorHelper.AE_NIL, new RuleIDValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueHappyCaseWithSingleRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType());
		attachGridTemplateColumns(ruleParams.getTemplate(), 1);
		ruleParams.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(createInt());
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue);

		assertEquals(longValue.toString(), new RuleIDValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateValueHappyCaseWithMultipleRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams(createUsageType());
		attachGridTemplateColumns(ruleParams.getTemplate(), 3);
		ruleParams.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		ruleParams.getTemplate().getColumn(2).setDataSpecDigest(createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		ruleParams.getTemplate().getColumn(3).setDataSpecDigest(createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue1 = new Long(createInt());
		Long longValue2 = new Long(createInt());
		Long longValue3 = new Long(createInt());
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue1);
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue2);
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue3);

		assertEquals(
				"(create$ " + longValue1.toString() + " " + longValue2.toString() + " " + longValue3.toString() + ")",
				new RuleIDValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	@Test
	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				new RuleIDValueSlotHelper(),
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createRuleNameValueSlot() });
	}
}

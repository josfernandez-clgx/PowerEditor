package com.mindbox.pe.server.generator.value;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class RuleIDValueSlotHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("RuleIDValueSlotHelperTest Tests");
		suite.addTestSuite(RuleIDValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public RuleIDValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueHappyCaseWithNoRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType());
		assertEquals(RuleGeneratorHelper.AE_NIL, new RuleIDValueSlotHelper().generateValue(
				ruleParams,
				RuleObjectMother.createRuleNameValueSlot()));
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueHappyCaseWithSingleRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType());
		ObjectMother.attachGridTemplateColumns(ruleParams.getTemplate(), 1);
		ruleParams.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue = new Long(ObjectMother.createInt());
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue);

		assertEquals(
				longValue.toString(),
				new RuleIDValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	@SuppressWarnings("unchecked")
	public void testGenerateValueHappyCaseWithMultipleRuleID() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams(ObjectMother.createUsageType());
		ObjectMother.attachGridTemplateColumns(ruleParams.getTemplate(), 3);
		ruleParams.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		ruleParams.getTemplate().getColumn(2).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(2).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		ruleParams.getTemplate().getColumn(3).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		ruleParams.getTemplate().getColumn(3).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		Long longValue1 = new Long(ObjectMother.createInt());
		Long longValue2 = new Long(ObjectMother.createInt());
		Long longValue3 = new Long(ObjectMother.createInt());
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue1);
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue2);
		((List<Object>) ReflectionUtil.getPrivate(ruleParams, "rowData")).add(longValue3);

		assertEquals(
				"(create$ " + longValue1.toString() + " " + longValue2.toString() + " " + longValue3.toString() + ")",
				new RuleIDValueSlotHelper().generateValue(ruleParams, RuleObjectMother.createRuleNameValueSlot()));
	}

	public void testGenerateValueWithNullRuleParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(new RuleIDValueSlotHelper(), "generateValue", new Class[] {
				GuidelineGenerateParams.class,
				ValueSlot.class }, new Object[] { null, RuleObjectMother.createRuleNameValueSlot() });
	}
}

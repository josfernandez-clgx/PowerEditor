package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClassLink;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;

public class LHSPatternListFactoryTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("LHSPatternListFactoryTest Tests");
		suite.addTestSuite(LHSPatternListFactoryTest.class);
		return suite;
	}

	private PatternFactoryHelper helperMock;
	private MockControl mockControl;
	private TemplateUsageType usageType;
	private LHSPatternListFactory patternListFactory;

	public LHSPatternListFactoryTest(String name) {
		super(name);
	}

	public void testConstructorWithNullHelperThrowsNullPointerException() throws Exception {
		try {
			new LHSPatternListFactory(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testProduceWithNullRuleDefinitionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(
				patternListFactory,
				"produce",
				new Class[] { RuleDefinition.class, TemplateUsageType.class });
	}

	public void testProcessWithMathExpConditionSurroundedByConditionsOnSameAttrHappyCase() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_IS_EMPTY);
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.setOp(Condition.OP_GREATER);
		condition2.setValue(RuleElementFactory.getInstance().createValue("value", "+", condition1.getReference()));
		Condition condition3 = ObjectMother.createCondition();
		condition3.setReference(condition1.getReference());
		condition3.setOp(Condition.OP_LESS_EQUAL);
		condition3.setValue(RuleElementFactory.getInstance().createValue(condition2.getReference()));

		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference().getClassName()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 3);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName()), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference().getClassName()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName()), "?"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.replay();

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);
		ruleDefinition.add(condition3);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		// first pattern should be from condition 1
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
		assertEquals(" & :UNSPECIFIED", objectPattern.get(0).getValueText());
		// second pattern should be from condition 2
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition2.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
		assertEquals("(+ value ?" + condition1.getReference().getAttributeName() + ")", objectPattern.get(0).getValueSlot().getSlotValue());
		// thrid pattern should be from condition 3
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition3.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition3.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
	}

	/**
	 * Test for TT 1954
	 * @throws Exception
	 */
	public void testProcessWithExistInORWrappesExistPatternsWithAnd() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		CompoundLHSElement orElement = RuleElementFactory.getInstance().createOrCompoundCondition();
		orElement.add(existExpression1);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(orElement);

		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink1 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		// The first pattern should be OR
		LHSPatternList orList = (LHSPatternList) patternList.get(0);
		assertEquals(LHSPatternList.TYPE_OR, orList.getType());
		assertEquals(1, orList.size());

		// The first pattern in OR should be AND with exist pattern and condition pattern
		LHSPatternList andList = (LHSPatternList) orList.get(0);
		assertEquals(LHSPatternList.TYPE_AND, andList.getType());
		assertEquals(2, andList.size());

		ObjectPattern objectPattern = (ObjectPattern) andList.get(0);
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		objectPattern = (ObjectPattern) andList.get(1);
		assertEquals("pe:" + condition1.getReference().getClassName(), objectPattern.getClassName());
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
	}

	//	/**
	//	 * Test for TT 1953
	//	 * @throws Exception
	//	 */
	//	public void testProcessWithExistInNOTWritesExistPattenrsBeforeNot() throws Exception {
	//		ExistExpression existExpression1 = ObjectMother.createExistExpression();
	//		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
	//		condition1.setOp(Condition.OP_ANY_VALUE);
	//		existExpression1.getCompoundLHSElement().add(condition1);
	//
	//		CompoundLHSElement notElement = RuleElementFactory.getInstance().createNotCompoundCondition();
	//		notElement.add(existExpression1);
	//		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
	//		ruleDefinition.setUsageType(usageType);
	//		ruleDefinition.add(notElement);
	//
	//		mockControl.expectAndReturn(
	//				helperMock.getRuleGenerationConfiguration(usageType),
	//				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
	//				4);
	//		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
	//				+ existExpression1.getClassName());
	//		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
	//		DomainClassLink dcLink1 = new DomainClassLink();
	//		dcLink1.setParentName(existExpression1.getClassName());
	//		dcLink1.setChildName(condition1.getReference().getClassName());
	//		mockControl.expectAndReturn(
	//				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
	//				new DomainClassLink[] { dcLink1 });
	//		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
	//				+ condition1.getReference().getClassName());
	//		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
	//				+ condition1.getReference().getClassName());
	//		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
	//				+ condition1.getReference().getClassName());
	//		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
	//				+ condition1.getReference().getAttributeName());
	//		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
	//				+ condition1.getReference().getAttributeName());
	//		mockControl.replay();
	//
	//		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
	//		assertEquals(2, patternList.size());
	//
	//		// the first pattern must be from the exist expression
	//		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
	//		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
	//
	//		// the second pattern must be not with the condition in the exist expression
	//		LHSPatternList notList = (LHSPatternList) patternList.get(1);
	//		assertEquals(LHSPatternList.TYPE_NOT, notList.getType());
	//		assertEquals(1, notList.size());
	//		objectPattern = (ObjectPattern) notList.get(0);
	//		assertEquals("pe:" + condition1.getReference().getClassName(), objectPattern.getClassName());
	//		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
	//	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr1 == C2.Attr2
	 THEN
	 ...
	 * </pre>
	 * Expected result is two object patterns, and the pattern for
	 * <code>C2.Attr2</code> should come before the pattern for <code>C1.Attr1</code>.
	 * 
	 * @throws Exception on error
	 */
	public void testProcessForAttrComparisonCheckOrder() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_LESS);
		Reference ref2 = ObjectMother.createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(ref2), "pe:" + ref2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(ref2.getClassName()), "pe:" + ref2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getClassName()), "?" + ref2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName()), "?" + ref2.getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName(), null), "?" + ref2.getAttributeName(), 1);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + ref2.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr1 == C2.Attr2
	 C2.Attr3 == Some-Value
	 THEN
	 ...
	 * </pre>
	 * <b>where C1 is the class of the control pattern</b>.
	 * 
	 * Expected result is two object patterns, and the pattern for
	 * <code>C2</code> should be the first and should contain 2 attribute patterns.
	 * The pattern for <code>C1</code> should contain one more  attribute patterns
	 * than the number of generic entities configured.
	 * 
	 * @throws Exception on error
	 */
	public void testProcessForAttrComparisonAndConditionOnRefObjectCheckOrderForControlPattern() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());

		condition1.setOp(Condition.OP_EQUAL);
		Reference ref2 = ObjectMother.createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.getReference().setClassName(ref2.getClassName());
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("somevalue"));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		resetControlPatternConfigInvariants(true, condition1.getReference().getClassName(), null);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(
				helperMock.getDeployLabelForClass(ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getControlPatternConfig().getPatternClassName()),
				"pe:" + condition1.getReference().getClassName());
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		RuleGenerationConfiguration.ControlPatternConfig controlPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getControlPatternConfig();
		int addedTypeCount = 0;
		for (int i = 0; i < types.length; i++) {
			if (types[i].isUsedInContext() && !controlPatternConfig.isDisallowed(types[i])) {
				DomainAttribute domainAttribute = ObjectMother.createDomainAttribute();
				mockControl.expectAndReturn(
						helperMock.findDomainAttributeForContextElement(controlPatternConfig, types[i].getName()),
						domainAttribute);
				mockControl.expectAndReturn(helperMock.asVariableName(domainAttribute.getName()), "?" + domainAttribute.getName());
				++addedTypeCount;
			}
		}
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(ref2), "pe:" + ref2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(ref2.getClassName()), "pe:" + ref2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getClassName()), "?" + ref2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName()), "?" + ref2.getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName(), null), "?" + ref2.getAttributeName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName(), 1);
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition2.getReference()), false);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("pe:" + condition2.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(2, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("pe:" + condition1.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(1 + addedTypeCount, objectPattern.size());
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr1 == C2.Attr2
	 C2.Attr3 == Some-Value
	 THEN
	 ...
	 * </pre>
	 * <b>where C1 is the class of the request pattern</b>.
	 * 
	 * Expected result is two object patterns, and the pattern for
	 * <code>C2</code> should be the first and should contain 2 attribute patterns.
	 * The pattern for <code>C1</code> should contain 3 attribute patterns.
	 * 
	 * @throws Exception on error
	 */
	public void testProcessForAttrComparisonAndConditionOnRefObjectCheckOrderForRequestPattern() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());

		condition1.setOp(Condition.OP_EQUAL);
		Reference ref2 = ObjectMother.createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.getReference().setClassName(ref2.getClassName());
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("somevalue"));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		resetRequestPatternConfigInvariants(true, condition1.getReference().getClassName(), "pe:", true);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(ref2), "pe:" + ref2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(ref2.getClassName()), "pe:" + ref2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getClassName()), "?" + ref2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName()), "?" + ref2.getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName(), null), "?" + ref2.getAttributeName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName(), 1);
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition2.getReference()), false);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("pe:" + condition2.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(2, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("pe:" + condition1.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(3, objectPattern.size());
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr1 IS-ANY-VALUE AND
	 C2.Attr2 IS-ANY-VALUE AND
	 C1.Attr1 == C2.Attr2
	 THEN
	 ...
	 * </pre>
	 * Expected result is two object patterns, and the pattern for
	 * <code>C2.Attr2</code> must be the first pattern.
	 * The second pattern should be for the last condition in LHS.
	 * 
	 * @throws Exception on error
	 */
	public void testProcessForAttrComparisonReplacesIsAnyValueAndCheckOrder() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		Condition condition3 = ObjectMother.createCondition();
		condition3.setReference(condition1.getReference());
		condition3.setOp(Condition.OP_LESS);
		condition3.setValue(RuleElementFactory.getInstance().createValue(condition2.getReference()));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);
		ruleDefinition.add(condition3);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference().getClassName()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName()), "?"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr IS-NOT-EMPTY AND
	 C1.Attr == C2.Attr
	 THEN
	 ...
	 * </pre>
	 * Expected result is three object patterns, and the pattern for
	 * <code>C1</code> should have exactly one attribute pattern for <code>Attr</code>.
	 * <p>
	 * Note that condition for <code>C1.Attr</code> shouldn't matter, as long as it's not
	 * an empty condition.
	 * 
	 * @throws Exception on error
	 */
	public void testProcessForAttrComparisonAndAnotherConditionHappyCase() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_IS_NOT_EMPTY);
		Condition condition2 = ObjectMother.createCondition();
		condition2.setReference(condition1.getReference());
		condition2.setOp(Condition.OP_EQUAL);
		Reference ref2 = ObjectMother.createReference();
		ref2.setAttributeName(condition1.getReference().getAttributeName());
		condition2.setValue(RuleElementFactory.getInstance().createValue(ref2));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(ref2), "pe:" + ref2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(ref2.getClassName()), "pe:" + ref2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getClassName()), "?" + ref2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName()), "?" + ref2.getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(ref2.getAttributeName(), null), "?" + ref2.getAttributeName(), 1);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + ref2.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * <pre>
	 IF
	 C1.Attr > 0 AND
	 C1.Attr IS-NOT-EMPTY
	 THEN
	 ...
	 * </pre>
	 * Expected result is two object patterns, one with each condition..
	 * <p>
	 * Note that condition for <code>C1.Attr</code> shouldn't matter, as long as it's not
	 * an empty condition. Note: both conditions must not be empty
	 * 
	 * @throws Exception on error
	 */
	public void testProcessWithTwoNonEmptyConditionOfSameAttributeCreatesTwoObjectPatterns() throws Exception {
		// build rule definition
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_IS_NOT_EMPTY);
		Condition condition2 = ObjectMother.createCondition();
		condition2.setReference(condition1.getReference());
		condition2.setOp(Condition.OP_GREATER_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("0"));

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition2.getReference()), false);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("pe:" + condition1.getReference().getAttributeName(), objectPattern.get(0).getAttributeName());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("pe:" + condition2.getReference().getAttributeName(), objectPattern.get(0).getAttributeName());
	}

	public void testProcessHappyCaseForExistWithOneConditionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		ExistExpression existExpression = ObjectMother.createExistExpression();
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		existExpression.getCompoundLHSElement().add(condition2);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(existExpression);
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:"
				+ existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression.getClassName()), "?" + existExpression.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression.getClassName()),
				new DomainClassLink[] { dcLink });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());

		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
	}

	public void testProcessHappyCaseForTwoExistsWithOneConditionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				5);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink1 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName()), "?" + existExpression2.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink2 });


		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(4, patternList.size());
		// first pattern should be for the first exist
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		// second pattern should be for the condition in the first exist
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		// thrid for second exist
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + existExpression2.getClassName(), objectPattern.getVariableName());
		// 4th for the condition in the 2nd exist
		objectPattern = (ObjectPattern) patternList.get(3);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
	}

	public void testProcessHappyCaseForTwoExistsWithOneConditionAndWithExclusionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName(existExpression1.getClassName() + "-2");
		existExpression2.setExcludedObjectName("?" + existExpression1.getClassName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				5);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink1 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		//		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName(), "?" + existExpression2.getObjectName()), "?"
		//				+ existExpression2.getObjectName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink2 });
		mockControl.expectAndReturn(helperMock.formatForExcludedObject("?" + existExpression1.getClassName()), "& ~"
				+ existExpression2.getExcludedObjectName());


		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), "?"
				+ existExpression2.getObjectName()), "?" + existExpression2.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(4, patternList.size());
		// first pattern should be for the first exist
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		// second pattern should be for the condition in the first exist
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		// thrid for second exist
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals(
				"?" + existExpression2.getClassName() + "-2" + " & ~" + existExpression2.getExcludedObjectName(),
				objectPattern.getVariableName());
		// 4th for the condition in the 2nd exist
		objectPattern = (ObjectPattern) patternList.get(3);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
	}

	public void testProcessHappyCaseForOneExistWithObjNameWithConditionOnTheSameObj() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createStringValue());
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("OBJ" + ObjectMother.createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition1.getReference()), false);
		mockControl.replay();

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		// the only pattern should be for the first exist, merged with its condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
	}

	public void testProcessHappyCaseForOneExistWithObjNameWithAttrRefConditionOnDiffObj() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createReferenceValue());
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("OBJ" + ObjectMother.createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(((Reference) condition1.getValue()).getClassName());

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getLinkage(
				((Reference) condition1.getValue()).getClassName(),
				existExpression1.getClassName()), new DomainClassLink[] { dcLink1 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName()), "?"
				+ ((Reference) condition1.getValue()).getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue())), "pe:"
				+ ((Reference) condition1.getValue()).getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName()), "?"
				+ ((Reference) condition1.getValue()).getAttributeName(), 2);
		mockControl.replay();

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());
		// the first pattern should be for the reference
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + ((Reference) condition1.getValue()).getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// second pattern is for the referencefor the first exist, merged with its condition
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		// first attribute is for linkage
		assertEquals("?" + ((Reference) condition1.getValue()).getClassName(), objectPattern.get(0).getVariableName());
		// second attribute is from the condition
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(1).getVariableName());
	}

	public void testProcessHappyCaseForOneExistWithNoObjNameWithAttrRefConditionOnTheSameObj() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		Reference referenceForValue = ObjectMother.createReference(condition1.getReference().getClassName(), "attr"
				+ ObjectMother.createInt());
		condition1.setValue(RuleElementFactory.getInstance().createValue(referenceForValue));
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName()), "?"
				+ ((Reference) condition1.getValue()).getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue())), "pe:"
				+ ((Reference) condition1.getValue()).getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName()), "?"
				+ ((Reference) condition1.getValue()).getAttributeName(), 2);
		mockControl.replay();

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		// the only pattern should be for the reference + condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		// second pattern is for the referencefor the first exist, merged with its condition
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		// the first attribute is from the condition's reference value
		assertEquals("?" + referenceForValue.getAttributeName(), objectPattern.get(0).getVariableName());
		// the second attribute is from the condition
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(1).getVariableName());
	}

	public void testProcessHappyCaseForOneExistWithObjNameWithAttrRefConditionOnTheSameObj() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		Reference referenceForValue = ObjectMother.createReference(condition1.getReference().getClassName(), "attr"
				+ ObjectMother.createInt());
		condition1.setValue(RuleElementFactory.getInstance().createValue(referenceForValue));
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("OBJ" + ObjectMother.createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName()), "?"
				+ ((Reference) condition1.getValue()).getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue())), "pe:"
				+ ((Reference) condition1.getValue()).getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName()), "?"
				+ ((Reference) condition1.getValue()).getAttributeName(), 2);
		mockControl.replay();

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		// the only pattern should be for the reference + condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		// second pattern is for the referencefor the first exist, merged with its condition
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(2, objectPattern.size());
		// the first attribute is from the condition's reference value
		assertEquals("?" + referenceForValue.getAttributeName(), objectPattern.get(0).getVariableName());
		// the second attribute is from the condition
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(1).getVariableName());
	}

	public void testProcessHappyCaseForTwoExistsWithConditionOnTheSameObjAndWithExclusionChecksOrder() throws Exception {
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("?AOBJ1");
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = ObjectMother.createCondition();
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleObjectMother.createStringValue());
		condition2.setReference(ObjectMother.createReference(condition1.getReference().getClassName(), ObjectMother.createString()));
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setExcludedObjectName("?" + existExpression1.getClassName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				5);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink1 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink2 });
		mockControl.expectAndReturn(helperMock.formatForExcludedObject("?" + existExpression1.getClassName()), "& ~"
				+ existExpression2.getExcludedObjectName());
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition2.getReference()), false);


		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());
		// first pattern should be for the first exist, merged with its condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// second pattern should be for the the 2nd exist, merged with its condition
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals(
				"?" + existExpression2.getClassName() + " & ~" + existExpression2.getExcludedObjectName(),
				objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	public void testProcessHappyCaseForTwoExistsWithConditionWithTheSameObjAndAttrWithExclusionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createStringValue());
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("OBJ1");
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.add(existExpression1);

		Condition condition2 = ObjectMother.createCondition();
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue(condition1.getReference()));
		condition2.setReference(condition1.getReference());
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setExcludedObjectName(existExpression1.getObjectName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		// setup mock control
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				5);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.formatForExcludedObject(existExpression1.getObjectName()), "& ~?"
				+ existExpression2.getExcludedObjectName());

		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference().getClassName()), "pe:"
				+ condition1.getReference().getClassName(), 3);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName(), 3);
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName()), "?"
				+ condition1.getReference().getAttributeName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.isStringDeployTypeForAttribute(condition2.getReference()), false);
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());
		// first pattern should be for the first exist, merged with its condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
		assertEquals(condition1.getValue().toString(), objectPattern.get(0).getValueSlot().getSlotValue());

		// second pattern should be for the the 2nd exist, merged with its condition
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals(
				"?" + existExpression2.getClassName() + " & ~?" + existExpression2.getExcludedObjectName(),
				objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition2.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
		assertEquals(
				"&:(eq ?" + condition2.getReference().getAttributeName() + " ?" + condition2.getReference().getAttributeName() + ")",
				objectPattern.get(0).getValueText());
	}

	public void testProcessForExistHappyCaseWithMutipleDomainLinks() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);

		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		String linkClass = ObjectMother.createString();
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(linkClass);
		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(linkClass);
		dcLink2.setChildName(condition1.getReference().getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink1, dcLink2 });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(linkClass), "pe:" + linkClass);
		mockControl.expectAndReturn(helperMock.asVariableName(linkClass), "?" + linkClass, 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), null), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		// first pattern should be from the first exist expression with one link attribute
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals("?" + linkClass, attributePattern.getVariableName());

		// second pattern should be from linkClass to condition1
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + linkClass, objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		attributePattern = objectPattern.get(0);
		assertEquals("?" + condition1.getReference().getClassName(), attributePattern.getVariableName());

		// third pattern should be from condition1
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
	}

	public void testProcessForExistHappyCaseWithEmbeddedExist() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);
		existExpression1.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression1.getClassName());
		dcLink.setChildName(existExpression2.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(existExpression2.getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName()), "?" + existExpression2.getClassName(), 2);
		dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression2.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), null), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		// first pattern should be from the first exist expression to the second exist
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals("?" + existExpression2.getClassName(), attributePattern.getVariableName());

		// second pattern should be from the second exist expression to condition2
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression2.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		attributePattern = objectPattern.get(0);
		assertEquals("?" + condition2.getReference().getClassName(), attributePattern.getVariableName());

		// third pattern should be from condition1
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
	}

	public void testProcessForExistHappyCaseWithEmbeddedExistWithObjectName() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setObjectName("obj" + ObjectMother.createInt());
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);
		existExpression1.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression1.getClassName());
		dcLink.setChildName(existExpression2.getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(existExpression2.getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName()), "?" + existExpression2.getClassName(), 2);
		dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression2.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink });
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), "?"
				+ existExpression2.getObjectName()), "?" + existExpression2.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName(), 2);
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		// first pattern should be from the first exist expression to the second exist
		assertEquals("?" + existExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		AttributePattern attributePattern = objectPattern.get(0);
		assertEquals("?" + existExpression2.getObjectName(), attributePattern.getVariableName());

		// second pattern should be from the second exist expression to condition2
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		attributePattern = objectPattern.get(0);
		assertEquals("?" + condition2.getReference().getClassName(), attributePattern.getVariableName());

		// third pattern should be from condition1
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
	}

	public void testProcessForTwoExistWithEmbeddedExistWithObjectName() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("obj1");
		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName("obj2");
		existExpression2.setExcludedObjectName(existExpression1.getObjectName());

		ExistExpression parentExpression1 = ObjectMother.createExistExpression();
		parentExpression1.getCompoundLHSElement().add(existExpression1);
		ExistExpression parentExpression2 = ObjectMother.createExistExpression();
		parentExpression2.setClassName(parentExpression1.getClassName());
		parentExpression2.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(parentExpression1);
		ruleDefinition.add(parentExpression2);

		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(parentExpression1.getClassName());
		dcLink.setChildName(existExpression1.getClassName());
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(
				helperMock.getLinkage(existExpression1.getClassName(), parentExpression1.getClassName()),
				new DomainClassLink[] { dcLink }, 2);
		mockControl.expectAndReturn(helperMock.formatForExcludedObject(existExpression2.getExcludedObjectName()), "& ~" + existExpression2.getExcludedObjectName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName(), "?" + existExpression1.getObjectName()), "?"
				+ existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName(), "?" + existExpression2.getObjectName()), "?"
				+ existExpression2.getObjectName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(parentExpression1.getClassName()), "pe:"
				+ parentExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(parentExpression1.getClassName()), "?"
				+ parentExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(parentExpression2.getClassName()), "pe:"
				+ parentExpression2.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(parentExpression2.getClassName()), "?"
				+ parentExpression2.getClassName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(4, patternList.size());
		// first pattern is for parent expression
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + parentExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.get(0).getVariableName());
		// second pattern is for exist expression 1
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		// third pattern is for parent expression for exist expression 2
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + parentExpression1.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.get(0).getVariableName());
		// fourth pattern is for exist expression 2
		objectPattern = (ObjectPattern) patternList.get(3);
		assertEquals("?" + existExpression2.getObjectName() + " & ~" + existExpression2.getExcludedObjectName(), objectPattern.getVariableName());
	}

	public void testProcessForExistAndConditionOnSameClass() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("obj" + ObjectMother.createInt());
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		condition1.getReference().setClassName(existExpression1.getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				4);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName(), "?" + existExpression1.getObjectName()), "?"
				+ existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
	}

	public void testProcessForExistWithTwoCondsWithSameAttributeNameAndDiffVarNames() throws Exception {
		ExistExpression existExpression1 = ObjectMother.createExistExpression();
		existExpression1.setObjectName("obj" + ObjectMother.createInt());
		Condition condition1 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		ExistExpression existExpression2 = ObjectMother.createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName("obj" + ObjectMother.createInt());
		Condition condition2 = ObjectMother.attachReference(ObjectMother.createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);

		ExistExpression existExpression = ObjectMother.createExistExpression();
		existExpression.getCompoundLHSElement().add(existExpression1);
		existExpression.getCompoundLHSElement().add(existExpression2);

		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression.getClassName());
		dcLink1.setChildName(existExpression1.getClassName());

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression1.getClassName());
		dcLink2.setChildName(condition1.getReference().getClassName());
		DomainClassLink dcLink3 = new DomainClassLink();
		dcLink3.setParentName(existExpression2.getClassName());
		dcLink3.setChildName(condition2.getReference().getClassName());

		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression);
		mockControl.expectAndReturn(
				helperMock.getRuleGenerationConfiguration(usageType),
				ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault(),
				6);
		mockControl.expectAndReturn(
				helperMock.getLinkage(existExpression1.getClassName(), existExpression.getClassName()),
				new DomainClassLink[] { dcLink1 },
				2);
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
				new DomainClassLink[] { dcLink2 },
				1);
		mockControl.expectAndReturn(
				helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName()),
				new DomainClassLink[] { dcLink3 },
				1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression.getClassName()), "pe:"
				+ existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression.getClassName()), "?" + existExpression.getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
				+ existExpression1.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression1.getClassName()), "?" + existExpression1.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(existExpression2.getClassName()), "pe:"
				+ existExpression2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.asVariableName(existExpression2.getClassName()), "?" + existExpression2.getClassName(), 1);
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
				+ condition1.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getClassName(), "?"
				+ existExpression1.getObjectName()), "?" + existExpression1.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition1.getReference().getAttributeName(), null), "?"
				+ condition1.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForClass(condition2.getReference()), "pe:"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.getDeployLabelForAttribute(condition2.getReference()), "pe:"
				+ condition2.getReference().getAttributeName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName(), "?"
				+ existExpression2.getObjectName()), "?" + existExpression2.getObjectName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getClassName()), "?"
				+ condition2.getReference().getClassName());
		mockControl.expectAndReturn(helperMock.asVariableName(condition2.getReference().getAttributeName(), null), "?"
				+ condition2.getReference().getAttributeName());
		mockControl.replay();

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(6, patternList.size());
		// first pattern should be for existExpression to existExpression1
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.get(0).getVariableName());
		// second pattern should be for existExpression to existExpression2
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.get(0).getVariableName());
		// third pattern should be for existExpression1 to condition1
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// 4th pattern should be for condition 1
		objectPattern = (ObjectPattern) patternList.get(3);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// 5th pattern should be for existExpression2 to condition2
		objectPattern = (ObjectPattern) patternList.get(4);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// 6th pattern should be for condition 1
		objectPattern = (ObjectPattern) patternList.get(5);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	// TODO Kim: add tests for other scenarios, including a case where exist and condition refer to the same class

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		mockControl = MockControl.createControl(PatternFactoryHelper.class);
		helperMock = (PatternFactoryHelper) mockControl.getMock();
		usageType = TemplateUsageType.getAllInstances()[0];
		patternListFactory = new LHSPatternListFactory(helperMock);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

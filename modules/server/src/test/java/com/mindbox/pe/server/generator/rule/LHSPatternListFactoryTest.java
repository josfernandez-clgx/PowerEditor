package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.attachReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createCondition;
import static com.mindbox.pe.server.ServerTestObjectMother.createDomainAttribute;
import static com.mindbox.pe.server.ServerTestObjectMother.createExistExpression;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createRuleDefinition;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClassLink;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ControlPatternConfigHelper;

public class LHSPatternListFactoryTest extends AbstractTestWithTestConfig {

	private PatternFactoryHelper helperMock;
	private TemplateUsageType usageType;
	private LHSPatternListFactory patternListFactory;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		helperMock = createMock(PatternFactoryHelper.class);
		usageType = TemplateUsageType.getAllInstances()[0];
		patternListFactory = new LHSPatternListFactory(helperMock);
	}

	@Test
	public void testConstructorWithNullHelperThrowsNullPointerException() throws Exception {
		try {
			new LHSPatternListFactory(null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	/**
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr IS-NOT-EMPTY AND
	 * 	 C1.Attr == C2.Attr
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * Expected result is three object patterns, and the pattern for <code>C1</code> should have exactly one attribute
	 * pattern for <code>Attr</code>.
	 * <p>
	 * Note that condition for <code>C1.Attr</code> shouldn't matter, as long as it's not an empty condition.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessForAttrComparisonAndAnotherConditionHappyCase() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_IS_NOT_EMPTY);
		Condition condition2 = createCondition();
		condition2.setReference(condition1.getReference());
		condition2.setOp(Condition.OP_EQUAL);
		Reference ref2 = createReference();
		ref2.setAttributeName(condition1.getReference().getAttributeName());
		condition2.setValue(RuleElementFactory.getInstance().createValue(ref2));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(ref2)).andReturn("pe:" + ref2);
		expect(helperMock.getDeployLabelForClass(ref2.getClassName())).andReturn("pe:" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getClassName())).andReturn("?" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getAttributeName())).andReturn("?" + ref2.getAttributeName()).times(2);
		expect(helperMock.asVariableName(ref2.getAttributeName(), null)).andReturn("?" + ref2.getAttributeName());
		replay(helperMock);

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
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr1 == C2.Attr2
	 * 	 C2.Attr3 == Some-Value
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * <b>where C1 is the class of the control pattern</b>.
	 * 
	 * Expected result is two object patterns, and the pattern for <code>C2</code> should be the first and should
	 * contain 2 attribute patterns. The pattern for <code>C1</code> should contain one more attribute patterns than the
	 * number of generic entities configured.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessForAttrComparisonAndConditionOnRefObjectCheckOrderForControlPattern() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());

		condition1.setOp(Condition.OP_EQUAL);
		Reference ref2 = createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));
		Condition condition2 = attachReference(createCondition());
		condition2.getReference().setClassName(ref2.getClassName());
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("somevalue"));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		resetControlPatternConfigInvariants(true, condition1.getReference().getClassName(), null);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getControlPatternConfig().getPattern().getClazz())).andReturn(
				"pe:" + condition1.getReference().getClassName());
		GenericEntityType[] types = GenericEntityType.getAllGenericEntityTypes();
		ControlPatternConfigHelper controlPatternConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getControlPatternConfig();
		int addedTypeCount = 0;
		for (int i = 0; i < types.length; i++) {
			if (types[i].isUsedInContext() && !controlPatternConfig.isDisallowed(types[i])) {
				DomainAttribute domainAttribute = createDomainAttribute();
				expect(helperMock.findDomainAttributeForContextElement(controlPatternConfig, types[i].getName())).andReturn(domainAttribute);
				expect(helperMock.asVariableName(domainAttribute.getName())).andReturn("?" + domainAttribute.getName());
				++addedTypeCount;
			}
		}
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName()).times(1);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(ref2)).andReturn("pe:" + ref2);
		expect(helperMock.getDeployLabelForClass(ref2.getClassName())).andReturn("pe:" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getClassName())).andReturn("?" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getAttributeName())).andReturn("?" + ref2.getAttributeName()).times(2);
		expect(helperMock.asVariableName(ref2.getAttributeName(), null)).andReturn("?" + ref2.getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition2.getReference())).andReturn(false);
		replay(helperMock);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("pe:" + condition2.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(2, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("pe:" + condition1.getReference().getClassName(), objectPattern.getClassName());
		assertEquals(1 + addedTypeCount, objectPattern.size());
	}

	//	/**
	//	 * Test for TT 1953
	//	 * @throws Exception
	//	 */
	//	@Test public void testProcessWithExistInNOTWritesExistPattenrsBeforeNot() throws Exception {
	//		ExistExpression existExpression1 = createExistExpression();
	//		Condition condition1 = attachReference(createCondition());
	//		condition1.setOp(Condition.OP_ANY_VALUE);
	//		existExpression1.getCompoundLHSElement().add(condition1);
	//
	//		CompoundLHSElement notElement = RuleElementFactory.getInstance().createNotCompoundCondition();
	//		notElement.add(existExpression1);
	//		RuleDefinition ruleDefinition = createRuleDefinition();
	//		ruleDefinition.setUsageType(usageType);
	//		ruleDefinition.add(notElement);
	//
	//		expect(
	//				helperMock.getRuleGenerationConfiguration(usageType),
	//				ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper(),
	//				4);
	//		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName()), "pe:"
	//				+ existExpression1.getClassName());
	//		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn( "?" + existExpression1.getClassName());
	//		DomainClassLink dcLink1 = new DomainClassLink();
	//		dcLink1.setParentName(existExpression1.getClassName());
	//		dcLink1.setChildName(condition1.getReference().getClassName());
	//		expect(
	//				helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName()),
	//				new DomainClassLink[] { dcLink1 });
	//		expect(helperMock.getDeployLabelForClass(condition1.getReference()), "pe:"
	//				+ condition1.getReference().getClassName());
	//		expect(helperMock.asVariableName(condition1.getReference().getClassName()).andReturn( null), "?"
	//				+ condition1.getReference().getClassName());
	//		expect(helperMock.asVariableName(condition1.getReference().getClassName()), "?"
	//				+ condition1.getReference().getClassName());
	//		expect(helperMock.getDeployLabelForAttribute(condition1.getReference()), "pe:"
	//				+ condition1.getReference().getAttributeName());
	//		expect(helperMock.asVariableName(condition1.getReference().getAttributeName()).andReturn( null), "?"
	//				+ condition1.getReference().getAttributeName());
	//		replay(helperMock);
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
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr1 == C2.Attr2
	 * 	 C2.Attr3 == Some-Value
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * <b>where C1 is the class of the request pattern</b>.
	 * 
	 * Expected result is two object patterns, and the pattern for <code>C2</code> should be the first and should
	 * contain 2 attribute patterns. The pattern for <code>C1</code> should contain 3 attribute patterns.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessForAttrComparisonAndConditionOnRefObjectCheckOrderForRequestPattern() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());

		condition1.setOp(Condition.OP_EQUAL);
		Reference ref2 = createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));
		Condition condition2 = attachReference(createCondition());
		condition2.getReference().setClassName(ref2.getClassName());
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("somevalue"));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		resetRequestPatternConfigInvariants(true, condition1.getReference().getClassName(), "pe:", true);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName()).times(1);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(ref2)).andReturn("pe:" + ref2);
		expect(helperMock.getDeployLabelForClass(ref2.getClassName())).andReturn("pe:" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getClassName())).andReturn("?" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getAttributeName())).andReturn("?" + ref2.getAttributeName()).times(2);
		expect(helperMock.asVariableName(ref2.getAttributeName(), null)).andReturn("?" + ref2.getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition2.getReference())).andReturn(false);
		replay(helperMock);

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
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr1 == C2.Attr2
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * Expected result is two object patterns, and the pattern for <code>C2.Attr2</code> should come before the pattern
	 * for <code>C1.Attr1</code>.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessForAttrComparisonCheckOrder() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_LESS);
		Reference ref2 = createReference();
		condition1.setValue(RuleElementFactory.getInstance().createValue(ref2));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(ref2)).andReturn("pe:" + ref2);
		expect(helperMock.getDeployLabelForClass(ref2.getClassName())).andReturn("pe:" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getClassName())).andReturn("?" + ref2.getClassName());
		expect(helperMock.asVariableName(ref2.getAttributeName())).andReturn("?" + ref2.getAttributeName()).times(2);
		expect(helperMock.asVariableName(ref2.getAttributeName(), null)).andReturn("?" + ref2.getAttributeName());
		replay(helperMock);

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
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr1 IS-ANY-VALUE AND
	 * 	 C2.Attr2 IS-ANY-VALUE AND
	 * 	 C1.Attr1 == C2.Attr2
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * Expected result is two object patterns, and the pattern for <code>C2.Attr2</code> must be the first pattern. The
	 * second pattern should be for the last condition in LHS.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessForAttrComparisonReplacesIsAnyValueAndCheckOrder() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		Condition condition2 = attachReference(createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		Condition condition3 = createCondition();
		condition3.setReference(condition1.getReference());
		condition3.setOp(Condition.OP_LESS);
		condition3.setValue(RuleElementFactory.getInstance().createValue(condition2.getReference()));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);
		ruleDefinition.add(condition3);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);

		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition2.getReference().getClassName())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName()).times(1);
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName()).times(1);
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName()).times(1);
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName())).andReturn("?" + condition2.getReference().getAttributeName()).times(2);
		replay(helperMock);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());

		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());

		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	@Test
	public void testProcessForExistAndConditionOnSameClass() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("obj" + createInt());
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		condition1.getReference().setClassName(existExpression1.getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		replay(helperMock);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
	}

	@Test
	public void testProcessForExistHappyCaseWithEmbeddedExist() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		ExistExpression existExpression2 = createExistExpression();
		Condition condition2 = attachReference(createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);
		existExpression1.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression1.getClassName());
		dcLink.setChildName(existExpression2.getClassName());
		expect(helperMock.getLinkage(existExpression2.getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink });
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.asVariableName(existExpression2.getClassName())).andReturn("?" + existExpression2.getClassName()).times(2);
		dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression2.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink });
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

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

	@Test
	public void testProcessForExistHappyCaseWithEmbeddedExistWithObjectName() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setObjectName("obj" + createInt());
		Condition condition2 = attachReference(createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);
		existExpression1.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression1.getClassName());
		dcLink.setChildName(existExpression2.getClassName());
		expect(helperMock.getLinkage(existExpression2.getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink });
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.asVariableName(existExpression2.getClassName())).andReturn("?" + existExpression2.getClassName()).times(2);
		dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression2.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink });
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), "?" + existExpression2.getObjectName())).andReturn("?" + existExpression2.getObjectName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

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

	@Test
	public void testProcessForExistHappyCaseWithMutipleDomainLinks() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression1);

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		String linkClass = createString();
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(linkClass);
		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(linkClass);
		dcLink2.setChildName(condition1.getReference().getClassName());
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1, dcLink2 });
		expect(helperMock.getDeployLabelForClass(linkClass)).andReturn("pe:" + linkClass);
		expect(helperMock.asVariableName(linkClass)).andReturn("?" + linkClass).times(2);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		replay(helperMock);

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

	@Test
	public void testProcessForExistWithTwoCondsWithSameAttributeNameAndDiffVarNames() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("obj" + createInt());
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName("obj" + createInt());
		Condition condition2 = attachReference(createCondition());
		condition2.setOp(Condition.OP_ANY_VALUE);
		existExpression2.getCompoundLHSElement().add(condition2);

		ExistExpression existExpression = createExistExpression();
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

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(existExpression);
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(6);
		expect(helperMock.getLinkage(existExpression1.getClassName(), existExpression.getClassName())).andReturn(new DomainClassLink[] { dcLink1 }).times(2);
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink2 }).times(1);
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink3 }).times(1);
		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		expect(helperMock.asVariableName(existExpression.getClassName())).andReturn("?" + existExpression.getClassName());
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.asVariableName(existExpression2.getClassName())).andReturn("?" + existExpression2.getClassName());
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), "?" + existExpression2.getObjectName())).andReturn("?" + existExpression2.getObjectName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

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

	@Test
	public void testProcessForTwoExistWithEmbeddedExistWithObjectName() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("obj1");
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName("obj2");
		existExpression2.setExcludedObjectName(existExpression1.getObjectName());

		ExistExpression parentExpression1 = createExistExpression();
		parentExpression1.getCompoundLHSElement().add(existExpression1);
		ExistExpression parentExpression2 = createExistExpression();
		parentExpression2.setClassName(parentExpression1.getClassName());
		parentExpression2.getCompoundLHSElement().add(existExpression2);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(parentExpression1);
		ruleDefinition.add(parentExpression2);

		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(parentExpression1.getClassName());
		dcLink.setChildName(existExpression1.getClassName());
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getLinkage(existExpression1.getClassName(), parentExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink }).times(2);
		expect(helperMock.formatForExcludedObject(existExpression2.getExcludedObjectName())).andReturn("& ~" + existExpression2.getExcludedObjectName());
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.asVariableName(existExpression2.getClassName(), "?" + existExpression2.getObjectName())).andReturn("?" + existExpression2.getObjectName());
		expect(helperMock.getDeployLabelForClass(parentExpression1.getClassName())).andReturn("pe:" + parentExpression1.getClassName());
		expect(helperMock.asVariableName(parentExpression1.getClassName())).andReturn("?" + parentExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(parentExpression2.getClassName())).andReturn("pe:" + parentExpression2.getClassName());
		expect(helperMock.asVariableName(parentExpression2.getClassName())).andReturn("?" + parentExpression2.getClassName());
		replay(helperMock);

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

	@Test
	public void testProcessHappyCaseForExistWithOneConditionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = attachReference(createCondition());
		ExistExpression existExpression = createExistExpression();
		Condition condition2 = attachReference(createCondition());
		existExpression.getCompoundLHSElement().add(condition2);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(existExpression);
		DomainClassLink dcLink = new DomainClassLink();
		dcLink.setParentName(existExpression.getClassName());
		dcLink.setChildName(condition2.getReference().getClassName());

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(existExpression.getClassName())).andReturn("pe:" + existExpression.getClassName());
		expect(helperMock.asVariableName(existExpression.getClassName())).andReturn("?" + existExpression.getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression.getClassName())).andReturn(new DomainClassLink[] { dcLink });
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(3, patternList.size());
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + condition1.getReference().getClassName(), objectPattern.getVariableName());
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression.getClassName(), objectPattern.getVariableName());
		objectPattern = (ObjectPattern) patternList.get(2);
		assertEquals("?" + condition2.getReference().getClassName(), objectPattern.getVariableName());
	}

	@Test
	public void testProcessHappyCaseForOneExistWithNoObjNameWithAttrRefConditionOnTheSameObj() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		Reference referenceForValue = createReference(condition1.getReference().getClassName(), "attr" + createInt());
		condition1.setValue(RuleElementFactory.getInstance().createValue(referenceForValue));
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName())).andReturn("?" + ((Reference) condition1.getValue()).getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue()))).andReturn("pe:" + ((Reference) condition1.getValue()).getAttributeName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName())).andReturn("?" + ((Reference) condition1.getValue()).getAttributeName()).times(2);
		replay(helperMock);

		RuleDefinition ruleDefinition = createRuleDefinition();
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

	@Test
	public void testProcessHappyCaseForOneExistWithObjNameWithAttrRefConditionOnDiffObj() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createReferenceValue());
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("OBJ" + createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(((Reference) condition1.getValue()).getClassName());

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getLinkage(((Reference) condition1.getValue()).getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1 });
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName())).andReturn("?" + ((Reference) condition1.getValue()).getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue()))).andReturn("pe:" + ((Reference) condition1.getValue()).getAttributeName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName())).andReturn("?" + ((Reference) condition1.getValue()).getAttributeName()).times(2);
		replay(helperMock);

		RuleDefinition ruleDefinition = createRuleDefinition();
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

	@Test
	public void testProcessHappyCaseForOneExistWithObjNameWithAttrRefConditionOnTheSameObj() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		Reference referenceForValue = createReference(condition1.getReference().getClassName(), "attr" + createInt());
		condition1.setValue(RuleElementFactory.getInstance().createValue(referenceForValue));
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("OBJ" + createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(((Reference) condition1.getValue()).getClassName())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getClassName())).andReturn("?" + ((Reference) condition1.getValue()).getClassName()).times(2);
		expect(helperMock.getDeployLabelForAttribute(((Reference) condition1.getValue()))).andReturn("pe:" + ((Reference) condition1.getValue()).getAttributeName());
		expect(helperMock.asVariableName(((Reference) condition1.getValue()).getAttributeName())).andReturn("?" + ((Reference) condition1.getValue()).getAttributeName()).times(2);
		replay(helperMock);

		RuleDefinition ruleDefinition = createRuleDefinition();
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

	@Test
	public void testProcessHappyCaseForOneExistWithObjNameWithConditionOnTheSameObj() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createStringValue());
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("OBJ" + createInt());
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition1.getReference())).andReturn(false);
		replay(helperMock);

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(existExpression1);
		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(1, patternList.size());
		// the only pattern should be for the first exist, merged with its condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition1.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
	}

	@Test
	public void testProcessHappyCaseForTwoExistsWithConditionOnTheSameObjAndWithExclusionChecksOrder() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("?AOBJ1");
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = createCondition();
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleObjectMother.createStringValue());
		condition2.setReference(createReference(condition1.getReference().getClassName(), createString()));
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setExcludedObjectName("?" + existExpression1.getClassName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(5);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1 });
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink2 });
		expect(helperMock.formatForExcludedObject("?" + existExpression1.getClassName())).andReturn("& ~" + existExpression2.getExcludedObjectName());
		expect(helperMock.isStringDeployTypeForAttribute(condition2.getReference())).andReturn(false);

		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + existExpression2.getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

		LHSPatternList patternList = patternListFactory.produce(ruleDefinition, usageType);
		assertEquals(2, patternList.size());
		// first pattern should be for the first exist, merged with its condition
		ObjectPattern objectPattern = (ObjectPattern) patternList.get(0);
		assertEquals("?" + existExpression1.getObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		// second pattern should be for the the 2nd exist, merged with its condition
		objectPattern = (ObjectPattern) patternList.get(1);
		assertEquals("?" + existExpression2.getClassName() + " & ~" + existExpression2.getExcludedObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
	}

	@Test
	public void testProcessHappyCaseForTwoExistsWithConditionWithTheSameObjAndAttrWithExclusionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_EQUAL);
		condition1.setValue(RuleObjectMother.createStringValue());
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.setObjectName("OBJ1");
		existExpression1.setClassName(condition1.getReference().getClassName());
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(existExpression1);

		Condition condition2 = createCondition();
		condition2.setOp(Condition.OP_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue(condition1.getReference()));
		condition2.setReference(condition1.getReference());
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setExcludedObjectName(existExpression1.getObjectName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(5);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.formatForExcludedObject(existExpression1.getObjectName())).andReturn("& ~?" + existExpression2.getExcludedObjectName());

		expect(helperMock.getDeployLabelForClass(condition1.getReference().getClassName())).andReturn("pe:" + condition1.getReference().getClassName()).times(3);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), "?" + existExpression1.getObjectName())).andReturn("?" + existExpression1.getObjectName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(3);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName())).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + existExpression2.getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		expect(helperMock.isStringDeployTypeForAttribute(condition2.getReference())).andReturn(false);
		replay(helperMock);

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
		assertEquals("?" + existExpression2.getClassName() + " & ~?" + existExpression2.getExcludedObjectName(), objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		assertEquals("?" + condition2.getReference().getAttributeName(), objectPattern.get(0).getVariableName());
		assertEquals("&:(eq ?" + condition2.getReference().getAttributeName() + " ?" + condition2.getReference().getAttributeName() + ")", objectPattern.get(0).getValueText());
	}

	@Test
	public void testProcessHappyCaseForTwoExistsWithOneConditionAndWithExclusionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = attachReference(createCondition());
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = attachReference(createCondition());
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.setClassName(existExpression1.getClassName());
		existExpression2.setObjectName(existExpression1.getClassName() + "-2");
		existExpression2.setExcludedObjectName("?" + existExpression1.getClassName());
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(5);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1 });
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink2 });
		expect(helperMock.formatForExcludedObject("?" + existExpression1.getClassName())).andReturn("& ~" + existExpression2.getExcludedObjectName());

		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), "?" + existExpression2.getObjectName())).andReturn("?" + existExpression2.getObjectName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

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
		assertEquals("?" + existExpression2.getClassName() + "-2" + " & ~" + existExpression2.getExcludedObjectName(), objectPattern.getVariableName());
		// 4th for the condition in the 2nd exist
		objectPattern = (ObjectPattern) patternList.get(3);
		assertEquals("?" + existExpression2.getObjectName(), objectPattern.getVariableName());
	}

	@Test
	public void testProcessHappyCaseForTwoExistsWithOneConditionChecksOrder() throws Exception {
		// check the order of object patterns
		Condition condition1 = attachReference(createCondition());
		ExistExpression existExpression1 = createExistExpression();
		existExpression1.getCompoundLHSElement().add(condition1);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(existExpression1);
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());

		Condition condition2 = attachReference(createCondition());
		ExistExpression existExpression2 = createExistExpression();
		existExpression2.getCompoundLHSElement().add(condition2);
		ruleDefinition.add(existExpression2);

		DomainClassLink dcLink2 = new DomainClassLink();
		dcLink2.setParentName(existExpression2.getClassName());
		dcLink2.setChildName(condition2.getReference().getClassName());

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(5);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1 });
		expect(helperMock.getDeployLabelForClass(existExpression2.getClassName())).andReturn("pe:" + existExpression2.getClassName());
		expect(helperMock.asVariableName(existExpression2.getClassName())).andReturn("?" + existExpression2.getClassName());
		expect(helperMock.getLinkage(condition2.getReference().getClassName(), existExpression2.getClassName())).andReturn(new DomainClassLink[] { dcLink2 });

		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName());
		replay(helperMock);

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

	/**
	 * Test for TT 1954
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProcessWithExistInORWrappesExistPatternsWithAnd() throws Exception {
		ExistExpression existExpression1 = createExistExpression();
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_ANY_VALUE);
		existExpression1.getCompoundLHSElement().add(condition1);

		CompoundLHSElement orElement = RuleElementFactory.getInstance().createOrCompoundCondition();
		orElement.add(existExpression1);
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.setUsageType(usageType);
		ruleDefinition.add(orElement);

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(existExpression1.getClassName())).andReturn("pe:" + existExpression1.getClassName());
		expect(helperMock.asVariableName(existExpression1.getClassName())).andReturn("?" + existExpression1.getClassName());
		DomainClassLink dcLink1 = new DomainClassLink();
		dcLink1.setParentName(existExpression1.getClassName());
		dcLink1.setChildName(condition1.getReference().getClassName());
		expect(helperMock.getLinkage(condition1.getReference().getClassName(), existExpression1.getClassName())).andReturn(new DomainClassLink[] { dcLink1 });
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName());
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName());
		replay(helperMock);

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

	@Test
	public void testProcessWithMathExpConditionSurroundedByConditionsOnSameAttrHappyCase() throws Exception {
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_IS_EMPTY);
		Condition condition2 = attachReference(createCondition());
		condition2.setOp(Condition.OP_GREATER);
		condition2.setValue(RuleElementFactory.getInstance().createValue("value", "+", condition1.getReference()));
		Condition condition3 = createCondition();
		condition3.setReference(condition1.getReference());
		condition3.setOp(Condition.OP_LESS_EQUAL);
		condition3.setValue(RuleElementFactory.getInstance().createValue(condition2.getReference()));

		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(4);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition1.getReference().getClassName())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getClassName())).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(3);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName())).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForClass(condition2.getReference().getClassName())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName())).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition2.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName())).andReturn("?" + condition2.getReference().getAttributeName()).times(2);
		replay(helperMock);

		RuleDefinition ruleDefinition = createRuleDefinition();
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
	 * Tests {@link LHSPatternListFactory} handles the following:
	 * 
	 * <pre>
	 * 	 IF
	 * 	 C1.Attr > 0 AND
	 * 	 C1.Attr IS-NOT-EMPTY
	 * 	 THEN
	 * 	 ...
	 * </pre>
	 * 
	 * Expected result is two object patterns, one with each condition..
	 * <p>
	 * Note that condition for <code>C1.Attr</code> shouldn't matter, as long as it's not an empty condition. Note: both
	 * conditions must not be empty
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testProcessWithTwoNonEmptyConditionOfSameAttributeCreatesTwoObjectPatterns() throws Exception {
		// build rule definition
		Condition condition1 = attachReference(createCondition());
		condition1.setOp(Condition.OP_IS_NOT_EMPTY);
		Condition condition2 = createCondition();
		condition2.setReference(condition1.getReference());
		condition2.setOp(Condition.OP_GREATER_EQUAL);
		condition2.setValue(RuleElementFactory.getInstance().createValue("0"));

		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.add(condition1);
		ruleDefinition.add(condition2);

		// setup mock control
		expect(helperMock.getRuleGenerationConfiguration(usageType)).andReturn(ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper()).times(3);
		expect(helperMock.getDeployLabelForClass(condition1.getReference())).andReturn("pe:" + condition1.getReference().getClassName());
		expect(helperMock.asVariableName(condition1.getReference().getClassName(), null)).andReturn("?" + condition1.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition1.getReference().getAttributeName(), null)).andReturn("?" + condition1.getReference().getAttributeName()).times(2);
		expect(helperMock.getDeployLabelForClass(condition2.getReference())).andReturn("pe:" + condition2.getReference().getClassName());
		expect(helperMock.asVariableName(condition2.getReference().getClassName(), null)).andReturn("?" + condition2.getReference().getClassName());
		expect(helperMock.getDeployLabelForAttribute(condition1.getReference())).andReturn("pe:" + condition2.getReference().getAttributeName()).times(2);
		expect(helperMock.asVariableName(condition2.getReference().getAttributeName(), null)).andReturn("?" + condition2.getReference().getAttributeName()).times(2);
		expect(helperMock.isStringDeployTypeForAttribute(condition2.getReference())).andReturn(false);
		replay(helperMock);

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

	// TODO Kim: add tests for other scenarios, including a case where exist and condition refer to the same class

	@Test
	public void testProduceWithNullRuleDefinitionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(patternListFactory, "produce", new Class[] { RuleDefinition.class, TemplateUsageType.class });
	}
}

package com.mindbox.pe.server;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;

public class RuleDefinitionUtilTest extends AbstractTestBase {

	protected static void assertEquals(ExistExpression ee1, ExistExpression ee2) {
		assertEquals("", ee1, ee2);
	}

	/**
	 * Tests if the specifieid exist expressions are equal. Note: this does not check for conditions
	 * contained in the exist expressions. This only tests for express expression's properties.
	 * 
	 * @param message
	 *            failure message
	 * @param ee1
	 *            exist expression 1
	 * @param ee2
	 *            exist expression 2
	 */
	protected static void assertEquals(String message, ExistExpression ee1, ExistExpression ee2) {
		assertEquals(message + ": class mismatch", ee1.getClassName(), ee2.getClassName());
		assertEquals(message + ": object name mismatch", ee1.getObjectName(), ee2.getObjectName());
		assertEquals(message + ": excluded object mismatch", ee1.getExcludedObjectName(), ee2.getExcludedObjectName());
		assertEquals(message + ": comment mismatch", ee1.getComment(), ee2.getComment());
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(RuleDefinitionUtilTest.class);
		suite.setName("RuleDefinitionUtil Tests");
		return suite;
	}

	public RuleDefinitionUtilTest(String name) {
		super(name);
	}

	/**
	 * Test TT1373: Allow the use of & in the ExistsWithExcluding.
	 * 
	 * @throws Exception
	 *             on error
	 */
	public void testTT1373() throws Exception {
		logBegin();

		RuleDefinition ruleDef = ObjectMother.createRuleDefinition();
		ExistExpression existExpression = RuleElementFactory.getInstance().createExistExpression("testClass");
		existExpression.setObjectName("object1");
		existExpression.setExcludedObjectName("obj1 & obj2 & obj3");
		ruleDef.add(existExpression);

		String ruleDefStr = RuleDefinitionUtil.toString(ruleDef);
		logger.debug("ruleDefStr = " + ruleDefStr);

		RuleDefinition rdFromStr = RuleDefinitionUtil.parseToRuleDefinition(ruleDefStr, null);
		assertNotNull(rdFromStr);
		CompoundLHSElement rootElement = rdFromStr.getRootElement();
		assertNotNull(rootElement);
		ExistExpression eeFromStr = (ExistExpression) rootElement.get(0);
		assertNotNull(eeFromStr);
		assertEquals(existExpression, eeFromStr);

		logEnd();
	}

	public void testParseToRuleDefinitionWithNullStringReturnsNull() throws Exception {
		assertNull(RuleDefinitionUtil.parseToRuleDefinition(null));
	}

	public void testParseToRuleDefinitionWithEmptyStringReturnsNull() throws Exception {
		assertNull(RuleDefinitionUtil.parseToRuleDefinition("  "));
	}

	public void testParseToRuleDefinitionUsesActionIDMap() throws Exception {
		ActionTypeDefinition typeDefinition1 = ObjectMother.createActionTypeDefinition();
		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(typeDefinition1);

		ActionTypeDefinition typeDefinition2 = ObjectMother.createActionTypeDefinition();

		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put("action:" + typeDefinition2.getID(), new Integer(typeDefinition1.getID()));

		RuleAction ruleAction = RuleElementFactory.getInstance().createRuleAction();
		ruleAction.setActionType(typeDefinition2);
		ruleAction.setName(typeDefinition2.getName());
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		ruleDefinition.updateAction(ruleAction);

		String ruleDefStr = RuleDefinitionUtil.toString(ruleDefinition);

		RuleDefinition rdFromStr = RuleDefinitionUtil.parseToRuleDefinition(ruleDefStr, idMap);
		assertEquals(typeDefinition1.getID(), rdFromStr.getActionTypeID());
	}

	public void testParseToRuleDefinitionForConditionWithObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		Condition condition = ObjectMother.createCondition();
		condition.setObjectName(ObjectMother.createString());
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));
		ruleDefinition.add(condition);

		String ruleStr = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		RuleDefinition ruleDefinition2 = RuleDefinitionUtil.parseToRuleDefinition(ruleStr);

		assertEquals(1, ruleDefinition2.sizeOfRootElements());
		LHSElement element = ruleDefinition2.getRootElementAt(0);
		assertTrue(element instanceof Condition);
		assertEquals(condition, ((Condition) element));
	}

	public void testParseToRuleDefinitionForConditionWithoutObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();
		Condition condition = ObjectMother.createCondition();
		condition.setName(ObjectMother.createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));
		ruleDefinition.add(condition);

		String ruleStr = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		RuleDefinition ruleDefinition2 = RuleDefinitionUtil.parseToRuleDefinition(ruleStr);

		assertEquals(1, ruleDefinition2.sizeOfRootElements());
		LHSElement element = ruleDefinition2.getRootElementAt(0);
		assertTrue(element instanceof Condition);
		assertEquals(condition, ((Condition) element));
	}
}

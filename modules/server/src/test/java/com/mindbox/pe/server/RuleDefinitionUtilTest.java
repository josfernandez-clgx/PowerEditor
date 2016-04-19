package com.mindbox.pe.server;

import static com.mindbox.pe.server.ServerTestAssert.assertConditionEquals;
import static com.mindbox.pe.server.ServerTestAssert.assertExistExpressionEquals;
import static com.mindbox.pe.server.ServerTestObjectMother.createActionTypeDefinition;
import static com.mindbox.pe.server.ServerTestObjectMother.createCondition;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createRuleDefinition;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.LHSElement;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleDefinitionUtilTest extends AbstractTestBase {

	/**
	 * Test TT1373: Allow the use of & in the ExistsWithExcluding.
	 * 
	 * @throws Exception on error
	 */
	@Test
	public void testTT1373() throws Exception {
		logBegin("TT1373");

		RuleDefinition ruleDef = createRuleDefinition();
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
		assertExistExpressionEquals(existExpression, eeFromStr);

		logEnd("TT1373");
	}

	@Test
	public void testParseToRuleDefinitionWithNullStringReturnsNull() throws Exception {
		assertNull(RuleDefinitionUtil.parseToRuleDefinition(null));
	}

	@Test
	public void testParseToRuleDefinitionWithEmptyStringReturnsNull() throws Exception {
		assertNull(RuleDefinitionUtil.parseToRuleDefinition("  "));
	}

	@Test
	public void testParseToRuleDefinitionUsesActionIDMap() throws Exception {
		ActionTypeDefinition typeDefinition1 = createActionTypeDefinition();
		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(typeDefinition1);

		ActionTypeDefinition typeDefinition2 = createActionTypeDefinition();

		Map<String, Integer> idMap = new HashMap<String, Integer>();
		idMap.put("action:" + typeDefinition2.getID(), new Integer(typeDefinition1.getID()));

		RuleAction ruleAction = RuleElementFactory.getInstance().createRuleAction();
		ruleAction.setActionType(typeDefinition2);
		ruleAction.setName(typeDefinition2.getName());
		RuleDefinition ruleDefinition = createRuleDefinition();
		ruleDefinition.updateAction(ruleAction);

		String ruleDefStr = RuleDefinitionUtil.toString(ruleDefinition);

		RuleDefinition rdFromStr = RuleDefinitionUtil.parseToRuleDefinition(ruleDefStr, idMap);
		assertEquals(typeDefinition1.getID(), rdFromStr.getActionTypeID());
	}

	@Test
	public void testParseToRuleDefinitionForConditionWithObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = createRuleDefinition();
		Condition condition = createCondition();
		condition.setObjectName(createString());
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));
		ruleDefinition.add(condition);

		String ruleStr = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		RuleDefinition ruleDefinition2 = RuleDefinitionUtil.parseToRuleDefinition(ruleStr);

		assertEquals(1, ruleDefinition2.sizeOfRootElements());
		LHSElement element = ruleDefinition2.getRootElementAt(0);
		assertTrue(element instanceof Condition);
		assertConditionEquals(condition, ((Condition) element));
	}

	@Test
	public void testParseToRuleDefinitionForConditionWithoutObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = createRuleDefinition();
		Condition condition = createCondition();
		condition.setName(createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));
		ruleDefinition.add(condition);

		String ruleStr = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		RuleDefinition ruleDefinition2 = RuleDefinitionUtil.parseToRuleDefinition(ruleStr);

		assertEquals(1, ruleDefinition2.sizeOfRootElements());
		LHSElement element = ruleDefinition2.getRootElementAt(0);
		assertTrue(element instanceof Condition);
		assertConditionEquals(condition, ((Condition) element));
	}
}

package com.mindbox.pe.server;

import static com.mindbox.pe.server.ServerTestObjectMother.createCondition;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createRuleDefinition;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.EOL;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleDefinitionStringWriterTest extends AbstractTestBase {

	@Test
	public void testWriteAsStringWithNullRuleDefThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleDefinitionStringWriter.class, "writeAsString", new Class[] { RuleDefinition.class });
	}

	@Test
	public void testWriteAsStringForConditionWithObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = createRuleDefinition();

		Condition condition = createCondition();
		condition.setObjectName(createString());
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		ruleDefinition.add(condition);

		RuleDefinitionStringWriter.writeAsString(ruleDefinition);

		testWriteAsStringForFirstCondition("<Condition>" + EOL + "          <Reference>" + EOL + "            <Class>"
				+ condition.getReference().getClassName() + "</Class>" + EOL + "            <Attribute>"
				+ condition.getReference().getAttributeName() + "</Attribute>" + EOL + "          </Reference>" + EOL + "          <Operator>"
				+ Condition.OPSTR_EQUAL + "</Operator>" + EOL + "          <Value>" + EOL + condition.getValue().toString() + "</Value>" + EOL
				+ "          <Comment>" + condition.getComment() + "</Comment>" + EOL + "          <ObjectName>" + condition.getObjectName()
				+ "</ObjectName>" + EOL + "        </Condition>", ruleDefinition);
	}

	@Test
	public void testWriteAsStringForConditionWithoutObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = createRuleDefinition();

		Condition condition = createCondition();
		condition.setName(createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		ruleDefinition.add(condition);

		RuleDefinitionStringWriter.writeAsString(ruleDefinition);

		testWriteAsStringForFirstCondition("<Condition>" + EOL + "          <Reference>" + EOL + "            <Class>"
				+ condition.getReference().getClassName() + "</Class>" + EOL + "            <Attribute>"
				+ condition.getReference().getAttributeName() + "</Attribute>" + EOL + "          </Reference>" + EOL + "          <Operator>"
				+ Condition.OPSTR_EQUAL + "</Operator>" + EOL + "          <Value>" + EOL + condition.getValue().toString() + "</Value>" + EOL
				+ "          <Comment></Comment>" + EOL + "        </Condition>", ruleDefinition);
	}

	private void testWriteAsStringForFirstCondition(String expected, RuleDefinition ruleDefinition) throws Exception {
		String result = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		int index1 = result.indexOf("<Condition>");
		int index2 = result.indexOf("</Condition>");
		assertTrue("Condition tag not found", index1 > 0 && index2 > index1);
		assertEquals(expected, result.substring(index1, index2 + 12));
	}

}

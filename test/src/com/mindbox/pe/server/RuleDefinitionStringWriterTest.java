package com.mindbox.pe.server;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;

public class RuleDefinitionStringWriterTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RuleDefinitionStringWriterTest Tests");
		suite.addTestSuite(RuleDefinitionStringWriterTest.class);
		return suite;
	}

	public RuleDefinitionStringWriterTest(String name) {
		super(name);
	}

	public void testWriteAsStringWithNullRuleDefThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleDefinitionStringWriter.class, "writeAsString", new Class[]
			{ RuleDefinition.class});
	}

	public void testWriteAsStringForConditionWithObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();

		Condition condition = ObjectMother.createCondition();
		condition.setObjectName(ObjectMother.createString());
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		ruleDefinition.add(condition);

		RuleDefinitionStringWriter.writeAsString(ruleDefinition);

		testWriteAsStringForFirstCondition("<Condition>" + EOL+
				"          <Reference>" + EOL +
				"            <Class>" + condition.getReference().getClassName()+"</Class>"+EOL+
				"            <Attribute>" + condition.getReference().getAttributeName()+"</Attribute>"+EOL+
				"          </Reference>"+EOL+
				"          <Operator>" + Condition.OPSTR_EQUAL+"</Operator>"+EOL+
				"          <Value>" + EOL + condition.getValue().toString() + "</Value>" + EOL +
				"          <Comment>" + condition.getComment() + "</Comment>" + EOL+
				"          <ObjectName>" + condition.getObjectName() + "</ObjectName>" + EOL +
				"        </Condition>", ruleDefinition);
	}

	public void testWriteAsStringForConditionWithoutObjectNameHappyCase() throws Exception {
		RuleDefinition ruleDefinition = ObjectMother.createRuleDefinition();

		Condition condition = ObjectMother.createCondition();
		condition.setName(ObjectMother.createString());
		condition.setOp(Condition.OP_EQUAL);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		ruleDefinition.add(condition);

		RuleDefinitionStringWriter.writeAsString(ruleDefinition);

		testWriteAsStringForFirstCondition("<Condition>" + EOL+
				"          <Reference>" + EOL +
				"            <Class>" + condition.getReference().getClassName()+"</Class>"+EOL+
				"            <Attribute>" + condition.getReference().getAttributeName()+"</Attribute>"+EOL+
				"          </Reference>"+EOL+
				"          <Operator>" + Condition.OPSTR_EQUAL+"</Operator>"+EOL+
				"          <Value>" + EOL + condition.getValue().toString() + "</Value>" + EOL +
				"          <Comment></Comment>" + EOL+
				"        </Condition>", ruleDefinition);
	}

	private void testWriteAsStringForFirstCondition(String expected, RuleDefinition ruleDefinition) throws Exception {
		String result = RuleDefinitionStringWriter.writeAsString(ruleDefinition);
		int index1 = result.indexOf("<Condition>");
		int index2 = result.indexOf("</Condition>");
		assertTrue("Condition tag not found", index1 > 0 && index2 > index1);
		assertEquals(expected, result.substring(index1, index2 + 12));
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for RuleDefinitionStringWriterTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for RuleDefinitionStringWriterTest
		super.tearDown();
	}
}

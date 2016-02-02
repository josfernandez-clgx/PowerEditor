package com.mindbox.pe.model.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.GuidelineActionProvider;

public class RuleElementFactoryTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RuleElementFactoryTest Tests");
		suite.addTestSuite(RuleElementFactoryTest.class);
		return suite;
	}

	public RuleElementFactoryTest(String name) {
		super(name);
	}

	public void testAsCopyStringWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "asCopyString", new Class[] { Condition.class });
	}

	public void testAsCopyStringWithObjectNameHappyCase() throws Exception {
		Condition condition = ObjectMother.createCondition();
		condition.setObjectName(ObjectMother.createString());
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(3);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		assertEquals("C[" + condition.getReference().getClassName() + '.' + condition.getReference().getAttributeName() + "||"
				+ Condition.Aux.toOpString(condition.getOp()) + "||" + condition.getValue().toString() + "||" + condition.getComment()
				+ "||" + condition.getObjectName() + ']', str);
	}

	public void testAsCopyStringWithoutObjectNameHappyCase() throws Exception {
		Condition condition = ObjectMother.createCondition();
		condition.setName(ObjectMother.createString());
		condition.setOp(3);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		assertEquals("C[" + condition.getReference().getClassName() + '.' + condition.getReference().getAttributeName() + "||"
				+ Condition.Aux.toOpString(condition.getOp()) + "||" + condition.getValue().toString() + "||]", str);
	}

	public void testDeepCopyConditionWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "deepCopyCondition", new Class[] { Condition.class });
	}

	public void testDeepCopyConditionHappyCase() throws Exception {
		Condition condition = ObjectMother.createCondition();
		condition.setObjectName(ObjectMother.createString());
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(3);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		Condition condition2 = RuleElementFactory.deepCopyCondition(condition);
		assertEquals(condition, condition2);
	}

	public void testToLHSElementWithNullStringThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "toLHSElement", new Class[] { String.class, DomainClassProvider.class, GuidelineActionProvider.class });
	}

	public void testToLHSElementForConditionWithObjectNameHappyCase() throws Exception {
		Condition condition = ObjectMother.createCondition();
		condition.setObjectName(ObjectMother.createString());
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(3);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		LHSElement element = RuleElementFactory.toLHSElement(str, null, null);
		assertTrue(element instanceof Condition);
		assertEquals(condition, (Condition) element);
	}

	public void testToLHSElementForConditionWithoutObjectNameHappyCase() throws Exception {
		Condition condition = ObjectMother.createCondition();
		condition.setComment(ObjectMother.createString());
		condition.setName(ObjectMother.createString());
		condition.setOp(3);
		condition.setReference(ObjectMother.createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(ObjectMother.createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		LHSElement element = RuleElementFactory.toLHSElement(str, null, null);
		assertTrue(element instanceof Condition);
		assertEquals(condition, (Condition) element);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for RuleElementFactoryTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for RuleElementFactoryTest
		super.tearDown();
	}
}

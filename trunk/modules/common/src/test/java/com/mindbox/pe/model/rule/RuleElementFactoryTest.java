package com.mindbox.pe.model.rule;

import static com.mindbox.pe.common.CommonTestAssert.assertEquals;
import static com.mindbox.pe.common.CommonTestObjectMother.createCondition;
import static com.mindbox.pe.common.CommonTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.GuidelineActionProvider;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RuleElementFactoryTest extends AbstractTestBase {

	@Test
	public void testAsCopyStringWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "asCopyString", new Class[] { Condition.class });
	}

	@Test
	public void testAsCopyStringWithObjectNameHappyCase() throws Exception {
		Condition condition = createCondition();
		condition.setObjectName(createString());
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(3);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		assertEquals(
				"C[" + condition.getReference().getClassName() + '.' + condition.getReference().getAttributeName() + "||"
						+ Condition.Aux.toOpString(condition.getOp()) + "||" + condition.getValue().toString() + "||" + condition.getComment() + "||"
						+ condition.getObjectName() + ']',
				str);
	}

	@Test
	public void testAsCopyStringWithoutObjectNameHappyCase() throws Exception {
		Condition condition = createCondition();
		condition.setName(createString());
		condition.setOp(3);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		assertEquals(
				"C[" + condition.getReference().getClassName() + '.' + condition.getReference().getAttributeName() + "||"
						+ Condition.Aux.toOpString(condition.getOp()) + "||" + condition.getValue().toString() + "||]",
				str);
	}

	@Test
	public void testDeepCopyConditionHappyCase() throws Exception {
		Condition condition = createCondition();
		condition.setObjectName(createString());
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(3);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		Condition condition2 = RuleElementFactory.deepCopyCondition(condition);
		assertEquals(condition, condition2);
	}

	@Test
	public void testDeepCopyConditionWithNullConditionThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "deepCopyCondition", new Class[] { Condition.class });
	}

	@Test
	public void testToLHSElementForConditionWithObjectNameHappyCase() throws Exception {
		Condition condition = createCondition();
		condition.setObjectName(createString());
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(3);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		LHSElement element = RuleElementFactory.toLHSElement(str, null, null);
		assertTrue(element instanceof Condition);
		assertEquals(condition, (Condition) element);
	}

	@Test
	public void testToLHSElementForConditionWithoutObjectNameHappyCase() throws Exception {
		Condition condition = createCondition();
		condition.setComment(createString());
		condition.setName(createString());
		condition.setOp(3);
		condition.setReference(createReference());
		condition.setValue(RuleElementFactory.getInstance().createConditionValue(createString(), null));

		String str = RuleElementFactory.asCopyString(condition);

		LHSElement element = RuleElementFactory.toLHSElement(str, null, null);
		assertTrue(element instanceof Condition);
		assertEquals(condition, (Condition) element);
	}

	@Test
	public void testToLHSElementWithNullStringThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(RuleElementFactory.class, "toLHSElement", new Class[] { String.class, DomainClassProvider.class,
				GuidelineActionProvider.class });
	}

}

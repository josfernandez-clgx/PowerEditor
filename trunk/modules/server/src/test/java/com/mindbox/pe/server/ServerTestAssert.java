package com.mindbox.pe.server;

import static com.mindbox.pe.unittest.UnitTestHelper.equalsNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.server.model.GenericEntityIdentity;

public class ServerTestAssert {

	public static void assertCommentEquals(RuleElement e1, RuleElement e2) {
		assertCommentEquals("", e1, e2);
	}

	public static void assertCommentEquals(String message, RuleElement e1, RuleElement e2) {
		assertTrue(message + "; comments do not match for " + e1 + "," + e2, equalsNullOrEmpty(e1.getComment(), e2.getComment()));
	}

	public static void assertConditionEquals(Condition condition1, Condition condition2) {
		assertConditionEquals("", condition1, condition2);
	}

	public static void assertConditionEquals(String message, Condition condition1, Condition condition2) {
		assertEquals(message + "; object name mismatch", condition1.getObjectName(), condition2.getObjectName());
		assertEquals(message + "; operator mismatch", condition1.getOp(), condition2.getOp());
		assertEquals(message + "; reference mismatch", condition1.getReference(), condition2.getReference());
		assertEquals(message + "; value mismatch", condition1.getValue().toString(), condition2.getValue().toString());
		assertCommentEquals(message, condition1, condition2);
	}

	public static void assertExistExpressionEquals(ExistExpression ee1, ExistExpression ee2) {
		assertExistExpressionEquals("", ee1, ee2);
	}

	/**
	 * Tests if the specifieid exist expressions are equal. Note: this does not check for conditions contained in the
	 * exist expressions. This only tests for express expression's properties.
	 * 
	 * @param message failure message
	 * @param ee1 exist expression 1
	 * @param ee2 exist expression 2
	 */
	public static void assertExistExpressionEquals(String message, ExistExpression ee1, ExistExpression ee2) {
		assertEquals(message + ": class mismatch", ee1.getClassName(), ee2.getClassName());
		assertEquals(message + ": object name mismatch", ee1.getObjectName(), ee2.getObjectName());
		assertEquals(message + ": excluded object mismatch", ee1.getExcludedObjectName(), ee2.getExcludedObjectName());
		assertEquals(message + ": comment mismatch", ee1.getComment(), ee2.getComment());
	}

	public static void assertGenericEntityIdentityEquals(GenericEntityIdentity id1, GenericEntityIdentity id2) {
		assertGenericEntityIdentityEquals("", id1, id2);
	}

	public static void assertGenericEntityIdentityEquals(String message, GenericEntityIdentity id1, GenericEntityIdentity id2) {
		assertEquals(message + "; id mismatch", id1.getEntityID(), id2.getEntityID());
		assertEquals(message + "; type mismatch", id1.getEntityType(), id2.getEntityType());
	}

	private ServerTestAssert() {
	}
}

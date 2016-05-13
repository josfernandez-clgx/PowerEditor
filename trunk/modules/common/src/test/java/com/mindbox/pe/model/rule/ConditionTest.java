package com.mindbox.pe.model.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

/**
 * @author Dan Gaughan
 * @since PowerEditor 5.0.0
 */
public class ConditionTest extends AbstractTestBase {

	@Test
	public void testToOpIntForEntityMatchFunction() throws Exception {
		assertEquals(Condition.Aux.toOpInt(Condition.OPSTR_ENTITY_MATCH_FUNC), Condition.OP_ENTITY_MATCH_FUNC);
	}

	@Test
	public void testToOpIntForEntityNotMatchFunction() throws Exception {
		assertEquals(Condition.Aux.toOpInt(Condition.OPSTR_NOT_ENTITY_MATCH_FUNC), Condition.OP_NOT_ENTITY_MATCH_FUNC);
	}

	@Test
	public void testToOpStrForEntityMatchFunction() throws Exception {
		assertEquals(Condition.Aux.toOpString(Condition.OP_ENTITY_MATCH_FUNC), Condition.OPSTR_ENTITY_MATCH_FUNC);
	}

}

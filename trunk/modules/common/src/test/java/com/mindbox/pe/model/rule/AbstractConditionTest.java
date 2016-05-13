package com.mindbox.pe.model.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractConditionTest extends AbstractTestBase {

	private static class TestImpl extends AbstractCondition {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7276213826267259370L;

		protected TestImpl(String dispName, int op) {
			super(dispName, op);
		}

	}

	@Test
	public void testToStringWithNullValueOrRefDoesNotThrowsException() throws Exception {
		AbstractCondition condition = new TestImpl(createString(), 1);
		condition.setReference(null);
		condition.setValue(null);
		assertNotNull(condition.toString());
	}

}

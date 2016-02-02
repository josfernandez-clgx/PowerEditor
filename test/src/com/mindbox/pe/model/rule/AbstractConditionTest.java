package com.mindbox.pe.model.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class AbstractConditionTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractConditionTest Tests");
		suite.addTestSuite(AbstractConditionTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractCondition {

		protected TestImpl(String dispName, int op) {
			super(dispName, op);
		}

	}

	public AbstractConditionTest(String name) {
		super(name);
	}

	public void testToStringWithNullValueOrRefDoesNotThrowsException() throws Exception {
		AbstractCondition condition = new TestImpl(ObjectMother.createString(), 1);
		condition.setReference(null);
		condition.setValue(null);
		assertNotNull(condition.toString());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

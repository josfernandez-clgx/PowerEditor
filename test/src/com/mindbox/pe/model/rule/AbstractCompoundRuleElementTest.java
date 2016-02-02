package com.mindbox.pe.model.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class AbstractCompoundRuleElementTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractCompoundRuleElementTest Tests");
		suite.addTestSuite(AbstractCompoundRuleElementTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("serial")
	private static class TestImpl extends AbstractCompoundRuleElement<RuleElement> {
		protected TestImpl() {
			super("Display-" + ObjectMother.createString());
		}
	}

	private TestImpl testImpl;

	public AbstractCompoundRuleElementTest(String name) {
		super(name);
	}

	public void testGetWithInvalidIndexThrowsIndexOutOfBoundsException() throws Exception {
		assertThrowsException(testImpl, "get", new Class[] { int.class }, new Object[] { new Integer(-1) }, IndexOutOfBoundsException.class);
		assertThrowsException(
				testImpl,
				"get",
				new Class[] { int.class },
				new Object[] { new Integer(testImpl.size()) },
				IndexOutOfBoundsException.class);
		assertThrowsException(
				testImpl,
				"get",
				new Class[] { int.class },
				new Object[] { new Integer(testImpl.size() + 1) },
				IndexOutOfBoundsException.class);
	}

	protected void setUp() throws Exception {
		testImpl = new TestImpl();
		super.setUp();
	}
}

package com.mindbox.pe.model.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractCompoundRuleElementTest extends AbstractTestBase {

	@SuppressWarnings("serial")
	private static class TestImpl extends AbstractCompoundRuleElement<RuleElement> {
		protected TestImpl() {
			super("Display-" + createString());
		}
	}

	private TestImpl testImpl;

	@Before
	public void setUp() throws Exception {
		testImpl = new TestImpl();
	}

	@Test
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
}

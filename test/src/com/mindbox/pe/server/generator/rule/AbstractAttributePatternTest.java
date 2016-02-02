package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class AbstractAttributePatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractAttributePatternTest Tests");
		suite.addTestSuite(AbstractAttributePatternTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractAttributePattern {

		protected TestImpl(String attributeName, String varName, boolean hasValueSlot, String valueText, ValueSlot valueSlot) {
			super(attributeName, varName, hasValueSlot, valueText, valueSlot);
		}

	}

	private TestImpl testImpl;

	public AbstractAttributePatternTest(String name) {
		super(name);
	}

	public void testConsturctorWithNullVariableNameThrowsNullPointerException() throws Exception {
		try {
			new TestImpl("attr", null, true, "", null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	public void testHasSameValueWithNullPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasSameValue", new Class[]
			{ AttributePattern.class});
	}

	public void testHasSameValuePositiveCase() throws Exception {
		assertTrue(testImpl.hasSameValue(new TestImpl("a", "b", false, testImpl.getValueText(), null)));
		assertTrue(new TestImpl("a", "b", true, null, new FocusOfAttentionPatternValueSlot()).hasSameValue(new TestImpl(
				"a2",
				"b2",
				true,
				null,
				new FocusOfAttentionPatternValueSlot())));
	}

	public void hasSameValueNegativeCase() throws Exception {
		assertTrue(testImpl.hasSameValue(new TestImpl("a", "b", true, testImpl.getValueText(), null)));
		assertTrue(testImpl.hasSameValue(new TestImpl("a", "b", false, "", null)));
	}

	public void testIsEmptyPositiveCaseForEmptyOrNullText() throws Exception {
		assertTrue(new TestImpl("attr", "var", false, "", null).isEmpty());
		assertTrue(new TestImpl("attr", "var", false, null, null).isEmpty());
	}

	public void testIsEmptyPositiveCaseForIdenticalText() throws Exception {
		String str = ObjectMother.createString();
		assertTrue(new TestImpl("attr", str, false, str, null).isEmpty());
	}

	public void testIsEmptyNegativeCaseForNonEmptyText() throws Exception {
		assertFalse(testImpl.isEmpty());
	}

	public void testIsEmptyNegativeCaseForValueSlot() throws Exception {
		assertFalse(new TestImpl("attr", "var", true, null, null).isEmpty());
	}

	public void testIsMoreRestrictiveWithNullPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "isMoreRestrictive", new Class[]
			{ AttributePattern.class});
	}

	public void testIsMoreRestrictivePositiveCase() throws Exception {
		assertTrue(testImpl.isMoreRestrictive(new TestImpl("attr", "var", false, null, null)));
	}

	public void testIsMoreRestrictiveNegativeCase() throws Exception {
		assertFalse(testImpl.isMoreRestrictive(testImpl));
		assertFalse(new TestImpl("attr1", "var1", false, null, null).isMoreRestrictive(new TestImpl("attr2", "var2", false, null, null)));
	}

	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(testImpl.equals(null));
	}

	public void testEqualsWithIncompatibleTypeReturnsFalse() throws Exception {
		assertFalse(testImpl.equals("value"));
	}

	public void testEqualsNegativeCase() throws Exception {
		assertFalse(testImpl.equals(new TestImpl(testImpl.getAttributeName(), testImpl.getVariableName() + "X", false, null, null)));
	}

	public void testEqualsOnlyUsesVariableName() throws Exception {
		assertTrue(testImpl.equals(new TestImpl(testImpl.getAttributeName() + "X", testImpl.getVariableName(), true, null, null)));
	}

	protected void setUp() throws Exception {
		super.setUp();
		int id = ObjectMother.createInt();
		testImpl = new TestImpl("pe:" + id, "?var" + id, false, "text" + id, null);
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractAttributePatternTest
		super.tearDown();
	}
}

package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractAttributePatternTest extends AbstractTestBase {

	private static class TestImpl extends AbstractAttributePattern {

		protected TestImpl(String attributeName, String varName, boolean hasValueSlot, String valueText, ValueSlot valueSlot) {
			super(attributeName, varName, hasValueSlot, valueText, valueSlot);
		}

	}

	private TestImpl testImpl;

	@Test
	public void testConsturctorWithNullVariableNameThrowsNullPointerException() throws Exception {
		try {
			new TestImpl("attr", null, true, "", null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testHasSameValueWithNullPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasSameValue", new Class[] { AttributePattern.class });
	}

	@Test
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

	@Test
	public void testIsEmptyPositiveCaseForEmptyOrNullText() throws Exception {
		assertTrue(new TestImpl("attr", "var", false, "", null).isEmpty());
		assertTrue(new TestImpl("attr", "var", false, null, null).isEmpty());
	}

	@Test
	public void testIsEmptyPositiveCaseForIdenticalText() throws Exception {
		String str = createString();
		assertTrue(new TestImpl("attr", str, false, str, null).isEmpty());
	}

	@Test
	public void testIsEmptyNegativeCaseForNonEmptyText() throws Exception {
		assertFalse(testImpl.isEmpty());
	}

	@Test
	public void testIsEmptyNegativeCaseForValueSlot() throws Exception {
		assertFalse(new TestImpl("attr", "var", true, null, null).isEmpty());
	}

	@Test
	public void testIsMoreRestrictiveWithNullPatternThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "isMoreRestrictive", new Class[] { AttributePattern.class });
	}

	@Test
	public void testIsMoreRestrictivePositiveCase() throws Exception {
		assertTrue(testImpl.isMoreRestrictive(new TestImpl("attr", "var", false, null, null)));
	}

	@Test
	public void testIsMoreRestrictiveNegativeCase() throws Exception {
		assertFalse(testImpl.isMoreRestrictive(testImpl));
		assertFalse(new TestImpl("attr1", "var1", false, null, null).isMoreRestrictive(new TestImpl("attr2", "var2", false, null, null)));
	}

	@Test
	public void testEqualsWithNullReturnsFalse() throws Exception {
		assertFalse(testImpl.equals(null));
	}

	@Test
	public void testEqualsWithIncompatibleTypeReturnsFalse() throws Exception {
		assertFalse(testImpl.equals("value"));
	}

	@Test
	public void testEqualsNegativeCase() throws Exception {
		assertFalse(testImpl.equals(new TestImpl(testImpl.getAttributeName(), testImpl.getVariableName() + "X", false, null, null)));
	}

	@Test
	public void testEqualsOnlyUsesVariableName() throws Exception {
		assertTrue(testImpl.equals(new TestImpl(testImpl.getAttributeName() + "X", testImpl.getVariableName(), true, null, null)));
	}

	@Before
	public void setUp() throws Exception {
		int id = createInt();
		testImpl = new TestImpl("pe:" + id, "?var" + id, false, "text" + id, null);
	}

}

package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractObjectPatternTest extends AbstractTestBase {

	private static class TestImpl extends AbstractObjectPattern {

		protected TestImpl(String className, String variableName) {
			super(className, variableName);
		}

	}

	private TestImpl testImpl;

	@Test
	public void testConstructorWithNullArgThrowsNullPointerException() throws Exception {
		try {
			new TestImpl(null, "var");
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
		try {
			new TestImpl("class", null);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testAddAllWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "addAll", new Class[] { ObjectPattern.class });
	}

	@Test
	public void testAddAllHappyCase() throws Exception {
		testImpl.add(RuleObjectMother.createAttributePattern());
		ObjectPattern objectPattern = new TestImpl(testImpl.getClassName(), testImpl.getVariableName() + "X");
		objectPattern.add(RuleObjectMother.createAttributePattern());
		objectPattern.add(RuleObjectMother.createAttributePattern());
		objectPattern.add(RuleObjectMother.createAttributePattern());
		testImpl.addAll(objectPattern);
		assertEquals(1 + objectPattern.size(), testImpl.size());
	}

	@Test
	public void testContainsAttributeNameWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "containsAttributeName", new Class[] { String.class });
	}

	@Test
	public void testContainsAttributeNamePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsAttributeName(attributePattern.getAttributeName()));
	}

	@Test
	public void testContainsAttributeNameNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsAttributeName(attributePattern.getAttributeName() + "X"));
	}

	@Test
	public void testContainsAttributeWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "containsAttribute", new Class[] { String.class });
	}

	@Test
	public void testContainsAttributePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsAttribute(attributePattern.getVariableName()));
	}

	@Test
	public void testContainsAttributeNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsAttribute(attributePattern.getVariableName() + "X"));
	}

	@Test
	public void testContainsNonEmptyAttributeWithNullReturnsFalse() throws Exception {
		assertFalse(testImpl.containsNonEmptyAttribute(null));
	}

	@Test
	public void testContainsNonEmptyAttributePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePatternWithStaticText();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName()));
	}

	@Test
	public void testContainsNonEmptyAttributeNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName()));
		assertFalse(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName() + "X"));
	}

	@Before
	public void setUp() throws Exception {
		int id = createInt();
		testImpl = new TestImpl("pe:class" + id, "?obj" + id);
	}

}

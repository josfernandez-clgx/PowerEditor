package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class AbstractObjectPatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractObjectPatternTest Tests");
		suite.addTestSuite(AbstractObjectPatternTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractObjectPattern {

		protected TestImpl(String className, String variableName) {
			super(className, variableName);
		}

	}

	private TestImpl testImpl;

	public AbstractObjectPatternTest(String name) {
		super(name);
	}

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
	
	public void testAddAllWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "addAll", new Class[]
			{ ObjectPattern.class});
	}

	public void testAddAllHappyCase() throws Exception {
		testImpl.add(RuleObjectMother.createAttributePattern());
		ObjectPattern objectPattern = new TestImpl(testImpl.getClassName(), testImpl.getVariableName()+"X");
		objectPattern.add(RuleObjectMother.createAttributePattern());
		objectPattern.add(RuleObjectMother.createAttributePattern());
		objectPattern.add(RuleObjectMother.createAttributePattern());
		testImpl.addAll(objectPattern);
		assertEquals(1 + objectPattern.size(), testImpl.size());
	}

	public void testContainsAttributeNameWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "containsAttributeName", new Class[]{String.class});
	}

	public void testContainsAttributeNamePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsAttributeName(attributePattern.getAttributeName()));
	}

	public void testContainsAttributeNameNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsAttributeName(attributePattern.getAttributeName() + "X"));
	}

	public void testContainsAttributeWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "containsAttribute", new Class[]{String.class});
	}

	public void testContainsAttributePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsAttribute(attributePattern.getVariableName()));
	}

	public void testContainsAttributeNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsAttribute(attributePattern.getVariableName() + "X"));
	}

	public void testContainsNonEmptyAttributeWithNullReturnsFalse() throws Exception {
		assertFalse(testImpl.containsNonEmptyAttribute(null));
	}

	public void testContainsNonEmptyAttributePositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePatternWithStaticText();
		testImpl.add(attributePattern);
		assertTrue(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName()));
	}

	public void testContainsNonEmptyAttributeNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName()));
		assertFalse(testImpl.containsNonEmptyAttribute(attributePattern.getVariableName() + "X"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		int id = ObjectMother.createInt();
		testImpl = new TestImpl("pe:class" + id, "?obj" + id);
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractObjectPatternTest
		super.tearDown();
	}
}

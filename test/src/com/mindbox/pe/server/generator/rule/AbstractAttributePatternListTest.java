package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class AbstractAttributePatternListTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractAttributePatternListTest Tests");
		suite.addTestSuite(AbstractAttributePatternListTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractAttributePatternList {

	}

	private TestImpl testImpl;

	public AbstractAttributePatternListTest(String name) {
		super(name);
	}

	public void testContainsPositiveCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertTrue(testImpl.contains(new StaticTextAttributePattern(attributePattern.getAttributeName() + "X", attributePattern.getVariableName())));
	}

	public void testContainsNegativeCase() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertFalse(testImpl.contains(new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName() + "X")));
	}

	public void testFindWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "find", new Class[]
			{ AttributePattern.class});
	}

	public void testFindWithExistingPatternReturnsIt() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		testImpl.add(attributePattern);
		assertEquals(attributePattern, testImpl.find(new StaticTextAttributePattern(attributePattern.getAttributeName() + "X", attributePattern.getVariableName())));
	}

	public void testFindWithNonExistingPatternReturnsNull() throws Exception {
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern();
		assertNull(testImpl.find(attributePattern));
		testImpl.add(attributePattern);
		assertNull(testImpl.find(new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName() + "X")));
	}

	public void testReplaceWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(testImpl, "replace", new Class[]
			{ AttributePattern.class, AttributePattern.class}, new Object[]
			{ null, RuleObjectMother.createAttributePattern()});
		assertThrowsNullPointerException(testImpl, "replace", new Class[]
			{ AttributePattern.class, AttributePattern.class}, new Object[]
			{ RuleObjectMother.createAttributePattern(), null});
	}

	public void testReplaceHappyCase() throws Exception {
		AttributePattern oldPattern = RuleObjectMother.createAttributePattern();
		testImpl.add(oldPattern);
		AttributePattern newPattern = new StaticTextAttributePattern(oldPattern.getAttributeName() + "X", oldPattern.getVariableName(), "new-text");
		testImpl.replace(oldPattern, newPattern);

		AttributePattern pattern = testImpl.get(0);
		assertEquals(oldPattern.getAttributeName() + "X", pattern.getAttributeName());
		assertEquals("new-text", pattern.getValueText());
	}

	public void testReplaceWithNonExistingPatternIsNoOp() throws Exception {
		AttributePattern oldPattern = RuleObjectMother.createAttributePattern();
		testImpl.add(oldPattern);
		AttributePattern newPattern = new StaticTextAttributePattern(oldPattern.getAttributeName() + "X", oldPattern.getVariableName(), "new-text");
		testImpl.replace(new StaticTextAttributePattern(oldPattern.getAttributeName(), oldPattern.getVariableName() + "X"), newPattern);

		AttributePattern pattern = testImpl.get(0);
		assertEquals(oldPattern.getAttributeName(), pattern.getAttributeName());
		assertEquals(oldPattern.getVariableName(), pattern.getVariableName());
		assertEquals(oldPattern.getValueText(), pattern.getValueText());
	}

	public void testPreservesInsertOrder() throws Exception {
		AttributePattern[] attributePatterns = new AttributePattern[4];
		for (int i = 0; i < attributePatterns.length; i++) {
			attributePatterns[i] = RuleObjectMother.createAttributePattern();
		}

		// add attribute patterns
		for (int i = 0; i < attributePatterns.length; i++) {
			testImpl.add(attributePatterns[i]);
		}

		assertEquals(attributePatterns.length, testImpl.size());
		for (int i = 0; i < attributePatterns.length; i++) {
			assertEquals(attributePatterns[i], testImpl.get(i));
		}

		testImpl.remove(attributePatterns[2]);
		testImpl.remove(attributePatterns[0]);

		assertEquals(attributePatterns.length - 2, testImpl.size());
		assertEquals(attributePatterns[1], testImpl.get(0));
		assertEquals(attributePatterns[3], testImpl.get(1));
	}

	protected void setUp() throws Exception {
		super.setUp();
		testImpl = new TestImpl();
	}

	protected void tearDown() throws Exception {
		// Tear downs for AbstractAttributePatternListTest
		super.tearDown();
	}
}

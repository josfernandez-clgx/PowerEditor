package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGenerationException;

public class OptimizingObjectPatternTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("OptimizingObjectPatternTest Tests");
		suite.addTestSuite(OptimizingObjectPatternTest.class);
		return suite;
	}

	private OptimizingObjectPattern objectPattern;

	public OptimizingObjectPatternTest(String name) {
		super(name);
	}

	public void testCreateNormalInstanceSetsInvariantsCorrectly() throws Exception {
		String className = ObjectMother.createString();
		String varName = ObjectMother.createString();
		OptimizingObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(className, varName);
		assertFalse(objectPattern.shouldBeFirst());
		assertEquals(className, objectPattern.getClassName());
		assertEquals(varName, objectPattern.getVariableName());
	}

	public void testCreateShouldBeFirstInstanceSetsInvariantsCorrectly() throws Exception {
		String className = ObjectMother.createString();
		String varName = ObjectMother.createString();
		OptimizingObjectPattern objectPattern = OptimizingObjectPattern.createShouldBeFirstInstance(className, varName);
		assertTrue(objectPattern.shouldBeFirst());
		assertEquals(className, objectPattern.getClassName());
		assertEquals(varName, objectPattern.getVariableName());
	}

	public void testHasConflictingAttributePatternWithNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				OptimizingObjectPattern.class,
				"hasConflictingAttributePattern",
				new Class[] { ObjectPattern.class, AttributePattern.class },
				new Object[] { null, RuleObjectMother.createAttributePattern() });
		assertThrowsNullPointerException(
				OptimizingObjectPattern.class,
				"hasConflictingAttributePattern",
				new Class[] { ObjectPattern.class, AttributePattern.class },
				new Object[] { RuleObjectMother.createObjectPattern(ObjectMother.createString()), null });
	}

	public void testHasConflictingAttributePatternPositiveCaseForNonEmptyAttribute() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(
				objectPattern,
				RuleObjectMother.createAttributePatternWithStaticText(objectPattern.get(0).getAttributeName())));
	}

	public void testHasConflictingAttributePatternPositiveCaseForSameAttributeName() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		AttributePattern attributePattern = new StaticTextAttributePattern(
				objectPattern.get(0).getAttributeName(),
				objectPattern.get(0).getVariableName() + "X");

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternPositiveCaseForEmptyPatternWithDiffVarName() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.createObjectPattern(ref.getClassName());
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ref.getAttributeName(),
				"link1", null);
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				objectPattern.get(0).getAttributeName(),
				"link2", null);
		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternNegativeCaseForEmptyPatternWithSameVarName() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.createObjectPattern(ref.getClassName());
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ref.getAttributeName(),
				"link1", ObjectMother.createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				objectPattern.get(0).getAttributeName(),
				objectPattern.get(0).getVariableName(), null);
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternNegativeCaseForSameAttributeNameWithNegation() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		AttributePattern attributePattern = new StaticTextAttributePattern(
				objectPattern.get(0).getAttributeName(),
				objectPattern.get(0).getVariableName() + " & ~?X");
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternPositiveCaseForSameAttributeDffVar() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatterns(RuleObjectMother.createObjectPattern(ref.getClassName()), 1);
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern(
				objectPattern.get(0).getAttributeName(),
				ObjectMother.createString());

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternNegativeCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		// different attribute name
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(
				objectPattern,
				RuleObjectMother.createAttributePatternWithStaticText()));
	}

	public void testHasConflictingAttributePatternNegativeCaseForSameAttributeSameVar() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatterns(RuleObjectMother.createObjectPattern(ref.getClassName()), 1);
		objectPattern.add(RuleObjectMother.createAttributePattern(
				objectPattern.get(0).getAttributeName() + "X",
				objectPattern.get(0).getVariableName()));

		AttributePattern attributePattern = RuleObjectMother.createAttributePattern(
				objectPattern.get(0).getAttributeName(),
				objectPattern.get(0).getVariableName());
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	public void testHasConflictingAttributePatternDoesNotCheckEmptyAttributePatterns() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(
				objectPattern,
				RuleObjectMother.createAttributePattern(objectPattern.get(0).getAttributeName())));
	}

	public void testAddWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPattern, "add", new Class[] { AttributePattern.class });
	}

	public void testAddWithSameAttributeNameButDiffVarThrowsRuleGenerationException() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ObjectMother.createString(),
				ObjectMother.createString(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				attributePattern.getAttributeName(),
				attributePattern.getVariableName() + "X",
				ObjectMother.createString());
		assertThrowsException(
				objectPattern,
				"add",
				new Class[] { AttributePattern.class },
				new Object[] { attributePattern },
				RuleGenerationException.class);
	}

	public void testAddWithNonMatchingPatternAddsIt() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ObjectMother.createString(),
				ObjectMother.createString(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				attributePattern.getAttributeName() + "X",
				ObjectMother.createString(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		assertEquals(2, objectPattern.size());
	}

	public void testAddWithMoreRestrictiveAttributeReplacesOldOne() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(ObjectMother.createString(), ObjectMother.createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				attributePattern.getAttributeName(),
				attributePattern.getVariableName(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		assertEquals(1, objectPattern.size());
		assertTrue(objectPattern.containsAttribute(attributePattern.getVariableName()));
		assertEquals(attributePattern.getValueText(), objectPattern.get(0).getValueText());
	}

	public void testAddWithLessRestrictiveAttributeIsNoOp() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ObjectMother.createString(),
				ObjectMother.createString(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		objectPattern.add(new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName()));

		assertEquals(1, objectPattern.size());
		assertTrue(objectPattern.containsAttribute(attributePattern.getVariableName()));
		assertEquals(attributePattern.getValueText(), objectPattern.get(0).getValueText());
	}

	public void testAddWithNonEmptyAttributeThrowsRuleGenrationException() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(
				ObjectMother.createString(),
				ObjectMother.createString(),
				ObjectMother.createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				attributePattern.getAttributeName(),
				attributePattern.getVariableName(),
				ObjectMother.createString());
		assertThrowsException(
				objectPattern,
				"add",
				new Class[] { AttributePattern.class },
				new Object[] { attributePattern },
				RuleGenerationException.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		objectPattern = OptimizingObjectPattern.createNormalInstance(ObjectMother.createString(), ObjectMother.createString());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.unittest.AbstractTestBase;

public class OptimizingObjectPatternTest extends AbstractTestBase {

	private OptimizingObjectPattern objectPattern;

	@Test
	public void testCreateNormalInstanceSetsInvariantsCorrectly() throws Exception {
		String className = createString();
		String varName = createString();
		OptimizingObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(className, varName);
		assertFalse(objectPattern.shouldBeFirst());
		assertEquals(className, objectPattern.getClassName());
		assertEquals(varName, objectPattern.getVariableName());
	}

	@Test
	public void testCreateShouldBeFirstInstanceSetsInvariantsCorrectly() throws Exception {
		String className = createString();
		String varName = createString();
		OptimizingObjectPattern objectPattern = OptimizingObjectPattern.createShouldBeFirstInstance(className, varName);
		assertTrue(objectPattern.shouldBeFirst());
		assertEquals(className, objectPattern.getClassName());
		assertEquals(varName, objectPattern.getVariableName());
	}

	@Test
	public void testHasConflictingAttributePatternWithNullArgThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(OptimizingObjectPattern.class, "hasConflictingAttributePattern", new Class[] { ObjectPattern.class,
				AttributePattern.class }, new Object[] { null, RuleObjectMother.createAttributePattern() });
		assertThrowsNullPointerException(OptimizingObjectPattern.class, "hasConflictingAttributePattern", new Class[] { ObjectPattern.class,
				AttributePattern.class }, new Object[] { RuleObjectMother.createObjectPattern(createString()), null });
	}

	@Test
	public void testHasConflictingAttributePatternPositiveCaseForNonEmptyAttribute() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(
				objectPattern,
				RuleObjectMother.createAttributePatternWithStaticText(objectPattern.get(0).getAttributeName())));
	}

	@Test
	public void testHasConflictingAttributePatternPositiveCaseForSameAttributeName() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		AttributePattern attributePattern = new StaticTextAttributePattern(objectPattern.get(0).getAttributeName(), objectPattern.get(0)
				.getVariableName() + "X");

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternPositiveCaseForEmptyPatternWithDiffVarName() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.createObjectPattern(ref.getClassName());
		AttributePattern attributePattern = new StaticTextAttributePattern(ref.getAttributeName(), "link1", null);
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(objectPattern.get(0).getAttributeName(), "link2", null);
		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternNegativeCaseForEmptyPatternWithSameVarName() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.createObjectPattern(ref.getClassName());
		AttributePattern attributePattern = new StaticTextAttributePattern(ref.getAttributeName(), "link1", createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(objectPattern.get(0).getAttributeName(), objectPattern.get(0).getVariableName(), null);
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternNegativeCaseForSameAttributeNameWithNegation() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		AttributePattern attributePattern = new StaticTextAttributePattern(objectPattern.get(0).getAttributeName(), objectPattern.get(0)
				.getVariableName() + " & ~?X");
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternPositiveCaseForSameAttributeDffVar() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatterns(RuleObjectMother.createObjectPattern(ref.getClassName()), 1);
		AttributePattern attributePattern = RuleObjectMother.createAttributePattern(objectPattern.get(0).getAttributeName(), createString());

		assertTrue(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternNegativeCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		// different attribute name
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, RuleObjectMother.createAttributePatternWithStaticText()));
	}

	@Test
	public void testHasConflictingAttributePatternNegativeCaseForSameAttributeSameVar() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatterns(RuleObjectMother.createObjectPattern(ref.getClassName()), 1);
		objectPattern.add(RuleObjectMother.createAttributePattern(objectPattern.get(0).getAttributeName() + "X", objectPattern.get(0)
				.getVariableName()));

		AttributePattern attributePattern = RuleObjectMother.createAttributePattern(objectPattern.get(0).getAttributeName(), objectPattern.get(0)
				.getVariableName());
		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(objectPattern, attributePattern));
	}

	@Test
	public void testHasConflictingAttributePatternDoesNotCheckEmptyAttributePatterns() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));

		assertFalse(OptimizingObjectPattern.hasConflictingAttributePattern(
				objectPattern,
				RuleObjectMother.createAttributePattern(objectPattern.get(0).getAttributeName())));
	}

	@Test
	public void testAddWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(objectPattern, "add", new Class[] { AttributePattern.class });
	}

	@Test
	public void testAddWithSameAttributeNameButDiffVarThrowsRuleGenerationException() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(createString(), createString(), createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(
				attributePattern.getAttributeName(),
				attributePattern.getVariableName() + "X",
				createString());
		assertThrowsException(
				objectPattern,
				"add",
				new Class[] { AttributePattern.class },
				new Object[] { attributePattern },
				RuleGenerationException.class);
	}

	@Test
	public void testAddWithNonMatchingPatternAddsIt() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(createString(), createString(), createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(attributePattern.getAttributeName() + "X", createString(), createString());
		objectPattern.add(attributePattern);

		assertEquals(2, objectPattern.size());
	}

	@Test
	public void testAddWithMoreRestrictiveAttributeReplacesOldOne() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(createString(), createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName(), createString());
		objectPattern.add(attributePattern);

		assertEquals(1, objectPattern.size());
		assertTrue(objectPattern.containsAttribute(attributePattern.getVariableName()));
		assertEquals(attributePattern.getValueText(), objectPattern.get(0).getValueText());
	}

	@Test
	public void testAddWithLessRestrictiveAttributeIsNoOp() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(createString(), createString(), createString());
		objectPattern.add(attributePattern);

		objectPattern.add(new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName()));

		assertEquals(1, objectPattern.size());
		assertTrue(objectPattern.containsAttribute(attributePattern.getVariableName()));
		assertEquals(attributePattern.getValueText(), objectPattern.get(0).getValueText());
	}

	@Test
	public void testAddWithNonEmptyAttributeThrowsRuleGenrationException() throws Exception {
		AttributePattern attributePattern = new StaticTextAttributePattern(createString(), createString(), createString());
		objectPattern.add(attributePattern);

		attributePattern = new StaticTextAttributePattern(attributePattern.getAttributeName(), attributePattern.getVariableName(), createString());
		assertThrowsException(
				objectPattern,
				"add",
				new Class[] { AttributePattern.class },
				new Object[] { attributePattern },
				RuleGenerationException.class);
	}

	@Before
	public void setUp() throws Exception {
		objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
	}
}

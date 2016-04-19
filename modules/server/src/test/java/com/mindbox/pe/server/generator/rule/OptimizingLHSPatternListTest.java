package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.unittest.AbstractTestBase;

public class OptimizingLHSPatternListTest extends AbstractTestBase {

	private OptimizingLHSPatternList optimizingLHSPatternList;

	@Test
	public void testHasConflictingAttributePatternPositiveCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));
		optimizingLHSPatternList.append(objectPattern);

		ObjectPattern objectPattern2 = RuleObjectMother.appendAttributePatternWithStaticText(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				objectPattern.get(0).getAttributeName());
		assertTrue(optimizingLHSPatternList.hasConflictingAttributePattern(objectPattern2));
	}

	@Test
	public void testHasConflictingAttributePatternNegativeCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));
		optimizingLHSPatternList.append(objectPattern);

		// different attribute name
		ObjectPattern objectPattern2 = RuleObjectMother
				.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));
		assertFalse(optimizingLHSPatternList.hasConflictingAttributePattern(objectPattern2));
	}

	@Test
	public void testHasConflictingAttributePatternDoNotCheckObjPatternWithDiffVar() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(OptimizingObjectPattern.createNormalInstance(
				ref.getClassName(),
				"?OBJ" + createInt()));
		optimizingLHSPatternList.append(objectPattern);

		// different attribute name
		ObjectPattern objectPattern2 = RuleObjectMother.appendAttributePatternWithStaticText(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				objectPattern.get(0).getAttributeName());
		assertFalse(optimizingLHSPatternList.hasConflictingAttributePattern(objectPattern2));
	}

	@Test
	public void testHasConflictingAttributePatternDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePatternWithStaticText(RuleObjectMother.createObjectPattern(ref.getClassName()));
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		optimizingLHSPatternList.append(patternList);

		ObjectPattern objectPattern2 = RuleObjectMother.appendAttributePatternWithStaticText(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				objectPattern.get(0).getAttributeName());
		assertFalse(optimizingLHSPatternList.hasConflictingAttributePattern(objectPattern2));
	}

	@Test
	public void testAppendWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(optimizingLHSPatternList, "append", new Class[] { ObjectPattern.class });
	}

	@Test
	public void testAppendWithExistingObjectVarAndAttributeJustAddsAttributePatterns() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern);

		objectPattern = OptimizingObjectPattern.createNormalInstance(objectPattern.getClassName(), objectPattern.getVariableName());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.append(objectPattern);

		assertEquals(1, optimizingLHSPatternList.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(3, objectPattern.size());
	}

	@Test
	public void testAppendWithNoExistingObjectVarAddsObjectPattern() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern);

		ObjectPattern objectPattern2 = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.append(objectPattern2);

		assertEquals(2, optimizingLHSPatternList.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern2.getVariableName());
		assertEquals(2, objectPattern.size());
	}

	@Test
	public void testInsertWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(optimizingLHSPatternList, "insert", new Class[] { ObjectPattern.class });
	}

	@Test
	public void testInsertWithExistingObjectVarJustAddsAttributePatterns() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.insert(objectPattern);

		objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), objectPattern.getVariableName());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern);

		assertEquals(1, optimizingLHSPatternList.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(3, objectPattern.size());
	}

	@Test
	public void testInsertWithExistingObjectVarWithExclusionJustAddsAttributePatterns() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.insert(objectPattern);

		objectPattern = OptimizingObjectPattern.createNormalInstance(objectPattern.getClassName(), objectPattern.getVariableName() + " & ~?var2");
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern);

		assertEquals(1, optimizingLHSPatternList.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(3, objectPattern.size());
	}

	@Test
	public void testInsertWithExistingObjectVarWithNoPreserveFlagChangesOrder() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern);

		ObjectPattern objectPattern2 = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern2);

		objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), objectPattern2.getVariableName());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern);

		assertEquals(2, optimizingLHSPatternList.size());
		assertEquals(objectPattern2.getClassName(), ((ObjectPattern) optimizingLHSPatternList.get(0)).getClassName());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(3, objectPattern.size());
	}

	@Test
	public void testInsertWithExistingObjectVarWithTruePreserveFlagDoNotChangeOrder() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern);

		ObjectPattern objectPattern2 = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern2);

		objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), objectPattern2.getVariableName());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern, true);

		assertEquals(2, optimizingLHSPatternList.size());
		assertEquals(objectPattern2.getClassName(), ((ObjectPattern) optimizingLHSPatternList.get(1)).getClassName());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(3, objectPattern.size());
	}

	@Test
	public void testInsertWithNoExistingObjectVarAddsObjectPattern() throws Exception {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.insert(objectPattern);

		ObjectPattern objectPattern2 = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern2);

		assertEquals(2, optimizingLHSPatternList.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern.getVariableName());
		assertEquals(1, objectPattern.size());
		objectPattern = optimizingLHSPatternList.find(objectPattern2.getVariableName());
		assertEquals(2, objectPattern.size());
	}

	/**
	 * Test for TT 1957
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInsertWithShoudBeFirstPatternAfterNotOrORInsertsAndNotMerge() throws Exception {
		OptimizingLHSPatternList orPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_OR);
		optimizingLHSPatternList.append(orPatternList);

		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(createString(), createString());
		objectPattern.add(new StaticTextAttributePattern(createString(), createString()));
		optimizingLHSPatternList.append(objectPattern);

		ObjectPattern objectPattern2 = OptimizingObjectPattern.createShouldBeFirstInstance(
				objectPattern.getClassName(),
				objectPattern.getVariableName());
		objectPattern2.add(new StaticTextAttributePattern(createString(), createString()));

		optimizingLHSPatternList.insert(objectPattern2, true);

		assertEquals(3, optimizingLHSPatternList.size());
		// first pattern should be the last pattern added (objectPattern2)
		assertTrue(optimizingLHSPatternList.get(0) instanceof ObjectPattern);
		assertEquals(objectPattern2.getClassName(), ((ObjectPattern) optimizingLHSPatternList.get(0)).getClassName());
		assertEquals(objectPattern2.get(0).getAttributeName(), ((ObjectPattern) optimizingLHSPatternList.get(0)).get(0).getAttributeName());

		// second pattern should be the OR pattern list
		assertEquals(OptimizingLHSPatternList.class, optimizingLHSPatternList.get(1).getClass());
		assertEquals(LHSPatternList.TYPE_OR, ((OptimizingLHSPatternList) optimizingLHSPatternList.get(1)).getType());

		// third pattern should be objectPattern
		assertTrue(optimizingLHSPatternList.get(2) instanceof ObjectPattern);
		assertEquals(objectPattern.getClassName(), ((ObjectPattern) optimizingLHSPatternList.get(2)).getClassName());
		assertEquals(objectPattern.get(0).getAttributeName(), ((ObjectPattern) optimizingLHSPatternList.get(2)).get(0).getAttributeName());
	}

	@Before
	public void setUp() throws Exception {
		optimizingLHSPatternList = new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
	}
}

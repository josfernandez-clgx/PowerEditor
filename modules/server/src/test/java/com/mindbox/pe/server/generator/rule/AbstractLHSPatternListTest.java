package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractLHSPatternListTest extends AbstractTestBase {

	private static class TestImpl extends AbstractLHSPatternList {

		protected TestImpl(int type) {
			super(type);
		}
	}

	private AbstractLHSPatternList testImpl;

	@Test
	public void testIndexOfPatternWithVariableWithNullThrowsNullPointerException() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "indexOfPatternWithVariable", new Class[] { String.class });
	}

	@Test
	public void testIndexOfPatternWithVariableHappyCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertEquals(0, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName()));
	}

	@Test
	public void testIndexOfPatternWithVariableWithExclusionHappyCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertEquals(0, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName() + " & ~var2"));
	}

	@Test
	public void testIndexOfPatternWithVariableWithExclusionHappyCaseReverse() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);
		assertEquals(0, testImpl.indexOfPatternWithVariable("?var1"));
	}

	@Test
	public void testIndexOfPatternWithVariableWithNonExistentVariableReturnsNegativeOne() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertEquals(-1, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName() + "X"));
	}

	@Test
	public void testIndexOfPatternWithVariableDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);
		assertEquals(-1, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName()));
	}

	@Test
	public void testFindWithNullThrowsNullPointerException() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "find", new Class[] { String.class });
	}

	@Test
	public void testFindHappyCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName());
		assertEquals(objectPattern, objectPattern2);
	}

	@Test
	public void testFindHappyCaseForVarWithExclusion() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName() + " & ~var2");
		assertEquals(objectPattern, objectPattern2);
	}

	@Test
	public void testFindHappyCaseForVarWithExclusionReverse() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName() + " & ~var2");
		assertEquals(objectPattern, objectPattern2);
	}

	@Test
	public void testFindWithNonExistentVariableReturnsNull() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertNull(testImpl.find(objectPattern.getVariableName() + "X"));
	}

	@Test
	public void testFindDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);
		assertNull(testImpl.find(objectPattern.getVariableName()));
	}

	@Test
	public void testHasPatternForReferenceWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasPatternForReference", new Class[] { Reference.class });
	}

	@Test
	public void testHasPatternForReferencePositiveCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertTrue(testImpl.hasPatternForReference(createReference(ref.getClassName(), ref.getAttributeName())));

		// check when object pattern is within an LHS pattern list
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
		testImpl.append(patternList);
		assertTrue(testImpl.hasPatternForReference(createReference(ref.getClassName(), ref.getAttributeName())));
	}

	@Test
	public void testHasPatternForReferencePositiveCaseWithExclusionVar() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				OptimizingObjectPattern.createNormalInstance(ref.getClassName(), AeMapper.makeAEVariable(ref.getClassName()) + " & ~?OBJ"),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertTrue(testImpl.hasPatternForReference(createReference(ref.getClassName(), ref.getAttributeName())));
	}

	@Test
	public void testHasPatternForReferenceNegativeCase() throws Exception {
		Reference ref = createReference();
		assertFalse(testImpl.hasPatternForReference(ref));
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertFalse(testImpl.hasPatternForReference(createReference(ref.getClassName(), ref.getAttributeName() + "X")));

		// check when object pattern is within an LHS pattern list
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
		testImpl.append(patternList);
		assertFalse(testImpl.hasPatternForReference(createReference(ref.getClassName(), ref.getAttributeName() + "X")));
	}

	@Test
	public void testHasPatternForVariableWithNullThrowsNullPointerException() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasPatternForVariableName", new Class[] { String.class });
	}

	@Test
	public void testHasPatternForVariablePositiveCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));
	}

	@Test
	public void testHasPatternForVariablePositiveCaseForVarWithExclusion() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName(objectPattern.getVariableName() + " & ~?var2"));
	}

	@Test
	public void testHasPatternForVariablePositiveCaseForVarWithExclusionReverse() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName("?var1"));
	}

	@Test
	public void testHasPatternForVariableNegativeCase() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));

		testImpl.append(objectPattern);
		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName() + "X"));
	}

	@Test
	public void testHasPatternForVariableDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);

		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));
	}

	@Before
	public void setUp() throws Exception {
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
	}
}

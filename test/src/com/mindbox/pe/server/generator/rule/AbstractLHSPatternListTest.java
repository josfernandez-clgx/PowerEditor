package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.AeMapper;

public class AbstractLHSPatternListTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractLHSPatternListTest Tests");
		suite.addTestSuite(AbstractLHSPatternListTest.class);
		return suite;
	}

	private static class TestImpl extends AbstractLHSPatternList {

		protected TestImpl(int type) {
			super(type);
		}
	}

	private AbstractLHSPatternList testImpl;

	public AbstractLHSPatternListTest(String name) {
		super(name);
	}

	public void testIndexOfPatternWithVariableWithNullThrowsNullPointerException() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "indexOfPatternWithVariable", new Class[] { String.class });
	}

	public void testIndexOfPatternWithVariableHappyCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertEquals(0, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName()));
	}

	public void testIndexOfPatternWithVariableWithExclusionHappyCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertEquals(0, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName() + " & ~var2"));
	}

	public void testIndexOfPatternWithVariableWithExclusionHappyCaseReverse() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);
		assertEquals(0, testImpl.indexOfPatternWithVariable("?var1"));
	}

	public void testIndexOfPatternWithVariableWithNonExistentVariableReturnsNegativeOne() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertEquals(-1, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName() + "X"));
	}

	public void testIndexOfPatternWithVariableDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);
		assertEquals(-1, testImpl.indexOfPatternWithVariable(objectPattern.getVariableName()));
	}

	public void testFindWithNullThrowsNullPointerException() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "find", new Class[] { String.class });
	}

	public void testFindHappyCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName());
		assertEquals(objectPattern, objectPattern2);
	}

	public void testFindHappyCaseForVarWithExclusion() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName() + " & ~var2");
		assertEquals(objectPattern, objectPattern2);
	}

	public void testFindHappyCaseForVarWithExclusionReverse() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);

		ObjectPattern objectPattern2 = testImpl.find(objectPattern.getVariableName() + " & ~var2");
		assertEquals(objectPattern, objectPattern2);
	}

	public void testFindWithNonExistentVariableReturnsNull() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertNull(testImpl.find(objectPattern.getVariableName() + "X"));
	}

	public void testFindDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);
		assertNull(testImpl.find(objectPattern.getVariableName()));
	}

	public void testHasPatternForReferenceWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasPatternForReference", new Class[] { Reference.class });
	}

	public void testHasPatternForReferencePositiveCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertTrue(testImpl.hasPatternForReference(ObjectMother.createReference(ref.getClassName(), ref.getAttributeName())));

		// check when object pattern is within an LHS pattern list
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
		testImpl.append(patternList);
		assertTrue(testImpl.hasPatternForReference(ObjectMother.createReference(ref.getClassName(), ref.getAttributeName())));
	}

	public void testHasPatternForReferencePositiveCaseWithExclusionVar() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				OptimizingObjectPattern.createNormalInstance(ref.getClassName(), AeMapper.makeAEVariable(ref.getClassName()) + " & ~?OBJ"),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertTrue(testImpl.hasPatternForReference(ObjectMother.createReference(ref.getClassName(), ref.getAttributeName())));
	}

	public void testHasPatternForReferenceNegativeCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		assertFalse(testImpl.hasPatternForReference(ref));
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);
		assertFalse(testImpl.hasPatternForReference(ObjectMother.createReference(ref.getClassName(), ref.getAttributeName() + "X")));

		// check when object pattern is within an LHS pattern list
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
		testImpl.append(patternList);
		assertFalse(testImpl.hasPatternForReference(ObjectMother.createReference(ref.getClassName(), ref.getAttributeName() + "X")));
	}

	public void testHasPatternForVariableWithNullThrowsNullPointerException() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertThrowsNullPointerExceptionWithNullArgs(testImpl, "hasPatternForVariableName", new Class[] { String.class });
	}

	public void testHasPatternForVariablePositiveCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));
	}

	public void testHasPatternForVariablePositiveCaseForVarWithExclusion() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName(objectPattern.getVariableName() + " & ~?var2"));
	}

	public void testHasPatternForVariablePositiveCaseForVarWithExclusionReverse() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(ref.getClassName(), "?var1 & ~?var2");
		testImpl.append(objectPattern);

		assertTrue(testImpl.hasPatternForVariableName("?var1"));
	}

	public void testHasPatternForVariableNegativeCase() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));

		testImpl.append(objectPattern);
		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName() + "X"));
	}

	public void testHasPatternForVariableDoesNotCheckEmbeddedPatternLists() throws Exception {
		Reference ref = ObjectMother.createReference();
		ObjectPattern objectPattern = RuleObjectMother.appendAttributePattern(
				RuleObjectMother.createObjectPattern(ref.getClassName()),
				ref.getAttributeName());
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		patternList.append(objectPattern);
		testImpl.append(patternList);

		assertFalse(testImpl.hasPatternForVariableName(objectPattern.getVariableName()));
	}

	protected void setUp() throws Exception {
		super.setUp();
		testImpl = new TestImpl(LHSPatternList.TYPE_AND);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

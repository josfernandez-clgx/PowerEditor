package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.rule.Reference;

public class GuidelineRuleTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineRuleTest Tests");
		suite.addTestSuite(GuidelineRuleTest.class);
		return suite;
	}

	private GuidelineRule guidelineRule;
	
	public GuidelineRuleTest(String name) {
		super(name);
	}

	public void testHasPatternForReferenceWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(guidelineRule, "hasPatternForReference", new Class[]{Reference.class});
	}

	public void testHasPatternForReferenceNegativeCaseOnEmptyPatternList() throws Exception {
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		guidelineRule.setLHSPatternList(patternList);
		assertFalse(patternList.hasPatternForReference(ObjectMother.createReference("c","a")));
	}

	protected void setUp() throws Exception {
		super.setUp();
		guidelineRule = new GuidelineRule();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

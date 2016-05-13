package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.unittest.AbstractTestBase;

public class GuidelineRuleTest extends AbstractTestBase {

	private GuidelineRule guidelineRule;

	@Test
	public void testHasPatternForReferenceWithNullReferenceThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(guidelineRule, "hasPatternForReference", new Class[] { Reference.class });
	}

	@Test
	public void testHasPatternForReferenceNegativeCaseOnEmptyPatternList() throws Exception {
		LHSPatternList patternList = RuleObjectMother.createLHSPatternList();
		guidelineRule.setLHSPatternList(patternList);
		assertFalse(patternList.hasPatternForReference(createReference("c", "a")));
	}

	@Before
	public void setUp() throws Exception {
		guidelineRule = new GuidelineRule();
	}
}

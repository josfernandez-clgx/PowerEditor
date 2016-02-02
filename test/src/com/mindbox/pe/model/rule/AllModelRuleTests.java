package com.mindbox.pe.model.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Dan Guaghan
 * @author MindBox
 * @since PowerEditor 5.0.0
 */
public class AllModelRuleTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Model Rule Tests");
		suite.addTest(AbstractCompoundRuleElementTest.suite());
		suite.addTest(AbstractConditionTest.suite());
		suite.addTest(ConditionTest.suite());
		suite.addTest(RuleElementFactoryTest.suite());
		return suite;
	}

}

package com.mindbox.pe.common.config;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.5.0
 */
public class AllCommonConfigTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Common Config Tests");
		suite.addTest(AbstractDigestedObjectHolderTest.suite());
		suite.addTest(ConfigUtilTest.suite());
		suite.addTest(EntityConfigurationTest.suite());
		suite.addTest(EntityPropertyTabDefinitionTest.suite());
		suite.addTest(EntityTypeDefinitionTest.suite());
		suite.addTest(MessageConfigurationTest.suite());
		suite.addTest(UIConfigurationTest.suite());
		return suite;
	}

}

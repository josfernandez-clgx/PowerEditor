package com.mindbox.pe.common.diff;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllCommonDiffClassTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Common Diff Tests");
		suite.addTest(SimpleGridDiffEngineTest.suite());
		return suite;
	}

}

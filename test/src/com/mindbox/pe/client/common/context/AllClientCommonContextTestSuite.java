/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.client.common.context;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllClientCommonContextTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Common Tree Tests");
		suite.addTest(GuidelineContextPanelTest.suite());
		return suite;
	}

}

/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.client.common.selection;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllClientCommonSelectionTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Common Selection Tests");
		suite.addTest(IDNameObjectSelectionPanelTest.suite());
		return suite;
	}

}

package com.mindbox.pe.client.common.tree;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllClientCommonTreeTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Client Common Tree Tests");
		suite.addTest(AbstractGenericCategorySelectionTreeTest.suite());
		suite.addTest(DatedCategoryTreeModelTest.suite());
		suite.addTest(GenericCategoryNodeTest.suite());
		suite.addTest(GenericCategorySelectionTreeTest.suite());
		suite.addTest(GenericCategoryOrEntitySelectionTreeTest.suite());
		return suite;
	}

}

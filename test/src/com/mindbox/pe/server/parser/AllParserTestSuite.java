package com.mindbox.pe.server.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 * @deprecated disabled until fixed
 */
public class AllParserTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Repository Tests");
//		suite.addTest(MessageParserTest.suite());
		return suite;
	}

}

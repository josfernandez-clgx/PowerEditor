package com.mindbox.pe.server.generator.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public final class AllServerGeneratorProcessorTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllServerGeneratorProcessorTestSuite.class.getName());
		suite.addTest(MessageProcessorTest.suite());
		return suite;
	}
}

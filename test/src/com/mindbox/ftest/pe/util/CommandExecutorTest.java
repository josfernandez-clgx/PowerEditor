package com.mindbox.ftest.pe.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CommandExecutorTest extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite("CommandExecutorTest Tests");
		suite.addTestSuite(CommandExecutorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public CommandExecutorTest(String name) {
		super(name);
	}
	
	public void testExecute() throws Exception {
		assertEquals(0, new TimeOutCommandExecutor(12*1000L).execute(new String[]{"net","stop","resin3017"}));
	}
}

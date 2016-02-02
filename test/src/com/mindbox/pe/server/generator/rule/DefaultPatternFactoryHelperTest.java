package com.mindbox.pe.server.generator.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;

public class DefaultPatternFactoryHelperTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DefaultPatternFactoryHelperTest Tests");
		suite.addTestSuite(DefaultPatternFactoryHelperTest.class);
		return suite;
	}

	private DefaultPatternFactoryHelper defaultPatternFactoryHelper;

	public DefaultPatternFactoryHelperTest(String name) {
		super(name);
	}

	public void testAsVariableNameWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(defaultPatternFactoryHelper, "asVariableName", new Class[]
			{ String.class, String.class});
	}

	public void testAsVariableNameHappyCaseWithNullOverride() throws Exception {
		String str = ObjectMother.createString();
		assertEquals("?" + str, defaultPatternFactoryHelper.asVariableName(str, null));
	}

	public void testAsVariableNameHappyCaseWithOverride() throws Exception {
		String str = ObjectMother.createString();
		String override = ObjectMother.createString();
		assertEquals(override, defaultPatternFactoryHelper.asVariableName(str, override));

	}

	// TODO Kim: add tests for other methods

	protected void setUp() throws Exception {
		super.setUp();
		defaultPatternFactoryHelper = new DefaultPatternFactoryHelper();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

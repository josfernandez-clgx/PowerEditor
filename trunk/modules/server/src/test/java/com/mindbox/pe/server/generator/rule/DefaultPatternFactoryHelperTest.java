package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerExceptionWithNullArgs;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class DefaultPatternFactoryHelperTest extends AbstractTestWithTestConfig {

	private DefaultPatternFactoryHelper defaultPatternFactoryHelper;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		defaultPatternFactoryHelper = new DefaultPatternFactoryHelper(null);
	}

	@Test
	public void testAsVariableNameHappyCaseWithNullOverride() throws Exception {
		String str = createString();
		assertEquals("?" + str, defaultPatternFactoryHelper.asVariableName(str, null));
	}

	@Test
	public void testAsVariableNameHappyCaseWithOverride() throws Exception {
		String str = createString();
		String override = createString();
		assertEquals(override, defaultPatternFactoryHelper.asVariableName(str, override));
	}

	@Test
	public void testAsVariableNameWithNullUsageTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(defaultPatternFactoryHelper, "asVariableName", new Class[] { String.class, String.class });
	}
}

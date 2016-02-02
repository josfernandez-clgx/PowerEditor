package com.mindbox.pe.server.config;

import junit.framework.TestSuite;

import org.apache.commons.beanutils.PropertyUtils;

import com.mindbox.pe.AbstractTestWithTestConfig;

public class RuleGenerationConfigurationTest extends AbstractTestWithTestConfig {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(RuleGenerationConfigurationTest.class.getName());
		suite.addTestSuite(RuleGenerationConfigurationTest.class);
		return suite;
	}

	public RuleGenerationConfigurationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	public void testMessageFormatConversionFunctionSetToDefaultIsMissing() throws Exception {
		config.resetConfiguration();
		config.initServer("test/config/PowerEditorConfiguration-NoProgram.xml");
		assertEquals("sprintf", ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageFormatConversionFunction());
	}

	public void testLhsDateFormatIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("lhsDateFormat");
	}

	public void testPEActionOnIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("PEActionOn");
	}

	public void testMessageFormatConversionFunctionIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageFormatConversionFunction");
	}

	public void testMessageDateFormatIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageDateFormat");
	}

	public void testMessageDateRangeFormatIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageDateRangeFormat");
	}

	public void testMessageDateFormatAeIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageDateFormatAe");
	}

	public void testGuidelineRuleSeedNameIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("guidelineRuleSeedName");
	}

	public void testObjectGenInstanceCreateTextIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("objectGenInstanceCreateText");
	}

	public void testLineagePatternConfigSetIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("lineagePatternConfigSet");
	}

	private void testDefaultConfigIsInitialized(String configPropertyName) throws Exception {
		testConfigIsInitialized(configPropertyName, ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault());
	}

	private void testConfigIsInitialized(String configPropertyName, RuleGenerationConfiguration ruleGenConfig) throws Exception {
		assertNotNull(PropertyUtils.getProperty(ruleGenConfig, configPropertyName));
	}
}

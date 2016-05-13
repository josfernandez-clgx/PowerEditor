package com.mindbox.pe.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class RuleGenerationConfigurationTest extends AbstractTestWithTestConfig {

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	private void testConfigIsInitialized(String configPropertyName, RuleGenerationConfigHelper ruleGenConfig) throws Exception {
		assertNotNull(PropertyUtils.getProperty(ruleGenConfig, configPropertyName));
	}

	private void testDefaultConfigIsInitialized(String configPropertyName) throws Exception {
		testConfigIsInitialized(configPropertyName, ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper());
	}

	@Test
	public void testGuidelineRuleSeedNameIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("guidelineRuleSeedName");
	}

	@Test
	public void testLhsDateFormatIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("lhsDateFormat");
	}

	@Test
	public void testLineagePatternConfigSetIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("lineagePatternConfigSet");
	}

	@Test
	public void testMessageDateFormatAeIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageDateFormatAe");
	}

	@Test
	public void testMessageDateRangeFormatIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageDateRangeFormat");
	}

	@Test
	public void testMessageFormatConversionFunctionIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("messageFormatConversionFunction");
	}

	@Test
	public void testMessageFormatConversionFunctionSetToDefaultIsMissing() throws Exception {
		config.resetConfiguration();
		config.initServer("src/test/config/PowerEditorConfiguration-NoProgram.xml");
		assertEquals("sprintf", ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getMessageFormatConversionFunction());
	}

	@Test
	public void testPEActionOnIsInitialized() throws Exception {
		testDefaultConfigIsInitialized("PEActionOn");
	}
}

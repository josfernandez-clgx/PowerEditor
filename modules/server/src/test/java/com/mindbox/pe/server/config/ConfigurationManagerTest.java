package com.mindbox.pe.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class ConfigurationManagerTest extends AbstractTestWithTestConfig {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	@Override
	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	@Test
	public void testConstructorInstantiatesRulesListCondMsgDelims() throws Exception {
		assertFalse(ConfigurationManager.getInstance().getCondMsgDelims().isEmpty());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetInstanceBeforeInitialize() throws Exception {
		config.resetConfiguration();
		ConfigurationManager.getInstance();
	}

	@Test
	public void testGetRuleGenerationConfigDefault() throws Exception {
		RuleGenerationConfigHelper defaultRuleGenConfig = ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper();
		assertNotNull(defaultRuleGenConfig);
		assertSame(ConfigurationManager.getInstance().getRuleGenerationConfigHelper(null), defaultRuleGenConfig);
	}

	@Test(expected = IllegalStateException.class)
	public void testInitializeNull() throws Exception {
		config.resetConfiguration();
		ConfigurationManager.initialize("1.0", "b1", null, "");
	}

	@Test(expected = NullPointerException.class)
	public void testSetServerBasePathNull() throws Exception {
		ConfigurationManager.getInstance().setServerBasePath(null);
	}

	@Test
	public void testSetServerBasePathSetOnlyOnce() throws Exception {
		assertNull(ConfigurationManager.getInstance().getServerBasePath()); // sanity check
		ConfigurationManager.getInstance().setServerBasePath("1");
		ConfigurationManager.getInstance().setServerBasePath("2");
		assertEquals("1", ConfigurationManager.getInstance().getServerBasePath());
	}

	@Test
	public void testUserManagementConfigSetsHideCopyToFalseByDefault() throws Exception {
		assertFalse(ConfigurationManager.getInstance().getUserManagementConfig().isHideCopyButton());
	}

	@Test
	public void testUserManagementConfigSetsHideCopyToTrueIfSet() throws Exception {
		config.resetConfiguration();
		config.initServer("src/test/config/PowerEditorConfiguration-Mod.xml");
		assertTrue(ConfigurationManager.getInstance().getUserManagementConfig().isHideCopyButton());
	}
}

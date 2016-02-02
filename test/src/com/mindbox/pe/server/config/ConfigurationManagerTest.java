package com.mindbox.pe.server.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.model.TemplateUsageType;

public class ConfigurationManagerTest extends AbstractTestWithTestConfig {
	public static Test suite() {
		TestSuite suite = new TestSuite("ConfigurationManagerTest");
		suite.addTestSuite(ConfigurationManagerTest.class);
		return suite;
	}
	
	public ConfigurationManagerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		assertEquals(config.getConfigFilename(), ConfigurationManager.getInstance().getFilename()); // sanity check
		
	}
	
	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
	
	public void testGetInstanceBeforeInitialize() throws Exception {
		config.resetConfiguration();
		try {
			ConfigurationManager.getInstance();
			fail("Expected " + IllegalStateException.class.getName());
		} catch (IllegalStateException e) {
			// pass
		}
	}
	
	public void testInitializeNull() throws Exception {
		config.resetConfiguration();
		try {
			ConfigurationManager.initialize(null);
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	public void testInitializeFileNotFound() throws Exception {
		config.resetConfiguration();
		try {
			ConfigurationManager.initialize("test/config/failMe.xml");
			fail("Expected " + ConfigurationException.class.getName());
		} catch (ConfigurationException e) {
			// pass
		}
	}
	
	
	//don't know how to test init with unreadable config file.
//	public void testInitializeUnreadableFile() throws Exception {
//		resetConfiguration();
//		try {
//			ConfigurationManager.initialize("anUnreadableFile.xml");
//			fail("Expected " + IllegalArgumentException.class.getName());
//		} catch (IllegalArgumentException e) {
//			// pass
//		}
//	}
	
	public void testSetServerBasePathNull() throws Exception {
		try {
			ConfigurationManager.getInstance().setServerBasePath(null);
			fail("Expected " + NullPointerException.class.getName());
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	public void testSetServerBasePathSetOnlyOnce() throws Exception {
		assertNull(ConfigurationManager.getInstance().getServerBasePath()); // sanity check
		ConfigurationManager.getInstance().setServerBasePath("1");
		ConfigurationManager.getInstance().setServerBasePath("2");
		assertEquals("1", ConfigurationManager.getInstance().getServerBasePath());
	}
	
	// Below are several constructor tests that test a complex instance variable is created.
	// Since setting the complex instance's properties is delegated to ConfigXMLDigester, we defer testing 
	// the details of these complex properties to ConfigXMLDigesterTest.
	// An alternative approach might be to "mock out" the ConfigXMLDigester, but that's probably overkill.
	public void testConstructorInstantiatesFeatureConfig() throws Exception {
		assertNotNull(ConfigurationManager.getInstance().getFeatureConfiguration());
	}

	public void testConstructorInstantiatesServerConfig() throws Exception {
		assertNotNull(ConfigurationManager.getInstance().getServerConfiguration());
	}

	public void testConstructorInstantiatesRuleGenerationConfig() throws Exception {
		assertTrue(TemplateUsageType.getAllInstances().length > 0);
		for (int i = 0; i < TemplateUsageType.getAllInstances().length; i++) {
			TemplateUsageType templateUsageType = TemplateUsageType.getAllInstances()[i];
			assertNotNull(ConfigurationManager.getInstance().getRuleGenerationConfiguration(templateUsageType));
		}
	}

	public void testGetRuleGenerationConfigDefault() throws Exception {
		RuleGenerationConfiguration defaultRuleGenConfig = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault();
		assertNotNull(defaultRuleGenConfig);
		assertSame(ConfigurationManager.getInstance().getRuleGenerationConfiguration(null), defaultRuleGenConfig);
	}

	public void testConstructorInstantiatesRulesListCondMsgDelims() throws Exception {
		assertFalse(ConfigurationManager.getInstance().getCondMsgDelims().isEmpty());
	}

	public void testConstructorInstantiatesUIConfig() throws Exception {
		assertNotNull(ConfigurationManager.getInstance().getUIConfiguration());
	}

	public void testConstructorInstantiatesEntityConfig() throws Exception {
		assertNotNull(ConfigurationManager.getInstance().getEntityConfiguration());
	}

	public void testConstructorInstantiatesUserPasswordPoliciesConfig() throws Exception {
		assertNotNull(ConfigurationManager.getInstance().getUserPasswordPoliciesConfig());
	}
	// end constructor tests deferring to ConfigXMLDigester
	
	public void testConstructorInitsDomainDefinitionFiles() throws Exception {
		assertEquals(new String[]{"test/config/MortgageDomain.xml"}, ConfigurationManager.getInstance().getDomainDefinitionFiles());
		ConfigurationManager.getInstance().addDomainDefinitionFile("domaindef.file");
		assertEquals(new String[]{"test/config/MortgageDomain.xml", "domaindef.file"}, ConfigurationManager.getInstance().getDomainDefinitionFiles());
	}

	public void testTemplateDefinitionFiles() throws Exception {
		assertEquals(0, ConfigurationManager.getInstance().getTemplateDefinitionFiles().length);
		ConfigurationManager.getInstance().addTemplateDefinitionFile("templdef.file");
		assertEquals(new String[]{"templdef.file"}, ConfigurationManager.getInstance().getTemplateDefinitionFiles());
	}

	public void testCBRDefinitionFiles() throws Exception {
		assertEquals(0, ConfigurationManager.getInstance().getCBRDefinitionFiles().length);
		ConfigurationManager.getInstance().addCBRDefinitionFile("cbrdef.file");
		assertEquals(new String[]{"cbrdef.file"}, ConfigurationManager.getInstance().getCBRDefinitionFiles());
	}
	
}

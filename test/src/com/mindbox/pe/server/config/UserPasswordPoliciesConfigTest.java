package com.mindbox.pe.server.config;

import java.util.Iterator;

import org.xml.sax.SAXParseException;


import junit.framework.TestSuite;

public class UserPasswordPoliciesConfigTest extends ConfigXmlTest {
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(UserPasswordPoliciesConfigTest.class.getName());
		suite.addTestSuite(UserPasswordPoliciesConfigTest.class);
		return suite;
	}
	
	public UserPasswordPoliciesConfigTest(String name) {
		super(name);
	}
	
	public void testUserPasswordPoliciesNotConfigured() throws Exception {
		removeAll("Server/UserPasswordPolicies");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		
		assertNotNull(config);
		assertEquals(UserPasswordPoliciesConfig.ValidatorConfig.DEFAULT_VALIDATOR_CLASS.getName(), 
				config.getValidatorConfig().getProviderClass().getName());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_EXPIRATION_DAYS, 
				config.getExpirationConfig().getExpirationDays());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_NOTIFICATION_DAYS, 
				config.getExpirationConfig().getNotificationDays());
		assertEquals(UserPasswordPoliciesConfig.LockoutConfig.DEFAULT_MAX_ATTEMPTS, 
				config.getLockoutConfig().getMaxAttempts());
	}
	
	// assumes test/config/PowerEditorConfiguration.xml has specific class configured
	public void testValidatorClassnameConfigured() throws Exception {
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(com.mindbox.pe.server.spi.pwd.RegexpPasswordValidator.class.getName(), 
				config.getValidatorConfig().getProviderClass().getName());    
	}
    
    public void testValidatorHistoryConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertEquals(config.getHistoryConfig().getLookback(), 3);
    }

    
	public void testValidatorClassnameNotInClasspath() throws Exception {
		replaceAttributeValue("Server/UserPasswordPolicies/Validator", "providerClassName", "some.nonexistent.classname");
		try {
			new UserPasswordPoliciesConfig(getPeConfigXml());
			fail("Expected " + ClassNotFoundException.class.getName());
		} catch (RuntimeException e) {
			// pass
			assertEquals(ClassNotFoundException.class.getName(), ((SAXParseException) e.getCause()).getException().getCause().getClass().getName());
		}
	}
	
	public void testValidatorNotConfigured() throws Exception {
		removeAll("Server/UserPasswordPolicies/Validator");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(UserPasswordPoliciesConfig.ValidatorConfig.DEFAULT_VALIDATOR_CLASS.getName(), 
				config.getValidatorConfig().getProviderClass().getName());
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	public void testExpirationConfigured() throws Exception {
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(90, config.getExpirationConfig().getExpirationDays());
		assertEquals(14, config.getExpirationConfig().getNotificationDays());
	}
	
	public void testExpirationConfiguredNegativeValue() throws Exception {
		replaceAttributeValue("Server/UserPasswordPolicies/Expiration", "expirationDays", "-1");
		replaceAttributeValue("Server/UserPasswordPolicies/Expiration", "notificationDays", "-1");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_EXPIRATION_DAYS, 
				config.getExpirationConfig().getExpirationDays());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_NOTIFICATION_DAYS, 
				config.getExpirationConfig().getNotificationDays());
	}

	public void testExpirationNotConfigured() throws Exception {
		removeAll("Server/UserPasswordPolicies/Expiration");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_EXPIRATION_DAYS, 
				config.getExpirationConfig().getExpirationDays());
		assertEquals(UserPasswordPoliciesConfig.ExpirationConfig.DEFAULT_NOTIFICATION_DAYS, 
				config.getExpirationConfig().getNotificationDays());
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	public void testLockoutConfigured() throws Exception {
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(3, config.getLockoutConfig().getMaxAttempts());
	}
	
	public void testLockoutConfiguredNegativeValue() throws Exception {
		replaceAttributeValue("Server/UserPasswordPolicies/Lockout", "maxAttempts", "-1");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(UserPasswordPoliciesConfig.LockoutConfig.DEFAULT_MAX_ATTEMPTS, 
				config.getLockoutConfig().getMaxAttempts());
	}
	
	public void testLockoutNotConfigured() throws Exception {
		removeAll("Server/UserPasswordPolicies/Lockout");
		UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
		assertEquals(UserPasswordPoliciesConfig.LockoutConfig.DEFAULT_MAX_ATTEMPTS, 
				config.getLockoutConfig().getMaxAttempts());
	}
    
    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorMinLengthParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("minLength") && parm.getValue().equals("6")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorMinRegexpMatchParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("minRegexpMatch") && parm.getValue().equals("3")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorRegexpUpperParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Upper}]).*")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorRegexpLowerParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Upper}]).*")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorRegexpDigitParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Digit}]).*")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorRegexpPunctParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Punct}]).*")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    // assumes test/config/PowerEditorConfiguration.xml has specific values configured
    public void testValidatorRegexpDescParameterConfigured() throws Exception {
        UserPasswordPoliciesConfig config = new UserPasswordPoliciesConfig(getPeConfigXml());
        assertNotNull(config.getValidatorConfig().getConfigParameters());        
        assertEquals(config.getValidatorConfig().getConfigParameters().size(), 7);
        boolean found = false;
        for (Iterator<ConfigParameter> i = config.getValidatorConfig().getConfigParameters().iterator(); i.hasNext();) {
            ConfigParameter parm = i.next();
            if (parm.getName().equals("description") && parm.getValue() != null) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
}

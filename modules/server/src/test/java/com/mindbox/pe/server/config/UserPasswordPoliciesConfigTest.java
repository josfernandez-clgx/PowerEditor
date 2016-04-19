package com.mindbox.pe.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.xsd.config.ParamType;

public class UserPasswordPoliciesConfigTest extends AbstractConfigXmlTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testExpirationConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertEquals(90, config.getExpirationDays());
		assertEquals(14, config.getNotificationDays());
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testLockoutConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertEquals(3, config.getMaxAttempts());
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific class configured
	@Test
	public void testValidatorClassnameConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertEquals(com.mindbox.pe.server.spi.pwd.RegexpPasswordValidator.class, config.getValidatorInstance().getClass());
	}

	@Test
	public void testValidatorHistoryConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertEquals(config.getLookback(), 3);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorMinLengthParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("minLength") && parm.getValue().equals("6")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorMinRegexpMatchParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("minRegexpMatch") && parm.getValue().equals("3")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorRegexpDescParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("description") && parm.getValue() != null) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorRegexpDigitParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Digit}]).*")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorRegexpLowerParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Upper}]).*")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorRegexpPunctParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Punct}]).*")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	// assumes test/config/PowerEditorConfiguration.xml has specific values configured
	@Test
	public void testValidatorRegexpUpperParameterConfigured() throws Exception {
		UserPasswordPoliciesConfigHelper config = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper();
		assertNotNull(config.getValidatorParams());
		assertEquals(config.getValidatorParams().size(), 7);
		boolean found = false;
		for (Iterator<ParamType> i = config.getValidatorParams().iterator(); i.hasNext();) {
			ParamType parm = i.next();
			if (parm.getName().equals("regexp") && parm.getValue().equals(".*([\\p{Upper}]).*")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

}

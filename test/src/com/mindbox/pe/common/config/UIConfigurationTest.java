package com.mindbox.pe.common.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.digest.TemplateUsageTypeDigest;

public class UIConfigurationTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("UIConfigurationTest Tests");
		suite.addTestSuite(UIConfigurationTest.class);
		return suite;
	}

	private UIConfiguration uiConfiguration;

	public UIConfigurationTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		uiConfiguration = new UIConfiguration();
	}

	public void testConstructorSetsDefaults() throws Exception {
		assertEquals(uiConfiguration.fitGridToScreen(), false);
		assertEquals(uiConfiguration.getClientWindowTitle(), "MindBox PowerEditor");
		assertEquals(uiConfiguration.showGuidelineTemplateID(), false);
		assertEquals(uiConfiguration.sortEnumValues(), false);
		assertEquals(uiConfiguration.getDefaultExpirationDays(), 0);
		assertNull(uiConfiguration.getDefaultTime());
		assertNull(uiConfiguration.getUIPolicies());
	}

	public void testAddEntityTagConfigWithNullIsNoOp() throws Exception {
		uiConfiguration.addEntityTagConfig(null);
		assertEquals(0, uiConfiguration.getEntityTabConfigurationMap().size());
	}

	public void testAddGuidelineTabConfigWithNullIsNoOp() throws Exception {
		uiConfiguration.addGuidelineTabConfig(null);
		assertEquals(0, uiConfiguration.getTabConfigurations().length);
	}

	public void testAddTemplateUsageTypeWithNullIsNoOp() throws Exception {
		uiConfiguration.addTemplateUsageType(null);
		assertEquals(0, uiConfiguration.getUsageConfigList().size());
	}

	public void testAddTemplateUsageTypeHappyCase() throws Exception {
		TemplateUsageTypeDigest usageTypeDigest = new TemplateUsageTypeDigest();
		usageTypeDigest.setName("Usage-" + ObjectMother.createInt());
		usageTypeDigest.setDisplayName("Display name of " + usageTypeDigest.getName());
		usageTypeDigest.setPrivilege("priv-" + ObjectMother.createInt());
		uiConfiguration.addTemplateUsageType(usageTypeDigest);
		assertEquals(1, uiConfiguration.getUsageConfigList().size());
		assertEquals(usageTypeDigest.getName(), uiConfiguration.getUsageConfigList().get(0).toString());
		assertEquals(usageTypeDigest.getDisplayName(), uiConfiguration.getUsageConfigList().get(0).getDisplayName());
		assertEquals(usageTypeDigest.getPrivilege(), uiConfiguration.getUsageConfigList().get(0).getPrivilege());
	}

	public void testSetUIPoliciesHappyCase() throws Exception {
		UIPolicies uiPolicies = new UIPolicies();
		uiPolicies.setEnforceSequentialActivationDates("true");
		uiConfiguration.setUIPolicies(uiPolicies);
		assertEquals(uiPolicies, uiConfiguration.getUIPolicies());
	}

	public void testSetUIPoliciesWithAlreadySetThrowsIllegalStateException() throws Exception {
		UIPolicies uiPolicies = new UIPolicies();
		uiConfiguration.setUIPolicies(uiPolicies);
		assertThrowsException(
				uiConfiguration,
				"setUIPolicies",
				new Class<?>[] { UIPolicies.class },
				new Object[] { uiPolicies },
				IllegalStateException.class);
	}

	public void testSetShowTemplateIDPositiveCase() throws Exception {
		uiConfiguration.setShowTemplateID(ConfigUtil.CONFIG_VALUE_YES);
		assertTrue(uiConfiguration.showGuidelineTemplateID());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setShowTemplateID(Boolean.TRUE.toString());
		assertTrue(uiConfiguration.showGuidelineTemplateID());
	}

	public void testSetShowTemplateIDNegativeCase() throws Exception {
		uiConfiguration.setShowTemplateID(ConfigUtil.CONFIG_VALUE_NO);
		assertFalse(uiConfiguration.showGuidelineTemplateID());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setShowTemplateID(Boolean.FALSE.toString());
		assertFalse(uiConfiguration.showGuidelineTemplateID());
	}

	public void testFitGridToScreenPositiveCase() throws Exception {
		uiConfiguration.setFitGridToScreen(ConfigUtil.CONFIG_VALUE_YES);
		assertTrue(uiConfiguration.fitGridToScreen());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setFitGridToScreen(Boolean.TRUE.toString());
		assertTrue(uiConfiguration.fitGridToScreen());
	}

	public void testFitGridToScreenNegativeCase() throws Exception {
		uiConfiguration.setFitGridToScreen(ConfigUtil.CONFIG_VALUE_NO);
		assertFalse(uiConfiguration.fitGridToScreen());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setFitGridToScreen(Boolean.FALSE.toString());
		assertFalse(uiConfiguration.fitGridToScreen());
	}

	public void testSortEnumValuePositiveCase() throws Exception {
		uiConfiguration.setSortEnumValue(ConfigUtil.CONFIG_VALUE_YES);
		assertTrue(uiConfiguration.sortEnumValues());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setSortEnumValue(Boolean.TRUE.toString());
		assertTrue(uiConfiguration.sortEnumValues());
	}

	public void testSortEnumValueNegativeCase() throws Exception {
		uiConfiguration.setSortEnumValue(ConfigUtil.CONFIG_VALUE_NO);
		assertFalse(uiConfiguration.sortEnumValues());
		uiConfiguration = new UIConfiguration();
		uiConfiguration.setSortEnumValue(Boolean.FALSE.toString());
		assertFalse(uiConfiguration.sortEnumValues());
	}

	public void testDefaultTimeStringHappyCase() throws Exception {
		String str = "12:34";
		uiConfiguration.setDefaultTimeString(str);
		assertEquals(new String[] { "12", "34" }, uiConfiguration.getDefaultTime());
	}

	public void testDefaultTimeStringWithNullIsNoOp() throws Exception {
		uiConfiguration.setDefaultTimeString(null);
		assertNull(uiConfiguration.getDefaultTime());
	}
}

package com.mindbox.pe.server.config;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.EntityPropertyTabDefinition;
import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;

public class ConfigXMLDigesterTest extends ConfigXmlTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("ConfigXMLDigesterTest Tests");
		suite.addTestSuite(ConfigXMLDigesterTest.class);
		return suite;
	}

	public ConfigXMLDigesterTest(String name) throws Exception {
		super(name);
	}


	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDigestServerSessionConfig_HappyCaseWithNoAuthProvider() throws Exception {
		SessionConfiguration sessionConfiguration = new SessionConfiguration(new FileReader(
				"test/data/PEServerSessionConfigWithAuthProvider.xml"));

		assertEquals("SomeCookie", sessionConfiguration.getUserIDCookie());
		assertEquals("http://login.url.com", sessionConfiguration.getLoginUrl());
		assertEquals("http://logout.url.com", sessionConfiguration.getLogoutUrlFromConfig());
		assertEquals("LogoutPath", sessionConfiguration.getLogoutHttpHeader());
		assertEquals(4, sessionConfiguration.getMaxUserSessions());
		assertEquals("com.xyz.powereditor.security.CustomAuthenticator", sessionConfiguration.getUserAuthenticationProviderClassName());
	}

	public void testDigestEntityConfigXMLHappyCaseForEntityTypeDefinition() throws Exception {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		EntityConfiguration entityConfiguration = new EntityConfiguration();
		String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PowerEditorConfiguration><EntityConfig>"
				+ "<EntityType name=\"product\" displayName=\"Product\" typeID=\"0\" useInContext=\"Yes\" useInCompatibility=\"No\" canClone=\"Yes\" useInMessageContext=\"Yes\" categoryType=\"10\"/>"
				+ "<EntityType name=\"investor\" displayName=\"Investor\" typeID=\"1\" useInContext=\"No\" useInCompatibility=\"Yes\" canClone=\"No\" categoryType=\"20\">"
				+ "<EntityProperty name=\"code\" displayName=\"Code\" showInSelectionTable=\"Yes\" isRequired=\"No\" isSearchable=\"Yes\" type=\"enum\" enumType=\"program.code\" sort=\"Yes\" allowMultiple=\"No\"/>"
				+ "<EntityProperty name=\"enum2\" displayName=\"Enum2\" showInSelectionTable=\"false\" isRequired=\"true\" isSearchable=\"false\" type=\"enum\" enumType=\"program.enum2\" sort=\"false\" allowMultiple=\"true\"/>"
				+ "</EntityType>" + "</EntityConfig></PowerEditorConfiguration>";
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
		ConfigXMLDigester.getInstance().digestEntityConfigXML(reader, entityConfiguration);
		EntityTypeDefinition[] entityTypeDefinitions = entityConfiguration.getEntityTypeDefinitions();
		assertEquals(2, entityTypeDefinitions.length);
		boolean productFound = false;
		boolean investorFound = false;
		for (int i = 0; i < entityTypeDefinitions.length; i++) {
			switch (entityTypeDefinitions[i].getTypeID()) {
			case 0: {
				productFound = true;
				assertEquals("product", entityTypeDefinitions[i].getName());
				assertEquals("Product", entityTypeDefinitions[i].getDisplayName());
				assertFalse(entityTypeDefinitions[i].useInCompatibility());
				assertTrue(entityTypeDefinitions[i].canClone());
				assertTrue(entityTypeDefinitions[i].useInMessageContext());
				assertTrue(entityTypeDefinitions[i].useInContext());
				assertEquals(10, entityTypeDefinitions[i].getCategoryType());
				break;
			}
			case 1: {
				investorFound = true;
				assertEquals("investor", entityTypeDefinitions[i].getName());
				assertEquals("Investor", entityTypeDefinitions[i].getDisplayName());
				assertTrue(entityTypeDefinitions[i].useInCompatibility());
				assertFalse(entityTypeDefinitions[i].canClone());
				assertFalse(entityTypeDefinitions[i].useInMessageContext());
				assertFalse(entityTypeDefinitions[i].useInContext());
				assertEquals(20, entityTypeDefinitions[i].getCategoryType());
				EntityPropertyDefinition[] propDefs = entityTypeDefinitions[i].getEntityPropertyDefinitions();
				assertEquals(2, propDefs.length);
				for (int j = 0; j < propDefs.length; j++) {
					if (!(propDefs[j].getName().equals("code") || propDefs[j].getName().equals("enum2"))) {
						fail("Unexpected property name for investor: " + propDefs[i].getName());
					}
					else if (propDefs[j].getName().equals("code")) {
						assertEquals("Code", propDefs[j].getDisplayName());
						assertTrue(propDefs[j].useInSelectionTable());
						assertFalse(propDefs[j].isRequired());
						assertTrue(propDefs[j].isSearchable());
						assertEquals("enum", propDefs[j].getType());
						assertEquals("program.code", propDefs[j].getEnumType());
						assertFalse(propDefs[j].allowMultiple());
						assertTrue(propDefs[j].sort());
					}
					else {
						assertEquals("Enum2", propDefs[j].getDisplayName());
						assertFalse(propDefs[j].useInSelectionTable());
						assertTrue(propDefs[j].isRequired());
						assertFalse(propDefs[j].isSearchable());
						assertEquals("enum", propDefs[j].getType());
						assertEquals("program.enum2", propDefs[j].getEnumType());
						assertTrue(propDefs[j].allowMultiple());
						assertFalse(propDefs[j].sort());
					}
				}
				break;
			}
			default:
				fail("Unexpected entity type id " + entityTypeDefinitions[i].getTypeID());
			}
		}
		assertTrue(productFound);
		assertTrue(investorFound);
	}

	public void testEntityConfigXMLHandlesCategoryTypeDefinitionProperly() throws Exception {
		EntityConfiguration entityConfiguration = new EntityConfiguration();
		ConfigXMLDigester.getInstance().digestEntityConfigXML(getPeConfigXml(), entityConfiguration);
		CategoryTypeDefinition[] catTypeDefs = entityConfiguration.getCategoryTypeDefinitions();
		assertEquals(5, catTypeDefs.length);
		for (int i = 0; i < catTypeDefs.length; i++) {
			switch (catTypeDefs[i].getTypeID()) {
			case 10: {
				assertEquals("Product Category", catTypeDefs[i].getName());
				assertTrue(catTypeDefs[i].useInSelectionTable());
				break;
			}
			case 20: {
				assertEquals("Program Category", catTypeDefs[i].getName());
				assertFalse(catTypeDefs[i].useInSelectionTable());
				break;
			}
			case 30: {
				assertEquals("Channel Category", catTypeDefs[i].getName());
				assertTrue(catTypeDefs[i].useInSelectionTable());
				break;
			}
			case 40: {
				assertEquals("Investor Category", catTypeDefs[i].getName());
				assertFalse(catTypeDefs[i].useInSelectionTable());
				break;
			}
			case 50: {
				assertEquals("Branch Category", catTypeDefs[i].getName());
				assertFalse(catTypeDefs[i].useInSelectionTable());
				break;
			}
			default:
				fail("Unexpected type id: " + catTypeDefs[i].getTypeID());
			}
		}
	}

	public void testDigestUserInterfaceConfigXML() throws Exception {
		removeAll("EntityConfig", "name", "program");
		EntityConfiguration entityConfiguration = new EntityConfiguration();
		ConfigXMLDigester.getInstance().digestEntityConfigXML(getPeConfigXml(), entityConfiguration);
		// create generic entity types
		EntityTypeDefinition[] typeDefs = entityConfiguration.getEntityTypeDefinitions();
		for (int i = 0; i < typeDefs.length; i++) {
			if (!GenericEntityType.hasTypeFor(typeDefs[i].getTypeID())) {
				GenericEntityType.makeInstance(typeDefs[i]);
			}
		}

		UIConfiguration uiConfiguration = new UIConfiguration();
		ConfigXMLDigester.getInstance().digestUserInterfaceConfigXML(getPeConfigXml(), uiConfiguration);
		assertEquals("Welcome to PowerEditor", uiConfiguration.getClientWindowTitle());
		assertEquals(new String[] { "00", "00" }, uiConfiguration.getDefaultTime());
		assertTrue(uiConfiguration.fitGridToScreen());
		assertTrue(uiConfiguration.showGuidelineTemplateID());
		assertTrue(uiConfiguration.sortEnumValues());
		assertNotNull(uiConfiguration.getUIPolicies());
		assertTrue(uiConfiguration.getUIPolicies().isSequentialActivationDatesEnfored());
		assertEquals(30, uiConfiguration.getDefaultExpirationDays());

		// Check usage types
		List<TemplateUsageType> usageTypeList = uiConfiguration.getUsageConfigList();
		assertEquals(29, usageTypeList.size());

		GuidelineTabConfig[] tabConfigs = uiConfiguration.getTabConfigurations();
		assertEquals(10, tabConfigs.length);
		assertEquals("Qualification", tabConfigs[0].getTitle());
		assertEquals("Pricing", tabConfigs[1].getTitle());
		assertEquals("Others", tabConfigs[9].getTitle());
		assertEquals(7, tabConfigs[0].getUsageTypes().length);
		assertEquals(3, tabConfigs[1].getUsageTypes().length);
		assertEquals(1, tabConfigs[9].getUsageTypes().length);

		// Check entity tab configs
		List<EntityTabConfig> tabConfigList = new LinkedList<EntityTabConfig>();
		tabConfigList.addAll(uiConfiguration.getEntityTabConfigurationMap().values());
		assertEquals(5, tabConfigList.size());
		assertEquals("product", tabConfigList.get(0).getType());
		assertEquals("channel", tabConfigList.get(1).getType());
		assertEquals("investor", tabConfigList.get(2).getType());

		for (Iterator<EntityTabConfig> iter = tabConfigList.iterator(); iter.hasNext();) {
			EntityTabConfig element = iter.next();
			if (element.getType() == "channel") {
				assertTrue(element.isVisible());
			}
			else if (element.getType() == "investor") {
				assertFalse(element.isVisible());
			}
			else if (element.getType() == "product") {
				assertTrue(element.isVisible());
				EntityPropertyTabDefinition[] tabDefs = element.getEntityPropertyTagDefinitions();
				assertEquals(3, tabDefs.length);
				assertEquals("Product Details", tabDefs[0].getTitle());
				assertEquals("Synchronziation Details", tabDefs[1].getTitle());
				assertEquals("ARM Info", tabDefs[2].getTitle());

				List<String> list = new LinkedList<String>();
				list.add("description");
				list.add("assumable");
				list.add("convertible");
				list.add("prepay.penalty");
				list.add("a.paper");
				list.add("negative.amort");
				list.add("min.credit");
				list.add("loan.type");
				list.add("lien.priority");
				list.add("amortization.type");
				list.add("amortization.terms");
				list.add("product.type");
				list.add("pricing.group");
				list.add("status");
				list.add("status.change.date");
				list.add("activation.date");
				list.add("expiration.date");
				assertEquals(list, Arrays.asList(tabDefs[0].getPropertyNames()));

				list.clear();
				list.add("assumption.type");
				list.add("calculation.type");
				list.add("days.late");
				list.add("hazard.insurance.required");
				list.add("hazard.insurance.amount");
				list.add("late.charged");
				list.add("late.charge.method");
				list.add("late.charge.percent");
				list.add("late.charge.type");
				list.add("penalty");
				list.add("prepay.penalty.percent");
				list.add("priority");
				list.add("property.insurance");
				list.add("refund");
				list.add("security");
				list.add("security.type");
				list.add("prepaid.interest.factor");
				list.add("documentation.type");
				list.add("interest.only.period");
				list.add("buydown.not.allowed");
				list.add("heloc");
				assertEquals(list, Arrays.asList(tabDefs[1].getPropertyNames()));

				list.clear();
				list.add("arm.index.name");
				list.add("arm.index.value");
				list.add("arm.first.adjust.period");
				list.add("arm.first.adjust.cap");
				list.add("arm.later.adjust.period");
				list.add("arm.later.adjust.cap");
				list.add("arm.first.payment.period");
				list.add("arm.first.payment.cap");
				list.add("arm.later.payment.period");
				list.add("arm.later.payment.cap");
				list.add("arm.reamort.period");
				list.add("deferred.limit");
				assertEquals(list, Arrays.asList(tabDefs[2].getPropertyNames()));
			}
		}
	}
}

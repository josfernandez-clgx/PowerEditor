package com.mindbox.pe.server.config;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityPropertyDefinition;
import com.mindbox.pe.common.config.EntityPropertyGroupDefinition;
import com.mindbox.pe.common.config.EntityPropertyTabDefinition;
import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.config.FeatureDefinition;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.common.config.UIPolicies;
import com.mindbox.pe.common.digest.TemplateUsageTypeDigest;

/**
 * @author Geneho Kim
 * @author MindBox
 * @author Inna Nill
 * @since PowerEditor 2.5.0
 */
public class ConfigXMLDigester {

	private static ConfigXMLDigester instance = null;

	public static ConfigXMLDigester getInstance() {
		if (instance == null) {
			instance = new ConfigXMLDigester();
		}
		return instance;
	}

	private ConfigXMLDigester() {
		// required to write Log4J logging of Digester
		Logger.getLogger("org.apache.commons.digester.Digester").setLevel(Level.WARN);
	}

	/**
	 * Digest feature configuration part of PE config file.
	 * @param config
	 * @param featureConfig
	 * @throws SAXException
	 * @throws IOException
	 * @since PowerEditor 4.0.1
	 */
	public void digestKnowledgeBaseFilterConfigXML(Reader config, KnowledgeBaseFilterConfig knowledgeBaseFilterConfig) throws IOException,
			SAXException {
		Digester digester = new Digester();
		digester.push(knowledgeBaseFilterConfig);
		digester.setValidating(false);

		digester.addObjectCreate("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter", DateFilterConfig.class);
		digester.addCallMethod("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter/BeginDate", "setBeginDateXmlString", 1);
		digester.addCallParam("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter/BeginDate", 0);
		digester.addCallMethod("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter/EndDate", "setEndDateXmlString", 1);
		digester.addCallParam("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter/EndDate", 0);
		digester.addSetNext("PowerEditorConfiguration/KnowledgeBaseFilter/DateFilter", "setDateFilterConfig");

		digester.parse(config);
	}

	/**
	 * Digest feature configuration part of PE config file.
	 * @param config
	 * @param featureConfig
	 * @throws SAXException
	 * @throws IOException
	 * @since PowerEditor 4.0.1
	 */
	public void digestFeatureConfigXML(Reader config, FeatureConfiguration featureConfig) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(featureConfig);
		digester.setValidating(false);

		digester.addObjectCreate("PowerEditorConfiguration/FeatureConfig/Feature", FeatureDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/FeatureConfig/Feature");
		digester.addSetNext("PowerEditorConfiguration/FeatureConfig/Feature", "addFeature");

		digester.parse(config);
	}

	public void digestKBConfigXML(Reader config, ConfigurationManager configManager) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(configManager);
		digester.setValidating(false);

		digester.addCallMethod("PowerEditorConfiguration/Server/KnowledgeBase/DomainFile", "addDomainDefinitionFile", 1);
		digester.addCallParam("PowerEditorConfiguration/Server/KnowledgeBase/DomainFile", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/KnowledgeBase/TemplateFile", "addTemplateDefinitionFile", 1);
		digester.addCallParam("PowerEditorConfiguration/Server/KnowledgeBase/TemplateFile", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/KnowledgeBase/CBRFile", "addCBRDefinitionFile", 1);
		digester.addCallParam("PowerEditorConfiguration/Server/KnowledgeBase/CBRFile", 0);

		digester.parse(config);
	}

	public void digestEntityConfigXML(Reader config, EntityConfiguration entityConfig) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(entityConfig);
		digester.setValidating(false);

		// entity configuration rules -----------------------

		digester.addObjectCreate("PowerEditorConfiguration/EntityConfig/CategoryType", CategoryTypeDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/EntityConfig/CategoryType");
		digester.addSetNext("PowerEditorConfiguration/EntityConfig/CategoryType", "addObject");

		digester.addObjectCreate("PowerEditorConfiguration/EntityConfig/EntityType", EntityTypeDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/EntityConfig/EntityType");
		digester.addSetNext("PowerEditorConfiguration/EntityConfig/EntityType", "addObject");

		digester.addObjectCreate("PowerEditorConfiguration/EntityConfig/EntityType/EntityProperty", EntityPropertyDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/EntityConfig/EntityType/EntityProperty");
		digester.addSetNext("PowerEditorConfiguration/EntityConfig/EntityType/EntityProperty", "addPropertyDefinition");

		// added in PE 4.3.1
		digester.addObjectCreate(
				"PowerEditorConfiguration/EntityConfig/EntityType/EntityPropertyGroup",
				EntityPropertyGroupDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/EntityConfig/EntityType/EntityPropertyGroup");
		digester.addSetNext("PowerEditorConfiguration/EntityConfig/EntityType/EntityPropertyGroup", "addPropertyGroup");

		digester.parse(config);
	}

	public void digestUserInterfaceConfigXML(Reader config, UIConfiguration uiConfiguration) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(uiConfiguration);
		digester.setValidating(false);

		digester.addObjectCreate("PowerEditorConfiguration/UserInterface/Entity/EntityTab", EntityTabConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/UserInterface/Entity/EntityTab");
		digester.addSetNext("PowerEditorConfiguration/UserInterface/Entity/EntityTab", "addEntityTagConfig");

		digester.addObjectCreate(
				"PowerEditorConfiguration/UserInterface/Entity/EntityTab/EntityPropertyTab",
				EntityPropertyTabDefinition.class);
		digester.addSetProperties("PowerEditorConfiguration/UserInterface/Entity/EntityTab/EntityPropertyTab");
		digester.addCallMethod(
				"PowerEditorConfiguration/UserInterface/Entity/EntityTab/EntityPropertyTab/EntityPropertyName",
				"addPropertyName",
				0);
		digester.addSetNext("PowerEditorConfiguration/UserInterface/Entity/EntityTab/EntityPropertyTab", "addEntityPropertyTabDefinition");

		digester.addObjectCreate("PowerEditorConfiguration/UserInterface/UsageTypeList/UsageType", TemplateUsageTypeDigest.class);
		digester.addSetProperties("PowerEditorConfiguration/UserInterface/UsageTypeList/UsageType");
		digester.addSetNext("PowerEditorConfiguration/UserInterface/UsageTypeList/UsageType", "addTemplateUsageType");

		digester.addSetProperties("PowerEditorConfiguration/UserInterface/Guideline");
		digester.addObjectCreate("PowerEditorConfiguration/UserInterface/Guideline/GuidelineTab", GuidelineTabConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/UserInterface/Guideline/GuidelineTab");
		digester.addSetNext("PowerEditorConfiguration/UserInterface/Guideline/GuidelineTab", "addGuidelineTabConfig");
		digester.addSetProperties("PowerEditorConfiguration/UserInterface/Guideline/GuidelineTab/UsageType", "name", "usageType");

		digester.addCallMethod("PowerEditorConfiguration/UserInterface/DateSynonym/DefaultTime", "setDefaultTimeString", 0);
		digester.addCallMethod(
				"PowerEditorConfiguration/UserInterface/DeployExpirationDate/DefaultDays",
				"setDefaultExpirationDaysString",
				0);
		digester.addCallMethod("PowerEditorConfiguration/UserInterface/ClientWindowTitle", "setClientWindowTitle", 0);
		digester.addCallMethod("PowerEditorConfiguration/UserInterface/UserDisplayNameAttribute", "setUserDisplayNameAttributeValue", 0);
		digester.addCallMethod("PowerEditorConfiguration/UserInterface/AllowDisableEnableUser", "setAllowDisableEnableUserFlag", 0);

		digester.addObjectCreate("PowerEditorConfiguration/UserInterface/UIPolicies", UIPolicies.class);
		digester.addCallMethod(
				"PowerEditorConfiguration/UserInterface/UIPolicies/EnforceSequentialActivationDates",
				"setEnforceSequentialActivationDates",
				0);
		digester.addCallMethod(
				"PowerEditorConfiguration/UserInterface/UIPolicies/AllowGapsInActivationDates",
				"setAllowGapsInActivationDates",
				0);
		digester.addSetNext("PowerEditorConfiguration/UserInterface/UIPolicies", "setUIPolicies");

		digester.parse(config);
	}

	public void digestRuleGenerationXML(Reader config, ConfigurationManager configManager) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(configManager.getRuleGenerationConfigurationDefault());
		digester.setValidating(false);

		RuleGenerationOverrideCreationFactory rgOverrideCreationFactory = new RuleGenerationOverrideCreationFactory(configManager);

		// rule generation configuration rules -----------------------

		digester.addObjectCreate("PowerEditorConfiguration/RuleGeneration/RuleGenerationDefault/LHS/Value", RuleLHSValueConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/RuleGeneration/RuleGenerationDefault/LHS/Value");
		digester.addSetNext("PowerEditorConfiguration/RuleGeneration/RuleGenerationDefault/LHS/Value", "addLHSValueConfig");

		digester.addFactoryCreate("PowerEditorConfiguration/RuleGeneration/RuleGenerationOverride", rgOverrideCreationFactory);
		digester.addObjectCreate("PowerEditorConfiguration/RuleGeneration/RuleGenerationOverride/LHS/Value", RuleLHSValueConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/RuleGeneration/RuleGenerationOverride/LHS/Value");
		digester.addSetNext("PowerEditorConfiguration/RuleGeneration/RuleGenerationOverride/LHS/Value", "addLHSValueConfig");

		digester.addObjectCreate(
				"PowerEditorConfiguration/RuleGeneration/ObjectGenerationDefault/ParameterContext",
				ParameterContextConfiguration.class);
		digester.addObjectCreate(
				"PowerEditorConfiguration/RuleGeneration/ObjectGenerationDefault/ParameterContext/Attribute",
				AttributeConfiguration.class);
		digester.addSetProperties("PowerEditorConfiguration/RuleGeneration/ObjectGenerationDefault/ParameterContext/Attribute");
		digester.addSetNext(
				"PowerEditorConfiguration/RuleGeneration/ObjectGenerationDefault/ParameterContext/Attribute",
				"addAttributeConfiguration");
		digester.addSetNext(
				"PowerEditorConfiguration/RuleGeneration/ObjectGenerationDefault/ParameterContext",
				"setParameterContextConfiguration");

		digester.parse(config);
	}

	/**
	 * Extracts enumeration source configuration from the specified config file.
	 * @param config
	 */
	public void digestEnumerationSourceConfig(Reader config, EnumerationSourceConfigSet enumerationSourceConfigSet) throws IOException,
			SAXException {
		Digester digester = new Digester();
		digester.push(enumerationSourceConfigSet);
		digester.setValidating(false);

		// rule generation configuration rules -----------------------
		digester.addObjectCreate("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource", EnumerationSourceConfig.class);
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/Type", "type");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/Name", "name");
		digester.addBeanPropertySetter(
				"PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/SupportsSelector",
				"supportsSelector");
		digester.addObjectCreate("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/Param", ConfigParameter.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/Param");
		digester.addSetNext("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource/Param", "addConfigParameter");
		digester.addSetNext("PowerEditorConfiguration/Server/EnumerationSources/EnumerationSource", "add");

		digester.parse(config);
	}

	/**
	 * Extracts LDAP connection configuration from the specified config file.
	 * @param config
	 */
	public void digestLDAPConnectionConfig(Reader config, LDAPConnectionConfig ldapConfig) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(ldapConfig);
		digester.setValidating(false);

		// rule generation configuration rules -----------------------
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP", "setLdapConfigured");
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/Connection", "setConnectionURL", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/Principal", "setPrincipal", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/Credentials", "setEncryptedCredentials", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserDirectoryDN", "addUserDirectoryDN", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserObjectClass", "setUserObjectClass", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserObjectClassHierarchy", "setUserObjectClassHierarchy", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserIDAttribute", "setUserIDAttribute", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserNameAttribute", "setUserNameAttribute", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserPasswordAttribute", "setUserPasswordAttribute", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserRolesAttribute", "setUserRolesAttribute", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/UserStatusAttribute", "setUserStatusAttribute", 0);
		digester.addCallMethod("PowerEditorConfiguration/Server/LDAP/AuthenticationScheme", "setAuthenticationScheme", 0);

		digester.parse(config);
	}

	/**
	 * Extracts user password policies configuration from the specified config file.
	 * @param config
	 */
	public void digestUserPasswordPoliciesConfig(Reader config, UserPasswordPoliciesConfig pwdConfig) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(pwdConfig);
		digester.setValidating(false);

		digester.addObjectCreate(
				"PowerEditorConfiguration/Server/UserPasswordPolicies/Validator",
				UserPasswordPoliciesConfig.ValidatorConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/UserPasswordPolicies/Validator");
		digester.addSetNext("PowerEditorConfiguration/Server/UserPasswordPolicies/Validator", "setValidatorConfig");

		digester.addObjectCreate("PowerEditorConfiguration/Server/UserPasswordPolicies/Validator/Param", ConfigParameter.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/UserPasswordPolicies/Validator/Param");
		digester.addSetNext("PowerEditorConfiguration/Server/UserPasswordPolicies/Validator/Param", "addConfigParameter");

		digester.addObjectCreate(
				"PowerEditorConfiguration/Server/UserPasswordPolicies/Expiration",
				UserPasswordPoliciesConfig.ExpirationConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/UserPasswordPolicies/Expiration");
		digester.addSetNext("PowerEditorConfiguration/Server/UserPasswordPolicies/Expiration", "setExpirationConfig");

		digester.addObjectCreate(
				"PowerEditorConfiguration/Server/UserPasswordPolicies/History",
				UserPasswordPoliciesConfig.HistoryConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/UserPasswordPolicies/History");
		digester.addSetNext("PowerEditorConfiguration/Server/UserPasswordPolicies/History", "setHistoryConfig");

		digester.addObjectCreate(
				"PowerEditorConfiguration/Server/UserPasswordPolicies/Lockout",
				UserPasswordPoliciesConfig.LockoutConfig.class);
		digester.addSetProperties("PowerEditorConfiguration/Server/UserPasswordPolicies/Lockout");
		digester.addSetNext("PowerEditorConfiguration/Server/UserPasswordPolicies/Lockout", "setLockoutConfig");

		digester.parse(config);
	}

	public void digestServerSessionConfig(Reader configReader, SessionConfiguration sessionConfiguration) throws IOException, SAXException {
		Digester digester = new Digester();
		digester.push(sessionConfiguration);
		digester.setValidating(false);

		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/MaxUserSessions", "maxUserSessionsStr");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/MaxAuthenticationsAttempts", "maxAuthenticationAttempts");
		digester.addBeanPropertySetter(
				"PowerEditorConfiguration/Server/Session/UserAuthenticationProviderClass",
				"userAuthenticationProviderClassName");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/UserIDCookie", "userIDCookie");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/LoginUrl", "loginUrl");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/LogoutUrl", "logoutUrlFromConfig");
		digester.addBeanPropertySetter("PowerEditorConfiguration/Server/Session/LogoutHttpHeader", "logoutHttpHeader");

		digester.parse(configReader);
	}
}
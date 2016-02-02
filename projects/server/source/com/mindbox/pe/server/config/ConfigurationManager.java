package com.mindbox.pe.server.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.PowerEditorXMLParser;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * PE configuration manager.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public final class ConfigurationManager {

	/* This auxilary class keeps track of all the RuleGeneration objects.  
	 * The default rule generation class is stored in a defaultRuleGenerationConfig.
	 * Jun 29, 2004
	 * @author Beth
	 */
	private static final class RuleGenerationConfigList extends HashMap<Object, RuleGenerationConfiguration> {

		private static final long serialVersionUID = -4302996408739395014L;

		private RuleGenerationConfiguration defaultRuleGenerationConfig;

		private RuleGenerationConfigList(Element ruleGenRootElement) {
			// first, create the ruleGenerationDefault
			Element ruleGenDefault = PowerEditorXMLParser.getFirstChild(ruleGenRootElement, "RuleGenerationDefault");
			if (ruleGenDefault == null) {
				throw new ConfigurationException("RuleGeneration element has no <RuleGenerationDefault> tag");
			}
			defaultRuleGenerationConfig = new RuleGenerationConfiguration(ruleGenDefault);
			this.put("defaultRuleGen", defaultRuleGenerationConfig); // key value not used

			// now, create the usageTypeOverrides					
			NodeList elementList = ruleGenRootElement.getElementsByTagName("RuleGenerationOverride");
			if (elementList != null) { //overrides are not required
				for (int i = 0; i < elementList.getLength(); i++) {
					Element utElement = (Element) elementList.item(i);
					String usageStr = utElement.getAttribute("usageType");
					if (usageStr == null || usageStr.trim().length() == 0)
						throw new ConfigurationException("RuleGenerationOverride element has no usageType attribute");

					TemplateUsageType usageType = TemplateUsageType.valueOf(usageStr.trim());
					if (usageType == null)
						throw new ConfigurationException("Undefined usageType " + usageStr + " in RuleGenerationOverride element");
					this.put(usageType, RuleGenerationConfiguration.newOverride(defaultRuleGenerationConfig, usageType, utElement));
				}
			}

			// Now, create the objectGenerationDefault
			Element objectGenDefault = PowerEditorXMLParser.getFirstChild(ruleGenRootElement, "ObjectGenerationDefault");
			if (objectGenDefault == null) {
				throw new ConfigurationException("RuleGeneration element has no <ObjectGenerationDefault> tag");
			}
			Element instanceCreateText = PowerEditorXMLParser.getFirstChild(objectGenDefault, "InstanceCreateText");
			if (instanceCreateText == null) {
				throw new ConfigurationException("ObjectGenerationDefault element has no <InstanceCreateText> tag");
			}
			defaultRuleGenerationConfig.setObjectGenInstanceCreateText(PowerEditorXMLParser.getValueOfFirstChild(
					objectGenDefault,
					"InstanceCreateText"));
		}


		/** Finds the ruleGenerationConfig object for the given usageType.
		 * If the usageType is not found, the default config object is returned.
		 * @param usageType
		 * @return the rule generation configuration for <code>usageType</code>
		 */
		public RuleGenerationConfiguration getRuleGenerationConfiguration(TemplateUsageType usageType) {
			RuleGenerationConfiguration result = null;
			// if a specific usage type is given, look for it
			if (usageType != null) result = (RuleGenerationConfiguration) get(usageType);


			// if usageType not found or is null, return the default
			if (result == null) result = defaultRuleGenerationConfig;

			if (result == null) {
				throw new ConfigurationException("RuleGenerationConfiguration has no defaultRuleGenerationConfig");
			}

			return result;
		}

		public Map<Object, MessageConfiguration> getCondMsgDelims() {
			Map<Object, MessageConfiguration> condMsgDelims = new HashMap<Object, MessageConfiguration>();
			for (Iterator<Object> usageTypesIter = this.keySet().iterator(); usageTypesIter.hasNext();) {
				Object obj = usageTypesIter.next();
				if (obj instanceof String) {
					condMsgDelims.put(obj.toString(), defaultRuleGenerationConfig.getMessageConfig());
				}
				else {
					TemplateUsageType usageType = (TemplateUsageType) obj;
					condMsgDelims.put(usageType, getRuleGenerationConfiguration(usageType).getMessageConfig());
				}
			}
			return condMsgDelims;
		}
	}

	private static ConfigurationManager INSTANCE = null;

	public static ConfigurationManager getInstance() {
		if (INSTANCE == null) throw new IllegalStateException("Initialize ConfigurationManager first");
		return INSTANCE;
	}

	public static void initialize(String configFilename) {
		try {
			INSTANCE = new ConfigurationManager(configFilename);
		}
		catch (RuntimeException re) {
			String msg = "Failed to initalize PE Configuration from " + configFilename + ": " + re.getMessage();
			Logger.getLogger(ConfigurationManager.class).fatal(msg, re);
			throw re;
		}
		catch (Exception e) {
			String msg = "Failed to initalize PE Configuration from " + configFilename + ": " + e.getMessage();
			Logger.getLogger(ConfigurationManager.class).fatal(msg, e);
			throw new IllegalArgumentException(msg);
		}
	}


	private final ServerConfiguration serverConfig;
	private final RuleGenerationConfigList ruleConfigList;
	private final UIConfiguration uiConfig;
	private final String filename;
	private final EntityConfiguration entityConfig;
	private final List<String> domainDefinitionFileList = new LinkedList<String>();
	private final List<String> templateDefinitionFileList = new LinkedList<String>();
	private final List<String> cbrDefinitionFileList = new LinkedList<String>();
	private final LDAPConnectionConfig ldapConfig;
	private final EnumerationSourceConfigSet enumerationSourceConfigSet;
	private final SessionConfiguration sessionConfiguration;

	/** @since PowerEditor 4.0.1 */
	private final FeatureConfiguration featureConfiguration;

	/** @since PowerEditor 5.1.0 */
	private final UserPasswordPoliciesConfig userPasswordPoliciesConfig;

	/** @since PowerEditor 5.7.2 */
	private final KnowledgeBaseFilterConfig knowledgeBaseFilterConfig;

	private String serverBasePath = null;

	private final Logger logger = Logger.getLogger(getClass());

	private ConfigurationManager(String filename) throws SAXException, IOException, ParserConfigurationException, SQLException {
		this.filename = filename;

		// Load KnowledgeBase filters
		logger.debug("loading Knowledgedbase filter...");
		knowledgeBaseFilterConfig = new KnowledgeBaseFilterConfig();
		ConfigXMLDigester.getInstance().digestKnowledgeBaseFilterConfigXML(openFile(filename), knowledgeBaseFilterConfig);
		logger.info("DateFilterConfig = " + knowledgeBaseFilterConfig.getDateFilterConfig());

		// Load features
		logger.debug("loading feature configuration...");
		featureConfiguration = new FeatureConfiguration();
		ConfigXMLDigester.getInstance().digestFeatureConfigXML(openFile(filename), featureConfiguration);

		// Load KB file configs
		logger.debug("loading KB configuration...");
		ConfigXMLDigester.getInstance().digestKBConfigXML(openFile(filename), this);

		String[] domainFiles = getDomainDefinitionFiles();
		if (domainFiles == null || domainFiles.length == 0) {
			throw new ConfigurationException("No domain definition file is specified in the configuration file");
		}
		else {
			for (int i = 0; i < domainFiles.length; i++) {
				openFile(domainFiles[i], "domain definition file");
			}
		}

		// load parameter template files, if feature is enabled
		if (getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.PARAMETER_FEATURE)) {
			String[] templateFiles = getTemplateDefinitionFiles();
			if (templateFiles != null && templateFiles.length > 0) {
				for (int i = 0; i < templateFiles.length; i++) {
					openFile(templateFiles[i], "template definition file");
				}
			}
		}

		// load CBR XML files, if feature is enabled
		if (getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.CBR_FEATURE)) {
			String[] cbrFiles = getCBRDefinitionFiles();
			if (cbrFiles != null && cbrFiles.length > 0) {
				for (int i = 0; i < cbrFiles.length; i++) {
					openFile(cbrFiles[i], "CBR definition file");
				}
			}
		}

		// Load Entity confurations
		logger.debug("loading Entity configuration...");
		this.entityConfig = new EntityConfiguration();
		ConfigXMLDigester.getInstance().digestEntityConfigXML(openFile(filename), entityConfig);

		// create generic entity types
		EntityTypeDefinition[] typeDefs = entityConfig.getEntityTypeDefinitions();
		for (int i = 0; i < typeDefs.length; i++) {
			if (!GenericEntityType.hasTypeFor(typeDefs[i].getTypeID())) {
				GenericEntityType.makeInstance(typeDefs[i]);
			}
		}

		// load config XML using old approach (non-digester)
		Document xmlDocument = PowerEditorXMLParser.getInstance().loadXML(filename);
		Element rootElement = xmlDocument.getDocumentElement();
		if (!rootElement.getTagName().equals("PowerEditorConfiguration")) {
			throw new ConfigurationException(filename + " does not contain <PowerEditorConfiguration> root tag");
		}

		if (PowerEditorXMLParser.getFirstChild(rootElement, "RuleGeneration") == null) {
			throw new ConfigurationException(filename + " does not contain <RuleGeneration> tag");
		}

		if (PowerEditorXMLParser.getFirstChild(rootElement, "Server") == null) {
			throw new ConfigurationException(filename + " does not contain <Server> tag");
		}

		if (PowerEditorXMLParser.getFirstChild(rootElement, "UserInterface") == null) {
			throw new ConfigurationException(filename + " does not contain <UserInterface> tag");
		}

		logger.debug("loading Server configuration...");
		serverConfig = new ServerConfiguration(PowerEditorXMLParser.getFirstChild(rootElement, "Server"));

		logger.debug("loading Enumeration source configuration, if any...");
		this.enumerationSourceConfigSet = new EnumerationSourceConfigSet(openFile(filename));
		logger.debug("Enumeration Source config = " + enumerationSourceConfigSet);

		logger.debug("loading LDAP configuration, if any...");
		this.ldapConfig = new LDAPConnectionConfig(openFile(filename));
		logger.debug("LDAP configuration = " + ldapConfig);

		logger.debug("loading session configuration...");
		this.sessionConfiguration = new SessionConfiguration(openFile(filename));
		this.sessionConfiguration.postParseProcess(serverConfig.getDatabaseConfig().getUserManagementProviderClassName());
		logger.debug("Session configuration = " + sessionConfiguration);

		logger.debug("loading UI configuration...");
		uiConfig = new UIConfiguration();

		ConfigXMLDigester.getInstance().digestUserInterfaceConfigXML(openFile(filename), uiConfig);
		uiConfig.validate();
		logger.info("Loaded " + uiConfig);

		// This must be loaded after UI, since usageTypeList is in UI, and ruleConfig needs usageTypes
		logger.debug("loading RuleGeneration configuration...");
		ruleConfigList = new RuleGenerationConfigList(PowerEditorXMLParser.getFirstChild(rootElement, "RuleGeneration"));

		// load LHS/Value tags using digester
		ConfigXMLDigester.getInstance().digestRuleGenerationXML(openFile(filename), this);

		this.userPasswordPoliciesConfig = new UserPasswordPoliciesConfig(openFile(filename));
	}

	public SessionConfiguration getSessionConfiguration() {
		return sessionConfiguration;
	}

	public KnowledgeBaseFilterConfig getKnowledgeBaseFilterConfig() {
		return knowledgeBaseFilterConfig;
	}

	public EnumerationSourceConfigSet getEnumerationSourceConfigSet() {
		return enumerationSourceConfigSet;
	}

	public LDAPConnectionConfig getLDAPConnectionConfig() {
		return ldapConfig;
	}

	public LDAPConnectionConfig getLdapConfig() {
		return ldapConfig;
	}

	public String getServerBasePath() {
		return serverBasePath;
	}

	public void setServerBasePath(String serverBasePath) {
		if (serverBasePath == null) throw new NullPointerException("serverBasePath cannot be null");
		if (this.serverBasePath == null) {
			this.serverBasePath = serverBasePath;
		}
	}

	public final String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @return the feature configuration
	 * @since PowerEditor 4.0.1
	 * @see FeatureConfiguration
	 */
	public FeatureConfiguration getFeatureConfiguration() {
		return featureConfiguration;
	}

	/**
	 * 
	 * @return the server configuration
	 * @see ServerConfiguration
	 */
	public ServerConfiguration getServerConfiguration() {
		return serverConfig;
	}

	public RuleGenerationConfiguration getRuleGenerationConfigurationDefault() {
		return ruleConfigList.getRuleGenerationConfiguration(null);
	}

	public RuleGenerationConfiguration getRuleGenerationConfiguration(TemplateUsageType usageType) {
		return ruleConfigList.getRuleGenerationConfiguration(usageType);
	}

	public UserPasswordPoliciesConfig getUserPasswordPoliciesConfig() {
		return userPasswordPoliciesConfig;
	}

	public Map<Object, MessageConfiguration> getCondMsgDelims() {
		return ruleConfigList.getCondMsgDelims();
	}

	public UIConfiguration getUIConfiguration() {
		return uiConfig;
	}

	public UIConfiguration getUiConfig() {
		return uiConfig;
	}

	public EntityConfiguration getEntityConfiguration() {
		return entityConfig;
	}

	public String[] getDomainDefinitionFiles() {
		return domainDefinitionFileList.toArray(new String[0]);
	}

	public String[] getTemplateDefinitionFiles() {
		return templateDefinitionFileList.toArray(new String[0]);
	}

	public String[] getCBRDefinitionFiles() {
		return cbrDefinitionFileList.toArray(new String[0]);
	}

	public void addDomainDefinitionFile(String file) {
		if (file != null) {
			domainDefinitionFileList.add(file);
		}
	}

	public void addTemplateDefinitionFile(String file) {
		if (file != null) {
			templateDefinitionFileList.add(file);
		}
	}

	public void addCBRDefinitionFile(String file) {
		if (file != null) {
			cbrDefinitionFileList.add(file);
		}
	}

	private FileReader openFile(String filename) {
		return openFile(filename, "Power Editor configuration file");
	}

	private FileReader openFile(String filename, String description) {
		try {
			return new FileReader(filename);
		}
		catch (FileNotFoundException e) {
			throw new ConfigurationException("Cannot read " + description + ": " + filename, e);
		}
	}

	public synchronized void validateWithDomainLoaded(DomainClassProvider domainClassProvider) {
		validateRuleGenerationConfigsWithDomain(domainClassProvider);
	}

	private void validateRuleGenerationConfigsWithDomain(DomainClassProvider domainClassProvider) {
		// Validate default rule generation config first
		RuleGenerationConfiguration defaultConfig = getRuleGenerationConfigurationDefault();
		defaultConfig.validateConfiguration(domainClassProvider);
		TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
		for (int i = 0; i < usageTypes.length; i++) {
			RuleGenerationConfiguration config = getRuleGenerationConfiguration(usageTypes[i]);
			if (config != defaultConfig) {
				config.validateConfiguration(domainClassProvider);
			}
		}
	}
}
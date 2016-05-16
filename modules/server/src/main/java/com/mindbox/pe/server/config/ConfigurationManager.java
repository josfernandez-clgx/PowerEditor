package com.mindbox.pe.server.config;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logFatal;
import static com.mindbox.pe.common.UtilBase.asBoolean;
import static com.mindbox.pe.common.UtilBase.isEmpty;
import static com.mindbox.pe.common.UtilBase.isEmptyAfterTrim;
import static com.mindbox.pe.common.UtilBase.isMember;
import static com.mindbox.pe.common.UtilBase.isTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.EntityConfigHelper;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.config.UserManagementConfig;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.server.db.PeDbUserAuthenticationProvider;
import com.mindbox.pe.server.ldap.DefaultUserAuthenticationProvider;
import com.mindbox.pe.server.spi.UserAuthenticationProvider;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.config.GuidelineTab;
import com.mindbox.pe.xsd.config.KnowledgeBaseFilter;
import com.mindbox.pe.xsd.config.LDAPConfig;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.ObjectGenerationDefault;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.RuleGenerationDefault;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.RuleGenerationOverride;
import com.mindbox.pe.xsd.config.ServerConfig;
import com.mindbox.pe.xsd.config.UserInterfaceConfig.UsageTypeList.UsageType;
import com.mindbox.pe.xsd.config.UserPasswordPolicies;

/**
 * PE configuration manager.
 * @author Gene Kim
 * @author MindBox, Inc
 */
public final class ConfigurationManager {

	public static final String AUTH_NONE = "none";
	public static final String AUTH_SIMPLE = "simple";
	private static final int DEFAULT_MAX_AUTH_ATTEMPTS = 5;
	private static final int DEFAULT_MAX_USER_SESSIONS = 10;
	private static final String DEFAULT_LOGOUT_URL = "/logout.jsp";
	private static final Logger LOG = Logger.getLogger(ConfigurationManager.class);
	private static ConfigurationManager instance = null;

	private static String asValidLdapAuthenticationScheme(String str) {
		if (str == null || str.trim().length() == 0) return null;
		if (str.equalsIgnoreCase("anonymous")) return AUTH_NONE;
		if (str.equalsIgnoreCase("simple")) return AUTH_SIMPLE;
		if (str.equalsIgnoreCase("cram-md5")) return "CRAM-MD5";
		if (str.equalsIgnoreCase("digest-md5")) return "DIGEST-MD5";
		if (str.equalsIgnoreCase("md5")) return "DIGEST-MD5";
		if (str.equalsIgnoreCase("EXTERNAL")) return "EXTERNAL";
		if (str.equalsIgnoreCase("GSSAPI")) return "GSSAPI";
		return null;
	}

	public static ConfigurationManager getInstance() {
		if (instance == null) throw new IllegalStateException("Initialize ConfigurationManager first");
		return instance;
	}

	public static void initialize(final String version, final String build, final PowerEditorConfiguration powerEditorConfiguration, final String filename) {
		try {
			instance = new ConfigurationManager(version, build, powerEditorConfiguration, filename);
		}
		catch (Exception e) {
			logFatal(LOG, e, "Failed to initialize PowerEditor from %s", powerEditorConfiguration);
			throw new IllegalStateException(e);
		}
	}

	private final PowerEditorConfiguration powerEditorConfiguration;
	private final String version;
	private final String build;
	private String serverBasePath = null;
	private String logoutUrlToUse;
	private final DateFilterConfigHelper dateFilterConfigHelper;
	private final EnumerationSourceConfigHelper enumerationSourceConfigHelper;
	private final EntityConfigHelper entityConfigHelper;
	private final RuleGenerationConfigHelper defaultRuleGenerationConfigHelper;
	private final Map<TemplateUsageType, RuleGenerationConfigHelper> overrideRuleGenerationConfigHelperMap = new HashMap<TemplateUsageType, RuleGenerationConfigHelper>();
	private final ParameterContextConfigHelper parameterContextConfigHelper;
	private final ServerConfigHelper serverConfigHelper;
	private final UserManagementConfig userManagementConfig;
	private final UserPasswordPoliciesConfigHelper userPasswordPoliciesConfigHelper;
	private final String filename;
	private UserAuthenticationProvider userAuthenticationProviderClass = null;

	private ConfigurationManager(final String version, final String build, final PowerEditorConfiguration powerEditorConfiguration, final String filename)
			throws JAXBException, IOException, SQLException {
		if (version == null) {
			throw new ConfigurationException("version cannot be null");
		}
		if (powerEditorConfiguration.getServer() == null) {
			throw new ConfigurationException("PE config file does not contain <Server> tag");
		}
		if (powerEditorConfiguration.getServer().getSession() == null) {
			throw new ConfigurationException("PE config file does not contain <Server>/<Session> tag");
		}
		if (powerEditorConfiguration.getRuleGeneration() == null) {
			throw new ConfigurationException("PE config file does not contain <RuleGeneration> tag");
		}
		if (powerEditorConfiguration.getUserInterface() == null) {
			throw new ConfigurationException("PE config file does not contain <UserInterface> tag");
		}
		if (powerEditorConfiguration.getUserInterface().getEntity() == null || powerEditorConfiguration.getUserInterface().getEntity().getEntityTab().isEmpty()) {
			throw new IllegalStateException("UserInterface element has no <Entity> tag or <Entity> tag has no <EntityTab> tags.");
		}
		if (powerEditorConfiguration.getUserInterface().getUsageTypeList() == null || powerEditorConfiguration.getUserInterface().getUsageTypeList().getUsageType().isEmpty()) {
			throw new IllegalStateException("UserInterface element has no <UsageTypeList> tag or it has no <UsageType> tags.");
		}
		if (powerEditorConfiguration.getUserInterface().getGuideline() == null || powerEditorConfiguration.getUserInterface().getGuideline().getGuidelineTab().isEmpty()) {
			throw new IllegalStateException("<Guideline> tag contains no <GuidelineTab> tags");
		}
		if (powerEditorConfiguration.getServer().getKnowledgeBase().getDomainFile() == null || powerEditorConfiguration.getServer().getKnowledgeBase().getDomainFile().isEmpty()) {
			throw new ConfigurationException("No domain definition file is specified in the configuration file");
		}
		this.version = version;
		this.build = build;
		this.filename = filename;
		this.powerEditorConfiguration = powerEditorConfiguration;

		final ServerConfig serverConfig = powerEditorConfiguration.getServer();

		// Create template usage types

		// Load KnowledgeBase filters
		logDebug(LOG, "loading Knowledgedbase filter...");
		final KnowledgeBaseFilter knowledgeBaseFilter = powerEditorConfiguration.getKnowledgeBaseFilter();
		if (knowledgeBaseFilter != null && knowledgeBaseFilter.getDateFilter() != null) {
			dateFilterConfigHelper = new DateFilterConfigHelper(knowledgeBaseFilter.getDateFilter());
			logDebug(LOG, "DateFilterConfig = [from=%s,to=%s]", knowledgeBaseFilter.getDateFilter().getBeginDate(), knowledgeBaseFilter.getDateFilter().getEndDate());
		}
		else {
			dateFilterConfigHelper = null;
		}

		// Load KB file configs
		logDebug(LOG, "loading KB configuration...");
		for (final String domainFileName : serverConfig.getKnowledgeBase().getDomainFile()) {
			checkFileExists(domainFileName, "domain definition file");
		}

		// load parameter template files, if feature is enabled
		if (isFeatureEnabled(FeatureNameType.PARAMETER)) {
			for (final String filePath : serverConfig.getKnowledgeBase().getTemplateFile()) {
				checkFileExists(filePath, "template definition file");
			}
		}

		logDebug(LOG, "Generating template usage types....");
		for (final UsageType usageType : powerEditorConfiguration.getUserInterface().getUsageTypeList().getUsageType()) {
			TemplateUsageType.createInstance(
					usageType.getName(),
					(usageType.getDisplayName() == null ? usageType.getName() : usageType.getDisplayName()),
					usageType.getPrivilege());
		}

		// Load Entity confurations
		logDebug(LOG, "Generating generic entity types...");
		for (final EntityType entityType : powerEditorConfiguration.getEntityConfig().getEntityType()) {
			if (!GenericEntityType.hasTypeFor(entityType.getTypeID().intValue())) {
				GenericEntityType.makeInstance(entityType);
			}
		}

		entityConfigHelper = new EntityConfigHelper(powerEditorConfiguration.getEntityConfig());

		logDebug(LOG, "loading session configuration...");
		resetUserAuthenticationProviderClass(serverConfig.getSession().getUserAuthenticationProviderClass(), serverConfig.getDatabase().getUserManagementProviderClass());
		if (powerEditorConfiguration.getServer().getSession().getMaxUserSessions() == null
				|| powerEditorConfiguration.getServer().getSession().getMaxUserSessions().intValue() < 1) {
			powerEditorConfiguration.getServer().getSession().setMaxUserSessions(DEFAULT_MAX_USER_SESSIONS);
		}
		if (powerEditorConfiguration.getServer().getUserPasswordPolicies() == null) {
			powerEditorConfiguration.getServer().setUserPasswordPolicies(new UserPasswordPolicies());
		}
		if (powerEditorConfiguration.getServer().getUserPasswordPolicies().getLockout() == null) {
			powerEditorConfiguration.getServer().getUserPasswordPolicies().setLockout(new UserPasswordPolicies.Lockout());
		}
		if (powerEditorConfiguration.getServer().getUserPasswordPolicies().getLockout().getMaxAttempts() == null
				|| powerEditorConfiguration.getServer().getUserPasswordPolicies().getLockout().getMaxAttempts().intValue() < 1) {
			powerEditorConfiguration.getServer().getUserPasswordPolicies().getLockout().setMaxAttempts(DEFAULT_MAX_AUTH_ATTEMPTS);
		}

		// populate user management config
		userManagementConfig = new UserManagementConfig();
		if (powerEditorConfiguration.getUserInterface().isHideCopyUserButon() != null && isTrue(powerEditorConfiguration.getUserInterface().isHideCopyUserButon())) {
			userManagementConfig.setHideCopyButton(true);
		}

		validateAndSetDefaultsLdapConfig();

		// set rule generation configuration
		final String ruleNamePrefix = (powerEditorConfiguration.getRuleGeneration().getRuleGenerationDefault().getRuleNamePrefix() == null
				? null
				: powerEditorConfiguration.getRuleGeneration().getRuleGenerationDefault().getRuleNamePrefix().getGuideline());
		defaultRuleGenerationConfigHelper = new RuleGenerationConfigHelper(powerEditorConfiguration.getRuleGeneration().getRuleGenerationDefault(), ruleNamePrefix, null);

		for (final RuleGenerationOverride ruleGenerationOverride : powerEditorConfiguration.getRuleGeneration().getRuleGenerationOverride()) {
			final TemplateUsageType usageType = TemplateUsageType.valueOf(ruleGenerationOverride.getUsageType());
			overrideRuleGenerationConfigHelperMap.put(
					usageType,
					RuleGenerationConfigHelper.newOverride(defaultRuleGenerationConfigHelper, usageType, null, ruleGenerationOverride));
		}

		enumerationSourceConfigHelper = new EnumerationSourceConfigHelper(serverConfig.getEnumerationSources());

		serverConfigHelper = new ServerConfigHelper(serverConfig);

		parameterContextConfigHelper = new ParameterContextConfigHelper(
				powerEditorConfiguration.getRuleGeneration().getObjectGenerationDefault() == null
						? null
						: powerEditorConfiguration.getRuleGeneration().getObjectGenerationDefault().getParameterContext());

		userPasswordPoliciesConfigHelper = (powerEditorConfiguration.getServer().getUserPasswordPolicies() == null
				? null
				: new UserPasswordPoliciesConfigHelper(powerEditorConfiguration.getServer().getUserPasswordPolicies()));

		logDebug(LOG, "Configuration loaded successfully");
	}

	private void checkFileExists(String filename, String description) {
		if (!new File(filename).exists()) {
			throw new ConfigurationException("Cannot read " + description + ": " + filename);
		}
	}

	public GuidelineTab findConfigurationForRuleSet(final TemplateUsageType templateUsageType) {
		for (final GuidelineTab guidelineTab : powerEditorConfiguration.getUserInterface().getGuideline().getGuidelineTab()) {
			for (final com.mindbox.pe.xsd.config.GuidelineTab.UsageType tabUsageType : guidelineTab.getUsageType()) {
				if (templateUsageType.toString().equals(tabUsageType.getName())) {
					return guidelineTab;
				}
			}
		}
		return null;
	}

	public String getAppBuild() {
		return build;
	}

	public String getAppVersion() {
		return version;
	}

	public Map<Object, MessageConfiguration> getCondMsgDelims() {
		final Map<Object, MessageConfiguration> condMsgDelims = new HashMap<Object, MessageConfiguration>();
		for (final TemplateUsageType templateUsageType : overrideRuleGenerationConfigHelperMap.keySet()) {
			condMsgDelims.put(templateUsageType, getRuleGenerationConfigHelper(templateUsageType).getMessageConfig());
		}
		return condMsgDelims;
	}

	public DateFilterConfigHelper getDateFilterConfigHelper() {
		return dateFilterConfigHelper;
	}

	public RuleGenerationConfigHelper getDefaultRuleGenerationConfigHelper() {
		return defaultRuleGenerationConfigHelper;
	}


	public EntityConfigHelper getEntityConfigHelper() {
		return entityConfigHelper;
	}

	public EntityType getEntityTypeForMessageContext() {
		return entityConfigHelper.getEntityTypeForMessageContext();
	}

	public EnumerationSourceConfigHelper getEnumerationSourceConfigHelper() {
		return enumerationSourceConfigHelper;
	}

	public String getFilename() {
		return filename;
	}

	public LDAPConfig getLdapConfig() {
		return powerEditorConfiguration.getServer().getLDAP();
	}

	public String getLogoutUrlToUse() {
		return logoutUrlToUse;
	}

	public ObjectGenerationDefault getObjectGenerationDefault() {
		return powerEditorConfiguration.getRuleGeneration().getObjectGenerationDefault();
	}

	public ParameterContextConfigHelper getParameterContextConfigHelper() {
		return parameterContextConfigHelper;
	}

	public PowerEditorConfiguration getPowerEditorConfiguration() {
		return powerEditorConfiguration;
	}

	public RuleGenerationConfigHelper getRuleGenerationConfigHelper(final TemplateUsageType usageType) {
		return overrideRuleGenerationConfigHelperMap.containsKey(usageType) ? overrideRuleGenerationConfigHelperMap.get(usageType) : getDefaultRuleGenerationConfigHelper();
	}

	public String getServerBasePath() {
		return serverBasePath;
	}

	public ServerConfigHelper getServerConfigHelper() {
		return serverConfigHelper;
	}

	public UserAuthenticationProvider getUserAuthenticationProvider() {
		return userAuthenticationProviderClass;
	}

	public UserManagementConfig getUserManagementConfig() {
		return userManagementConfig;
	}

	public UserPasswordPoliciesConfigHelper getUserPasswordPoliciesConfigHelper() {
		return userPasswordPoliciesConfigHelper;
	}

	public boolean isFeatureEnabled(final FeatureNameType featureNameType) {
		return ConfigUtil.isFeatureEnabled(powerEditorConfiguration, featureNameType);
	}

	public boolean isLdapConfigured() {
		return powerEditorConfiguration.getServer().getLDAP() != null;
	}

	public void resetLogoutUrlToUse(HttpServletRequest request) {
		if (logoutUrlToUse == null) {
			String urlToUse = powerEditorConfiguration.getServer().getSession().getLogoutUrl();
			if (isEmptyAfterTrim(urlToUse)) {
				if (!isEmptyAfterTrim(powerEditorConfiguration.getServer().getSession().getLogoutHttpHeader())) {
					String headerStr = request.getHeader(powerEditorConfiguration.getServer().getSession().getLogoutHttpHeader());
					if (headerStr != null) {
						String[] headerStrs = headerStr.split(";");
						if (headerStrs.length > 0) {
							urlToUse = headerStrs[0];
						}
					}
				}
			}

			this.logoutUrlToUse = (isEmptyAfterTrim(urlToUse) ? request.getContextPath() + DEFAULT_LOGOUT_URL : urlToUse);
		}
	}

	private void resetUserAuthenticationProviderClass(final String userAuthenticationProviderClassName, final String userManagementProviderClassname) {
		try {
			if (!isEmpty(userAuthenticationProviderClassName)) {
				this.userAuthenticationProviderClass = UserAuthenticationProvider.class.cast(Class.forName(userAuthenticationProviderClassName).newInstance());
			}
			else if (userManagementProviderClassname != null && userManagementProviderClassname.endsWith("LDAPUserManagementProvider")) {
				this.userAuthenticationProviderClass = new DefaultUserAuthenticationProvider();
			}
			else {
				this.userAuthenticationProviderClass = new PeDbUserAuthenticationProvider();
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Could not initialize UserAuthenticationProvider plugin in class path: " + userAuthenticationProviderClassName, e);
		}
	}

	public void setServerBasePath(String serverBasePath) {
		if (serverBasePath == null) {
			throw new NullPointerException("serverBasePath cannot be null");
		}
		if (this.serverBasePath == null) {
			this.serverBasePath = serverBasePath;
		}
	}

	private void validateAndSetDefaultsLdapConfig() {
		if (!isLdapConfigured()) {
			return;
		}

		final LDAPConfig ldapConfig = powerEditorConfiguration.getServer().getLDAP();

		if (isEmpty(ldapConfig.getConnection())) {
			throw new IllegalArgumentException("<Server><LDAP><Connection> is not specified in PowerEditorConfiguration.xml");
		}

		ldapConfig.setAuthenticationScheme(asValidLdapAuthenticationScheme(ldapConfig.getAuthenticationScheme()));
		if (ldapConfig.getAuthenticationScheme() == null) {
			throw new IllegalArgumentException("<Server><LDAP><AuthenticationScheme> is not specified or contains an invalid value in PowerEditorConfiguration.xml");
		}

		final boolean anonymous = ldapConfig.getAuthenticationScheme().equals(AUTH_NONE);
		if (!anonymous && isEmpty(ldapConfig.getPrincipal()))
			throw new IllegalArgumentException("<Server><LDAP><Principal> is required in PowerEditorConfiguration.xml for non-anonyous connection");
		if (!anonymous && (ldapConfig.getCredentials() == null || isEmpty(ldapConfig.getCredentials()))) {
			throw new IllegalArgumentException("<Server><LDAP><Credentials> is required in PowerEditorConfiguration.xml for non-anonyous connection");
		}
		if (isEmpty(ldapConfig.getUserIDAttribute())) {
			throw new IllegalArgumentException("<Server><LDAP><UserIDAttribute> is not specified in PowerEditorConfiguration.xml");
		}

		userManagementConfig.setReadOnly(true);
		userManagementConfig.setRoleChangeable(true);
		userManagementConfig.setPasswordChangeable(false);
		userManagementConfig.setAllowDelete(false);

		if (isEmpty(ldapConfig.getUserNameAttribute())) {
			userManagementConfig.setNameChangeable(false);
			ldapConfig.setUserNameAttribute("cn");
			LOG.warn("<Server><LDAP><UserNameAttribute> is not specified in PowerEditorConfiguration.xml>. Set to " + ldapConfig.getUserNameAttribute());
		}
		else {
			userManagementConfig.setNameChangeable(true);
		}

		if (isEmpty(ldapConfig.getUserPasswordAttribute())) {
			ldapConfig.setUserPasswordAttribute("userPassword");
			LOG.warn("<Server><LDAP><UserPasswordAttribute> is not specified in PowerEditorConfiguration.xml. Set to " + ldapConfig.getUserPasswordAttribute());
		}

		if (isEmpty(ldapConfig.getUserRolesAttribute())) {
			// if no role attribute is specified, MB_USER_ROLE table is used
			LOG.warn("<Server><LDAP><UserRolesAttribute> is not specified in PowerEditorConfiguration.xml");
			userManagementConfig.setRoleChangeable(!userManagementConfig.isReadOnly());
		}
		else {
			userManagementConfig.setRoleChangeable(true);
		}

		if (isEmpty(ldapConfig.getUserStatusAttribute())) {
			userManagementConfig.setStatusChangeable(false);
			LOG.warn("<Server><LDAP><UserPasswordAttribute> is not specified in PowerEditorConfiguration.xml: all users will be active status!");
		}
		else {
			userManagementConfig.setStatusChangeable(!userManagementConfig.isReadOnly());
		}

		if (ldapConfig.getUserDirectoryDN().isEmpty()) {
			LOG.warn("No <Server><LDAP><UseDirectoryDN> is found. Will use initial directory.");
		}

		if (isEmpty(ldapConfig.getUserObjectClass())) {
			ldapConfig.setUserObjectClass("person");
			LOG.warn("<Server><LDAP><UserObjectClass> is not specified in PowerEditorConfiguration.xml. Set to " + ldapConfig.getUserObjectClass());
		}

		LOG.info("Number of user directory DNs = " + ldapConfig.getUserDirectoryDN().size());
	}

	private void validateControlPattern(final RuleGenerationLHS.Pattern pattern, final TemplateUsageType templateUsageType, final DomainClassProvider domainClassProvider) {
		if (!asBoolean(pattern.isGenerate(), true)) {
			return;
		}

		final Map<String, String> attributes = new HashMap<String, String>();
		for (final RuleGenerationLHS.Pattern.Attribute attribute : pattern.getAttribute()) {
			attributes.put(attribute.getType(), attribute.getName());
		}
		final String[] disallowedEntities = (pattern.getDisallowedEntities() == null ? new String[0] : pattern.getDisallowedEntities().split(","));
		final String usageTypeStr = (templateUsageType == null ? "(default)" : templateUsageType.toString());

		// TT 1946 Validate that all required attributes are specified
		for (GenericEntityType genericEntityType : GenericEntityType.getAllGenericEntityTypes()) {
			if (genericEntityType.isUsedInContext() && !isMember(genericEntityType.getName(), disallowedEntities)) {
				// check attribute element is speicified
				if (!attributes.containsKey(genericEntityType.getName())) {
					throw new ConfigurationException("<Attribute> element of type '" + genericEntityType + "' must be specified for control pattern of " + usageTypeStr);
				}
				// check attribute element contains a valid attribute name
				DomainClass dc = domainClassProvider.getDomainClass(pattern.getClazz());
				if (dc == null) {
					throw new ConfigurationException(
							"<Pattern type='control'> for " + usageTypeStr + " contains invalid value; No domain class named '" + pattern.getClazz() + "' found");
				}
				DomainAttribute da = dc.getDomainAttribute(attributes.get(genericEntityType.getName()));
				if (da == null) {
					throw new ConfigurationException(
							"<Attribute> element of type '" + genericEntityType + " for " + usageTypeStr + "  contains invalid value; Domain attribute of name "
									+ attributes.get(genericEntityType.getName()) + " not found for " + dc);
				}
			}
		}

	}

	private void validateRuleGenerationConfigsWithDomain(final DomainClassProvider domainClassProvider) {
		// Validate default rule generation config first
		final RuleGenerationDefault ruleGenerationDefault = powerEditorConfiguration.getRuleGeneration().getRuleGenerationDefault();
		validateRuleGnerationLHS(ruleGenerationDefault.getLHS(), null, domainClassProvider);

		for (final RuleGenerationOverride ruleGenerationOverride : powerEditorConfiguration.getRuleGeneration().getRuleGenerationOverride()) {
			validateRuleGnerationLHS(ruleGenerationDefault.getLHS(), TemplateUsageType.valueOf(ruleGenerationOverride.getUsageType()), domainClassProvider);
		}
	}

	private void validateRuleGnerationLHS(final RuleGenerationLHS ruleGenerationLHS, final TemplateUsageType templateUsageType, final DomainClassProvider domainClassProvider) {
		for (final RuleGenerationLHS.Pattern pattern : ruleGenerationLHS.getPattern()) {
			if (pattern.getType() == LHSPatternType.CONTROL) {
				validateControlPattern(pattern, templateUsageType, domainClassProvider);
			}
		}
	}

	public synchronized void validateWithDomainLoaded(final DomainClassProvider domainClassProvider) {
		validateRuleGenerationConfigsWithDomain(domainClassProvider);
	}
}
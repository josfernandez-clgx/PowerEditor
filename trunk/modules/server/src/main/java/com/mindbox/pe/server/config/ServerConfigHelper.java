package com.mindbox.pe.server.config;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Password;
import com.mindbox.pe.server.db.DefaultPEDBCProvider;
import com.mindbox.pe.server.spi.PEDBCProvider;
import com.mindbox.pe.xsd.config.ServerConfig;
import com.mindbox.pe.xsd.config.ServerConfig.Deployment;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
public final class ServerConfigHelper {

	public static final class DBConfig {

		private final String driver;
		private final String connection;
		private final String user;
		private final Password pwd;
		private final int maxConnections;
		private final Class<?> providerClass;
		private final String guidelineRuleProviderClass;
		private final String userManagementProviderClass;
		private final int monitorIntervalInSeconds;
		private final String validationQuery;

		private DBConfig(Class<?> providerClass, String driver, String conn, String user, Password pwd, int maxConn, String guidelineRuleProviderClass, String userManagementProviderClass,
				int monitorIntervalInSeconds, String validationQuery) {
			this.providerClass = providerClass;
			this.driver = driver;
			this.connection = conn;
			this.user = user;
			this.pwd = pwd;
			this.maxConnections = maxConn;
			this.guidelineRuleProviderClass = guidelineRuleProviderClass;
			this.userManagementProviderClass = userManagementProviderClass;
			this.monitorIntervalInSeconds = (monitorIntervalInSeconds < 1 ? 120 : monitorIntervalInSeconds); // defaults to 2 minutes
			this.validationQuery = validationQuery;
		}

		public String getConnectionStr() {
			return connection;
		}

		public String getDriver() {
			return driver;
		}

		public String getGuidelineRuleProviderClassName() {
			return guidelineRuleProviderClass;
		}

		public int getMaxConnections() {
			return maxConnections;
		}

		public int getMonitorIntervalInSeconds() {
			return monitorIntervalInSeconds;
		}

		public String getPassword() {
			return pwd.getClearText();
		}

		public Class<?> getProviderClass() {
			return providerClass;
		}

		public String getUser() {
			return user;
		}

		public String getUserManagementProviderClassName() {
			return userManagementProviderClass;
		}

		public String getValidationQuery() {
			return validationQuery;
		}
	}

	private final ServerConfig serverConfig;
	private final DBConfig dbConfig;
	private final boolean auditAll;

	public ServerConfigHelper(final ServerConfig serverConfig) {
		// init DB configs -- configured using ConfigXMLDigester
		if (serverConfig.getDatabase() == null) {
			throw new IllegalArgumentException("<Server> tag must contain <Database> tag.");
		}
		// init log configs
		if (serverConfig.getLog() == null) {
			throw new IllegalArgumentException("<Server> tag does not contain <Log> tag.");
		}
		this.serverConfig = serverConfig;
		this.auditAll = (serverConfig.getAudit() == null ? true : UtilBase.asBoolean(serverConfig.getAudit().isAuditAll(), true));

		final Password dbPassword = Password.fromEncryptedString(serverConfig.getDatabase().getPassword());

		int maxConn = 5;
		if (serverConfig.getDatabase().getMaxConnection() != null) {
			maxConn = serverConfig.getDatabase().getMaxConnection().intValue();
		}

		final String ruleProviderClassname = serverConfig.getDatabase().getGuidelineRuleProviderClass();

		final String userManagementProviderClassname = serverConfig.getDatabase().getUserManagementProviderClass();

		Class<?> providerClass = null;
		if (!UtilBase.isEmpty(serverConfig.getDatabase().getProvider())) {
			try {
				providerClass = Class.forName(serverConfig.getDatabase().getProvider());
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("<Server><Database><Provider> class not found: " + serverConfig.getDatabase().getProvider());
			}

			if (!(PEDBCProvider.class.isAssignableFrom(providerClass))) {
				throw new IllegalArgumentException("The <Database><Provider> class " + providerClass.getName() + " is invalid. It must be implement PEDBCProvider interface.");
			}
		}
		else {
			providerClass = DefaultPEDBCProvider.class;
		}

		int monitorInterval = -1;
		if (serverConfig.getDatabase().getMonitorInterval() != null) {
			monitorInterval = serverConfig.getDatabase().getMonitorInterval().intValue();
		}

		String validationQuery = serverConfig.getDatabase().getValidationQuery();
		this.dbConfig = new DBConfig(
				providerClass,
				serverConfig.getDatabase().getDriver(),
				serverConfig.getDatabase().getConnection(),
				serverConfig.getDatabase().getUser(),
				dbPassword,
				maxConn,
				ruleProviderClassname,
				userManagementProviderClassname,
				monitorInterval,
				validationQuery);
	}

	public boolean auditAll() {
		return auditAll;
	}

	public DBConfig getDatabaseConfig() {
		return dbConfig;
	}

	public Deployment getDeploymentConfig() {
		return serverConfig.getDeployment();
	}
}

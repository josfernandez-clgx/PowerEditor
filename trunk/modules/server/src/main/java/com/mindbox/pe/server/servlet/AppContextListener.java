package com.mindbox.pe.server.servlet;

import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logFatal;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.ServerControl;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.PrivilegeConfig;
import com.mindbox.pe.server.db.loaders.TypeEnumValueLoader;
import com.mindbox.pe.server.migration.DataMigrator;
import com.mindbox.pe.server.migration.DataMigratorFactory;
import com.mindbox.pe.xsd.config.LogFileConfig;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.ServerConfig;

public class AppContextListener implements ServletContextListener {

	private static final String CONFIG_VARIABLE_NAME = "PEConfigFile";
	private static final String CONFIG_INIT_PARAM = "ConfigurationFile";
	private static final int LOG_MAX_BACKUP_INDEX = 20;

	private static Date serverStartDate = null;

	private static String determineConfigFilePath(final ServletContext servletContext) {
		// try environment variable first
		String filePath = System.getenv(CONFIG_VARIABLE_NAME);
		if (UtilBase.isEmpty(filePath)) {
			// try System property
			filePath = System.getProperty(CONFIG_VARIABLE_NAME);
			if (UtilBase.isEmpty(filePath)) {
				filePath = servletContext.getInitParameter(CONFIG_INIT_PARAM);
			}
		}
		return filePath;
	}

	static Date getServerStartDate() {
		return serverStartDate;
	}

	private final Logger logger = Logger.getLogger(AppContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		echoAll("-----------------------------------------------------------------");
		echoAll("[PE:destroy] DESTROY Request received @ " + getTimeDate());
		echoAll("-----------------------------------------------------------------");

		echoAll("[PE:destroy] clearing cache...");

		SecurityCacheManager.getInstance().stopUserMonitorWork();

		Loader.clearCache();

		DeploymentManager.deinitialize();

		AuditLogger.getInstance().logServerShutdown();
		ServerControl.setStatusToStopped();

		echoAll("[PE:destroy] DONE");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		long startTime = System.currentTimeMillis();

		final String version = servletContextEvent.getServletContext().getInitParameter("PEVersion");
		final String build = servletContextEvent.getServletContext().getInitParameter("PEBuild");

		echoAll("-----------------------------------------------------------------");
		echoAll(String.format("[PowerEditor] Version: %s. Starting up @ ", version, getTimeDate()));
		echoAll("-----------------------------------------------------------------");

		final String configFile = determineConfigFilePath(servletContextEvent.getServletContext());
		if (UtilBase.isEmpty(configFile)) {
			throw new IllegalStateException(String.format(
					"*** Configuration file not specified. Set %s environment variable or System property or provide %s context init parameter. Initialziation aborted!!!",
					CONFIG_VARIABLE_NAME,
					CONFIG_INIT_PARAM));
		}

		final Logger commonsLogger = Logger.getLogger("org.apache.commons");
		commonsLogger.removeAllAppenders();
		commonsLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c{2} %-5p: %m%n")));
		commonsLogger.setLevel(Level.WARN);

		try {
			final PowerEditorConfiguration powerEditorConfiguration = XmlUtil.unmarshal(new FileReader(configFile), PowerEditorConfiguration.class);
			ConfigurationManager.initialize(version, build, powerEditorConfiguration, new File(configFile).getAbsolutePath());
			echoAll("Configuration initialization complete");
		}
		catch (Exception e) {
			logFatal(logger, e, "Failed to initialize PE from %s", configFile);
			throw new IllegalStateException(e);
		}

		// persist base server path
		ConfigurationManager.getInstance().setServerBasePath(servletContextEvent.getServletContext().getRealPath(""));

		long configInitTime = System.currentTimeMillis() - startTime;
		startTime = System.currentTimeMillis();

		echoAll("Initializing Logs...");
		initializeLogs(ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer());

		logInfo(logger, "PE: config initialization time = %s", configInitTime);
		echoAll("*** PowerEditor Configuration File = " + configFile);

		echoAll(String.format("PowerEditor Version: %s (%s)", version, build));
		echoAll(String.format("Java Version: %s by %s at %s", System.getProperty("java.version"), System.getProperty("java.vendor"), System.getProperty("java.home")));

		echoAll("* Servlet log initialized");

		logInfo(logger, "PE: log initialization time = %s", (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();
		try {
			// initialize resource
			ResourceUtil.initialize(ConfigurationManager.getInstance().getServerBasePath(), new String[] { "resource.LabelsBundle", "resource.MessagesBundle", "resource.ValidationMessages" });
		}
		catch (IOException ex) {
			ex.printStackTrace(System.err);
			logger.fatal("Failed to load resource files", ex);
			throw new IllegalStateException(ex);
		}

		logInfo(logger, "PE: resource initialization time = %s", (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();

		// load type enum values first
		try {
			TypeEnumValueLoader.getInstance().load(ConfigurationManager.getInstance().getPowerEditorConfiguration().getKnowledgeBaseFilter());
			logInfo(logger, "Loaded Type Enum values");
		}
		catch (SQLException e) {
			logger.fatal("Failed to load Type Enum values", e);
			throw new IllegalStateException("Failed to load Type Enum values", e);
		}

		// update privileges in the database before calling cache
		logInfo(logger, "updating privileges in DB...");
		try {
			updatePrivilegesInDB();
			logInfo(logger, "successfully updated privileges in DB...");
		}
		catch (SQLException e) {
			logger.fatal("Error while updating privilege", e);
			throw new IllegalStateException("Error while updating privileges", e);
		}

		logInfo(logger, "loading cache...");
		try {
			Loader.loadToCache(true);
		}
		catch (ServerException e) {
			throw new IllegalStateException(e);
		}

		logInfo(logger, "PE cache initialization time = %s", (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();

		serverStartDate = new Date();

		DeploymentManager.getInstance();

		logInfo(logger, "initialized; running migration...");
		try {
			startTime = System.currentTimeMillis();

			final DataMigrator dataMigrator = DataMigratorFactory.getDataMigrator(version);
			if (dataMigrator != null) {
				dataMigrator.migrateData(version);
				logInfo(logger, "data migration time = %s", (System.currentTimeMillis() - startTime));
			}
			else {
				logInfo(logger, "No data migrator found for version %s; migration skipped", version);
			}
		}
		catch (SQLException e) {
			logger.fatal("Failed to migrate data", e);
			throw new IllegalStateException("Failed to migrate data", e);
		}

		SecurityCacheManager.getInstance().startUserMonitorWork();
		BizActionCoordinator.getInstance().setServerStartDate(serverStartDate);

		AuditLogger.getInstance().logServerStartup();
		ServerControl.setStatusToRunning();

		echoAll("PE initialization complete");
	}

	private void echoAll(String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}

	private String getTimeDate() {
		final Calendar calendar = Calendar.getInstance();
		return calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + " on " + (calendar.get(2) + 1) + "-" + calendar.get(5) + "-" + calendar.get(1);
	}

	private void initDBLog(final LogFileConfig logConfig) throws IOException {
		String logFilename = logConfig.getFile();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.db");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			if (logConfig.getMaxSize() != null) {
				fileAppender.setMaxFileSize(logConfig.getMaxSize().intValue() + "MB");
			}
			fileAppender.setMaxBackupIndex(LOG_MAX_BACKUP_INDEX);
			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel(Level.DEBUG);

			logInfo(logger, "*** INITIALIZED ***");
		}
		else {
			throw new IOException("DB log file not specified");
		}
	}

	private void initDeployerLog(final LogFileConfig logConfig) throws IOException {
		String logFilename = logConfig.getFile();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.generator");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			if (logConfig.getMaxSize() != null) {
				fileAppender.setMaxFileSize(logConfig.getMaxSize().intValue() + "MB");
			}
			fileAppender.setMaxBackupIndex(LOG_MAX_BACKUP_INDEX);
			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel(UtilBase.asBoolean(logConfig.isDebug(), false) ? Level.DEBUG : Level.INFO);

			logInfo(logger, "*** INITIALIZED ***");
		}
		else {
			throw new IOException("Deployer log file not specified");
		}
	}

	private void initializeLogs(final ServerConfig serverConfig) {
		// initialize Log4J
		BasicConfigurator.configure();

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.WARN);
		rootLogger.setAdditivity(false);

		rootLogger.info("*** INITIALIZED ***");

		try {
			for (LogFileConfig logConfig : serverConfig.getLog().getLogFile()) {
				final boolean isOn = UtilBase.asBoolean(logConfig.isWriteLog(), false);
				if (isOn) {
					switch (logConfig.getType()) {
					case SERVER:
						// initialize server / servlet log, if enabled
						initServerLog(logConfig);
						echoAll("Server log initialized!!!");
						break;
					case SERVLET:
						initServletLog(logConfig);
						echoAll("Servlet log initialized!!!");
						break;
					case DEPLOYER:
						initDeployerLog(logConfig);
						echoAll("*** Deployer log initialized!!!");
						break;
					case DATABASE:
						initDBLog(logConfig);
						echoAll("*** Database log initialized!!!");
						break;
					case LOADER:
						initLoaderLog(logConfig);
						echoAll("*** Loader log initialized!!!");
					}
				}
				else {
					echoAll(String.format("%s log is turned off", logConfig.getType()));
				}
			}
		}
		catch (IOException ex) {
			logError(logger, ex, "Failed to initialize log");
		}
	}

	private void initLoaderLog(final LogFileConfig logConfig) throws IOException {
		String logFilename = logConfig.getFile();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.db.loaders");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			if (logConfig.getMaxSize() != null) {
				fileAppender.setMaxFileSize(logConfig.getMaxSize().intValue() + "MB");
			}
			fileAppender.setMaxBackupIndex(LOG_MAX_BACKUP_INDEX);

			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel(UtilBase.asBoolean(logConfig.isDebug(), false) ? Level.DEBUG : Level.INFO);

			logInfo(logger, "*** INITIALIZED ***");
		}
		else {
			throw new IOException("Loader log file not specified");
		}
	}

	private void initServerLog(final LogFileConfig logConfig) throws IOException {
		String logFilename = logConfig.getFile();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server");

			final PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			if (logConfig.getMaxSize() != null) {
				fileAppender.setMaxFileSize(logConfig.getMaxSize().intValue() + "MB");
			}
			fileAppender.setMaxBackupIndex(LOG_MAX_BACKUP_INDEX);
			final Level level = (UtilBase.asBoolean(logConfig.isDebug(), false) ? Level.DEBUG : Level.INFO);

			initServerLogLogger(logger, false, fileAppender, level);
			initServerLogLogger(Logger.getLogger("com.mindbox.pe.wrapper"), false, fileAppender, level);
			initServerLogLogger(Logger.getLogger("com.mindbox.pe.common"), true, fileAppender, level);
			initServerLogLogger(Logger.getLogger("com.mindbox.pe.model"), true, fileAppender, level);

			// add 3rd party log entries here
			RollingFileAppender thirdPartyAppender = new RollingFileAppender(layout, ConfigurationManager.getInstance().getServerBasePath() + "/WEB-INF/pe-3rd-party.log", true);
			if (logConfig.getMaxSize() != null) {
				fileAppender.setMaxFileSize(logConfig.getMaxSize().intValue() + "MB");
			}
			fileAppender.setMaxBackupIndex(LOG_MAX_BACKUP_INDEX);

			initServerLogLogger(Logger.getLogger("com.crystaldecisions"), false, thirdPartyAppender, level);
		}
		else {
			throw new IOException("Server log file not specified");
		}
	}

	private void initServerLogLogger(Logger logger, boolean additivity, RollingFileAppender fileAppender, Level level) {
		logger.removeAllAppenders();
		logger.addAppender(fileAppender);
		logger.setAdditivity(additivity);
		logger.setLevel(level);
		logInfo(logger, "*** INITIALIZED ***");
	}

	private void initServletLog(final LogFileConfig logConfig) throws IOException {
		final Logger serverLogger = Logger.getLogger("com.mindbox.pe.server.servlet");
		serverLogger.setAdditivity(true);

		if (UtilBase.asBoolean(logConfig.isWriteLog(), false)) {
			serverLogger.setLevel(UtilBase.asBoolean(logConfig.isDebug(), false) ? Level.DEBUG : Level.INFO);
		}
		else {
			serverLogger.setLevel(Level.ERROR);
		}

		final Logger commonLogger = Logger.getLogger("com.mindbox.pe.common");
		commonLogger.setAdditivity(false);
		commonLogger.setLevel(this.logger.getLevel());

		final Logger modelLogger = Logger.getLogger("com.mindbox.pe.model");
		modelLogger.setAdditivity(false);
		modelLogger.setLevel(this.logger.getLevel());
	}


	/* This method gets entities from EntityType and UsageType from the config file
	 * and constructs privileges based on them. Those privileges are then updated in the database
	 * as needed.
	 * @throws SQLException
	 */
	private void updatePrivilegesInDB() throws SQLException {
		PrivilegeConfig privilegeConfig = new PrivilegeConfig();
		privilegeConfig.updatePrivilegesForInitialization(
				ConfigurationManager.getInstance().getEntityConfigHelper().getEntityTypeDefinitions(),
				TemplateUsageType.getAllInstances(),
				TypeEnumValueManager.getInstance().getAllEnumValues(TypeEnumValue.TYPE_STATUS));
	}

}

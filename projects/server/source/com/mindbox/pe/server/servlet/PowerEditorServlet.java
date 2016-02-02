package com.mindbox.pe.server.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SapphireComm;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.ServerControl;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.PrivilegeConfig;
import com.mindbox.pe.server.config.ServerConfiguration;
import com.mindbox.pe.server.db.loaders.TypeEnumValueLoader;
import com.mindbox.pe.server.repository.RepositoryManageFactory;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

/**
 * PowerEditor Servlet.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class PowerEditorServlet extends HttpServlet {

	private static final long serialVersionUID = -5642092409070959267L;

	private static final boolean INVALIDATE_UPON_NEW_SESSION = true;
	private static final String BYPASS_RESTART_CHECK = "pe.bypassRestartCheck";


	public void destroy() {
		super.destroy();

		echoAll("-----------------------------------------------------------------");
		echoAll("[PowerEditorServlet.destroy] DESTROY Request received @ " + getTimeDate());
		echoAll("-----------------------------------------------------------------");

		echoAll("[PowerEditorServlet.destroy] clearing cache...");

		Loader.clearCache();

		echoAll("[PowerEditorServlet.destroy] destorying repositories...");
		RepositoryManageFactory.getInstance().getAdHocRuleRepositoryManager().deinitialize();

		echoAll("[PowerEditorServlet.destroy] DONE");
		AuditLogger.getInstance().logServerShutdown();
		logger = null;
		ServerControl.setStatusToStopped();
	}

	public void init(ServletConfig servletconfig) throws ServletException {
		super.init(servletconfig);

		long startTime = System.currentTimeMillis();

		echoAll("-----------------------------------------------------------------");
		echoAll("[PowerEditorServlet.init] INIT Request received @ " + getTimeDate());
		echoAll("-----------------------------------------------------------------");

		String configFile = servletconfig.getInitParameter("ConfigurationFile");
		if (configFile == null || configFile.length() == 0) {
			throw new IllegalStateException("*** Configuration file not specified. Initialziation aborted!!!");
		}

		Logger commonsLogger = Logger.getLogger("org.apache.commons");
		commonsLogger.removeAllAppenders();
		commonsLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c{2} %-5p: %m%n")));
		commonsLogger.setLevel(Level.WARN);

		ConfigurationManager.initialize(configFile);
		echoAll("Configuration initialization complete");

		// persist base server path
		ConfigurationManager.getInstance().setServerBasePath(servletconfig.getServletContext().getRealPath(""));

		long configInitTime = System.currentTimeMillis() - startTime;
		startTime = System.currentTimeMillis();

		echoAll("Initializing Logs...");
		initializeLogs(ConfigurationManager.getInstance().getServerConfiguration());

		this.logger = Logger.getLogger(PowerEditorServlet.class);
		logger.info("PowerEditorServlet: config initialization time = " + configInitTime);
		echoAll("*** PowerEditor Configuration File = " + configFile);
		Package serverPackage = Package.getPackage("com.mindbox.pe.server");
		if (serverPackage != null) {
			echoAll("<TR class=\"heading\"><TD colspan=\"2\">Version Information</TD></TR>");
			echoAll("PowerEditor Version: " + serverPackage.getSpecificationVersion() + " (" + serverPackage.getImplementationVersion()
					+ ")");
			echoAll("Java Version: " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " at "
					+ System.getProperty("java.home"));
		}

		getServletContext().setAttribute("serverPackage", serverPackage);

		echoAll("* Servlet log initialized");

		logger.info("PowerEditorServlet: log initialization time = " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();

		serverStartDate = new Date();

		try {
			// initialize resource
			ResourceUtil.initialize(ConfigurationManager.getInstance().getServerBasePath(), new String[] {
					"resource.LabelsBundle",
					"resource.MessagesBundle",
					"resource.ValidationMessages" });
		}
		catch (IOException ex) {
			ex.printStackTrace(System.err);
			logger.fatal("Failed to load resource files", ex);
			throw new ServletException(ex);
		}

		logger.info("PowerEditorServlet: resource initialization time = " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();

		// load type enum values first
		try {
			TypeEnumValueLoader.getInstance().load(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig());
			logger.info("Loaded Type Enum values");
		}
		catch (SQLException e) {
			logger.fatal("Failed to load Type Enum values", e);
			throw new ServletException("Failed to load Type Enum values", e);
		}

		// update privileges in the database before calling cache
		logger.info("updating privileges in DB...");
		try {
			updatePrivilegesInDB();
			logger.info("successfully updated privileges in DB...");
		}
		catch (SQLException e) {
			logger.fatal("Error while updating privilege", e);
			throw new ServletException("Error while updating privileges", e);
		}

		logger.info("loading cache...");
		try {
			Loader.loadToCache(true);
		}
		catch (ServerException e) {
			throw new ServletException(e);
		}

		logger.info("PowerEditorServlet: cache initialization time = " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();

		echoAll("Servlet initialization complete");
		BizActionCoordinator.getInstance().setServerStartDate(new Date());
		AuditLogger.getInstance().logServerStartup();
		ServerControl.setStatusToRunning();
	}

	private String getSessionId(HttpServletRequest httpservletrequest) {
		echoAll(">> Calling getSessionId()...");
		HttpSession httpsession = httpservletrequest.getSession(false);
		if (httpsession == null) {
			echoAll("No session associated with request");
			return null;
		}
		else {
			String s = httpsession.getId();
			echoAll(">> getSessionId() returned..." + s);
			return s;
		}
	}

	public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException, IOException {
		Object obj = null;
		try {
			echoAll("-----------------------------------------------------------------");

			echoAll("[PowerEditorServlet.doPost] POST Request received @ " + getTimeDate());
			echoAll("[PowerEditorServlet.doPost] Time since last activity = " + timeSinceLastActivity());
			echoAll("-----------------------------------------------------------------");

			HttpSession httpSession = httpservletrequest.getSession();

			// check server status
			if (ServerControl.isServerReloading()) {
				obj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.reloading", null);
			}
			else if (ServerControl.isServerStopped()) {
				obj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.stopped", null);
			}
			// check relogin
			else if (SessionManager.getInstance().needsRelogin(httpSession.getId())) {
				obj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.refresh", null);
			}
			else {
				RequestComm<?> requestcomm = null;
				requestcomm = (RequestComm<?>) SapphireComm.serializeInUnchecked(httpservletrequest.getInputStream());
				if (requestcomm == null) {
					echoAll("Could not serialize input stream...");
					throw new ServletException("Could not serialize input stream...");
				}

				if (requestcomm instanceof LoginRequest) {
					httpSession.setAttribute(BYPASS_RESTART_CHECK, Boolean.TRUE);
				}

				if (httpSession.getAttribute(BYPASS_RESTART_CHECK) == null && httpSession.getCreationTime() < serverStartDate.getTime()) {
					echoAll("Session was created before servlet restarted! Session Start at: " + printDate(httpSession.getCreationTime())
							+ " Server started at: " + serverStartDate.toString());
					obj = new ErrorResponse("ServerRestartError", "");
				}
				else {
					if (httpSession.isNew() && INVALIDATE_UPON_NEW_SESSION) {
						echoAll("NEW SESSION received with id = " + httpSession.getId());

						// HTTP-Only cookie: Check if the specified session is valid; then do not terminate session!
						if (SessionRequest.class.isInstance(requestcomm)) {
							echoAll(String.format(
									"Checking new session [%s] with [session-id=%s] ",
									httpSession.getId(),
									SessionRequest.class.cast(requestcomm).getSessionID()));

							if (!SessionManager.getInstance().hasSession(SessionRequest.class.cast(requestcomm).getSessionID())) {
								SessionManager.getInstance().terminateSession(httpSession.getId());
							}
						}
						else {
							SessionManager.getInstance().terminateSession(httpSession.getId());
						}
					}

					obj = processInput(requestcomm, httpservletrequest);
					echoAll("Exiting doPost, sessionId = " + getSessionId(httpservletrequest));
				}
			}
		}
		catch (Exception exception) {
			obj = logError(exception, "Exception servicing POST request: " + exception.toString());
		}
		catch (Error error) {
			obj = logError(null, "Error servicing POST request: " + error.toString());
		}
		catch (Throwable throwable) {
			logger.error("Unknown error in doPOST", throwable);
		}

		// Serialize response
		((SapphireComm<?>) (obj)).serializeOut(new BufferedOutputStream(httpservletresponse.getOutputStream()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseComm processInput(RequestComm<?> requestcomm, HttpServletRequest httpservletrequest) {
		Object obj = null;
		echoAll(">> PowerEditorServlet.processInput() ");
		try {
			IRequestCommHandler handler = HandlerFactory.getHandler(requestcomm);
			echoAll("  using handler = " + handler);
			obj = handler.serviceRequest(requestcomm, httpservletrequest);
		}
		catch (Exception exception) {
			logger.error("Failed to process request " + requestcomm, exception);
			obj = new ErrorResponse(ErrorResponse.UNKNOWN_ERROR, exception.toString());
		}
		echoAll("<< PowerEditorServlet.processInput() with " + obj);
		return ((ResponseComm) (obj));
	}

	private String getTimeDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + " on " + (calendar.get(2) + 1) + "-" + calendar.get(5)
				+ "-" + calendar.get(1);
	}

	private String printDate(long l) {
		return (new Date(l)).toString();
	}

	private String timeSinceLastActivity() {
		long l = -1L;
		Date date = new Date();
		if (lastActivityDate != null) l = date.getTime() - lastActivityDate.getTime();
		lastActivityDate = date;
		if (l == -1L)
			return "n/a";
		else
			return "" + (double) l / 60000D + " mins.";
	}

	private ResponseComm logError(Exception exception, String s) {
		logger.error("LogError: " + s, exception);
		return null;
	}

	/* This method gets entities from EntityType and UsageType from the config file
	 * and constructs privileges based on them. Those privileges are then updated in the database
	 * as needed.
	 * @throws SQLException
	 */
	private void updatePrivilegesInDB() throws SQLException {
		PrivilegeConfig privilegeConfig = new PrivilegeConfig();
		privilegeConfig.updatePrivilegesForInitialization(
				ConfigurationManager.getInstance().getEntityConfiguration().getEntityTypeDefinitions(),
				ConfigurationManager.getInstance().getUIConfiguration().getUsageConfigList(),
				TypeEnumValueManager.getInstance().getAllEnumValues(TypeEnumValue.TYPE_STATUS));
	}


	private void initializeLogs(ServerConfiguration serverConfig) {
		// initialize Log4J
		BasicConfigurator.configure();

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.WARN);
		rootLogger.setAdditivity(false);

		rootLogger.info("*** INITIALIZED ***");

		ServerConfiguration.LogConfig logConfig = null;
		try {
			logConfig = serverConfig.getLogConfig("server");
			if (logConfig != null && logConfig.isTurnedOn()) {
				// initialize server / servlet log, if enabled
				initServerLog(logConfig);
				echoAll("Server log initialized!!!");

				logConfig = serverConfig.getLogConfig("servlet");
				if (logConfig != null) {
					initServletLog(logConfig);
					echoAll("Servlet log initialized!!!");
				}
				else {
					echoAll("Servlet log is turned OFF");
				}
			}
			else {
				echoAll("*** Server log is turned OFF!!!");
			}

			// initialize deployer log, if enabled
			logConfig = serverConfig.getLogConfig("deployer");
			if (logConfig != null && logConfig.isTurnedOn()) {
				initDeployerLog(logConfig);
				echoAll("*** Deployer log initialized!!!");
			}
			else {
				echoAll("*** Deployer log is turned OFF!!!");
			}

			// initialize DB log, if enabled
			logConfig = serverConfig.getLogConfig("database");
			if (logConfig != null && logConfig.isTurnedOn()) {
				initDBLog(logConfig);
				echoAll("*** Database log initialized!!!");
			}
			else {
				echoAll("*** DataBase log is turned OFF!!!");
			}

			// initialize Loader log, if enabled
			logConfig = serverConfig.getLogConfig("loader");
			if (logConfig != null && logConfig.isTurnedOn()) {
				initLoaderLog(logConfig);
				echoAll("*** Loader log initialized!!!");
			}
			else {
				echoAll("*** Loader log is turned OFF!!!");
			}
		}
		catch (IOException ex) {
			String msg = "*** WARNING - FAILED TO INITIALIZE LOG - " + ex.getMessage();
			echoAll(msg);
			ex.printStackTrace(System.err);
		}
	}

	private void initServerLog(ServerConfiguration.LogConfig logConfig) throws IOException {
		String logFilename = logConfig.getFilename();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			fileAppender.setMaxFileSize(logConfig.getMaxSizeInMB() + "MB");
			fileAppender.setMaxBackupIndex(20);
			Level level = (logConfig.isDebugOn() ? Level.DEBUG : Level.INFO);

			initServerLogLogger(logger, false, fileAppender, level);
			initServerLogLogger(Logger.getLogger("com.mindbox.pe.wrapper"), false, fileAppender, level);
			initServerLogLogger(Logger.getLogger("com.mindbox.pe.common"), true, fileAppender, level);

			// add 3rd party log entries here
			RollingFileAppender thirdPartyAppender = new RollingFileAppender(layout, ConfigurationManager.getInstance().getServerBasePath()
					+ "/WEB-INF/pe-3rd-party.log", true);
			fileAppender.setMaxFileSize(logConfig.getMaxSizeInMB() + "MB");
			fileAppender.setMaxBackupIndex(20);

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
		logger.info("*** INITIALIZED ***");
	}

	private void initServletLog(ServerConfiguration.LogConfig logConfig) throws IOException {
		this.logger = Logger.getLogger("com.mindbox.pe.server.servlet");
		this.logger.setAdditivity(true);

		if (logConfig.isTurnedOn()) {
			logger.setLevel((logConfig.isDebugOn() ? Level.DEBUG : Level.INFO));
		}
		else {
			logger.setLevel(Level.ERROR);
		}

		Logger commonLogger = Logger.getLogger("com.mindbox.pe.common");
		commonLogger.setAdditivity(false);
		commonLogger.setLevel(this.logger.getLevel());
		commonLogger = Logger.getLogger("com.mindbox.pe.model");
		commonLogger.setAdditivity(false);
		commonLogger.setLevel(this.logger.getLevel());
	}

	private void initDeployerLog(ServerConfiguration.LogConfig logConfig) throws IOException {
		String logFilename = logConfig.getFilename();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.generator");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			fileAppender.setMaxFileSize(logConfig.getMaxSizeInMB() + "MB");
			fileAppender.setMaxBackupIndex(20);
			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel((logConfig.isDebugOn() ? Level.DEBUG : Level.INFO));

			logger.info("*** INITIALIZED ***");
		}
		else {
			throw new IOException("Deployer log file not specified");
		}
	}

	private void initDBLog(ServerConfiguration.LogConfig logConfig) throws IOException {
		String logFilename = logConfig.getFilename();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.db");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			fileAppender.setMaxFileSize(logConfig.getMaxSizeInMB() + "MB");
			fileAppender.setMaxBackupIndex(20);
			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel(Level.DEBUG);

			logger.info("*** INITIALIZED ***");
		}
		else {
			throw new IOException("DB log file not specified");
		}
	}

	private void initLoaderLog(ServerConfiguration.LogConfig logConfig) throws IOException {
		String logFilename = logConfig.getFilename();
		if (logFilename != null) {
			Logger logger = Logger.getLogger("com.mindbox.pe.server.db.loaders");

			PatternLayout layout = new PatternLayout(logConfig.getPattern());

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logFilename, true);
			fileAppender.setMaxFileSize(logConfig.getMaxSizeInMB() + "MB");
			fileAppender.setMaxBackupIndex(20);
			logger.removeAllAppenders();
			logger.addAppender(fileAppender);
			logger.setAdditivity(false);
			logger.setLevel((logConfig.isDebugOn() ? Level.DEBUG : Level.INFO));

			logger.info("*** INITIALIZED ***");
		}
		else {
			throw new IOException("Loader log file not specified");
		}
	}

	public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	private void echoAll(String msg) {
		if (logger != null) {
			logger.info(msg);
		}
	}

	private Date serverStartDate;
	private Date lastActivityDate;
	private Logger logger = null;
}
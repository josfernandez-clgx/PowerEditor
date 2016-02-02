package com.mindbox.pe.server.config;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mindbox.pe.common.PowerEditorXMLParser;
import com.mindbox.pe.server.db.DefaultPEDBCProvider;
import com.mindbox.pe.server.spi.PEDBCProvider;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
public final class ServerConfiguration {

	public static final class LogConfig {

		private final boolean debugOn;
		private final boolean write;
		private final String filename;
		private final String pattern;
		private final int maxSizeMB;

		private LogConfig(boolean write, boolean debugOn, String filename, String pattern, int maxSizeMB) {
			this.write = write;
			this.debugOn = debugOn;
			this.filename = filename;
			this.pattern = pattern;
			this.maxSizeMB = maxSizeMB;
		}

		public boolean isTurnedOn() {
			return write;
		}

		public boolean isDebugOn() {
			return debugOn;
		}

		public String getFilename() {
			return filename;
		}

		public String getPattern() {
			return pattern;
		}

		public int getMaxSizeInMB() {
			return maxSizeMB;
		}
	}

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

		private DBConfig(Class<?> providerClass, String driver, String conn, String user, Password pwd, int maxConn,
				String guidelineRuleProviderClass, String userManagementProviderClass, int monitorIntervalInSeconds, String validationQuery) {
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

		public String getValidationQuery() {
			return validationQuery;
		}

		public String getGuidelineRuleProviderClassName() {
			return guidelineRuleProviderClass;
		}

		public String getUserManagementProviderClassName() {
			return userManagementProviderClass;
		}

		public Class<?> getProviderClass() {
			return providerClass;
		}

		public String getDriver() {
			return driver;
		}

		public String getConnectionStr() {
			return connection;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return pwd.getClearText();
		}

		public int getMaxConnections() {
			return maxConnections;
		}

		public int getMonitorIntervalInSeconds() {
			return monitorIntervalInSeconds;
		}
	}

	public static final class DeploymentConfig {

		private final String baseDir;
		private final boolean saveOld;
		private final boolean useTimestampFolder;
		private final String postDeployScript;
		private final boolean reportMissingLink;

		private DeploymentConfig(String baseDir, boolean saveOld, boolean useTimestampFolder, String postDeployScript,
				boolean reportMissingLink) {
			this.baseDir = baseDir;
			this.saveOld = saveOld;
			this.useTimestampFolder = useTimestampFolder;
			this.postDeployScript = postDeployScript;
			this.reportMissingLink = reportMissingLink;
		}

		public boolean saveOldFiles() {
			return saveOld;
		}

		public boolean useTimestampFolder() {
			return useTimestampFolder;
		}

		public String getBaseDir() {
			return baseDir;
		}

		public String getPostDeployScriptFile() {
			return postDeployScript;
		}

		public boolean isReportMissingLinkOn() {
			return reportMissingLink;
		}
	}

	private final Map<String, LogConfig> logConfigMap;
	private final DBConfig dbConfig;
	private final DeploymentConfig deployConfig;
	private final boolean auditAll;

	ServerConfiguration(Element serverElement) {
		// init DB configs -- configured using ConfigXMLDigester
		Element dbElement = PowerEditorXMLParser.getFirstChild(serverElement, "Database");
		if (dbElement == null) {
			throw new IllegalArgumentException("<Server> tag must contain <Database> tag.");
		}
		if (PowerEditorXMLParser.getFirstChild(dbElement, "Driver") == null) {
			throw new IllegalArgumentException("<Server><Database> tag must contain <Driver> tag.");
		}
		if (PowerEditorXMLParser.getFirstChild(dbElement, "Connection") == null) {
			throw new IllegalArgumentException("<Server><Database> tag must contain <Connection> tag.");
		}
		if (PowerEditorXMLParser.getFirstChild(dbElement, "User") == null) {
			throw new IllegalArgumentException("<Server><Database> tag must contain <User> tag.");
		}
		if (PowerEditorXMLParser.getFirstChild(dbElement, "Password") == null) {
			throw new IllegalArgumentException("<Server><Database> tag must contain <Password> tag.");
		}
		Password pwd = Password.fromEncryptedString(PowerEditorXMLParser.getValueOfFirstChild(dbElement, "Password"));

		int maxConn = 5;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "MaxConnection") != null) {
			maxConn = Integer.parseInt(PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(dbElement, "MaxConnection")));
		}

		String ruleProviderClassname = null;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "GuidelineRuleProviderClass") != null) {
			ruleProviderClassname = PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(
					dbElement,
					"GuidelineRuleProviderClass"));
		}

		String userManagementProviderClassname = null;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "UserManagementProviderClass") != null) {
			userManagementProviderClassname = PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(
					dbElement,
					"UserManagementProviderClass"));
		}

		Class<?> providerClass = null;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "Provider") != null) {
			try {
				providerClass = Class.forName(PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(dbElement, "Provider")));
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("<Server><Database><Provider> class not found: "
						+ PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(dbElement, "Provider")));
			}

			if (!(PEDBCProvider.class.isAssignableFrom(providerClass))) {
				throw new IllegalArgumentException("The <Database><Provider> class " + providerClass.getName()
						+ " is invalid. It must be implement PEDBCProvider interface.");
			}
		}
		else {
			providerClass = DefaultPEDBCProvider.class;
		}

		int monitorInterval = -1;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "MonitorInterval") != null) {
			try {
				monitorInterval = Integer.parseInt(PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(
						dbElement,
						"MonitorInterval")));
			}
			catch (Exception ex) {
			}
		}
		String validationQuery = null;
		if (PowerEditorXMLParser.getFirstChild(dbElement, "ValidationQuery") != null) {
			validationQuery = PowerEditorXMLParser.getValue(PowerEditorXMLParser.getFirstChild(dbElement, "ValidationQuery"));
		}
		this.dbConfig = new DBConfig(
				providerClass,
				PowerEditorXMLParser.getValueOfFirstChild(dbElement, "Driver"),
				PowerEditorXMLParser.getValueOfFirstChild(dbElement, "Connection"),
				PowerEditorXMLParser.getValueOfFirstChild(dbElement, "User"),
				pwd,
				maxConn,
				ruleProviderClassname,
				userManagementProviderClassname,
				monitorInterval,
				validationQuery);

		// init log configs
		this.logConfigMap = new HashMap<String, LogConfig>();
		Element logElement = PowerEditorXMLParser.getFirstChild(serverElement, "Log");
		if (logElement == null) {
			throw new IllegalArgumentException("<Server> tag does not contain <Log> tag.");
		}

		NodeList logFiles = logElement.getElementsByTagName("LogFile");
		for (int i = 0; i < logFiles.getLength(); i++) {
			Element element = (Element) logFiles.item(i);
			String key = element.getAttribute("type");
			boolean writeLog = "YES".equalsIgnoreCase(element.getAttribute("writeLog"));
			boolean debugOn = "YES".equalsIgnoreCase(element.getAttribute("debug"));
			String file = element.getAttribute("file");
			String pattern = element.getAttribute("pattern");
			if (pattern == null) {
				pattern = "%d{yyyy-MM-dd HH:mm:sss} %c{2}: %m%n";
			}
			String maxSizeStr = element.getAttribute("maxSize");
			int maxSize = 2;
			try {
				if (maxSizeStr != null) {
					maxSize = Integer.parseInt(maxSizeStr);
				}
			}
			catch (Exception ex) {
			}

			logConfigMap.put(key, new LogConfig(writeLog, debugOn, file, pattern, maxSize));
		}

		// init deployment configs
		Element deployElement = PowerEditorXMLParser.getFirstChild(serverElement, "Deployment");
		if (deployElement == null) {
			throw new IllegalArgumentException("<Server> tag does not contain <Deployment> tag.");
		}

		if (PowerEditorXMLParser.getFirstChild(deployElement, "BaseDir") == null) {
			throw new IllegalArgumentException("<Server><Deployment> tag does not contain <BaseDir> tag.");
		}

		String baseDir = PowerEditorXMLParser.getValueOfFirstChild(deployElement, "BaseDir");
		boolean saveOld = false;
		if (PowerEditorXMLParser.getFirstChild(deployElement, "SaveOldFiles") != null) {
			saveOld = "YES".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(deployElement, "SaveOldFiles"));
		}
		boolean useTSFolder = false;
		if (PowerEditorXMLParser.getFirstChild(deployElement, "UseTimeStampFolder") != null) {
			useTSFolder = "YES".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(deployElement, "UseTimeStampFolder"));
		}

		String postDeployScript = null;
		if (PowerEditorXMLParser.getFirstChild(deployElement, "PostDeployScript") != null) {
			Element fileElement = PowerEditorXMLParser.getFirstChild(
					PowerEditorXMLParser.getFirstChild(deployElement, "PostDeployScript"),
					"File");
			if (fileElement != null) {
				postDeployScript = PowerEditorXMLParser.getValue(fileElement);
			}
		}
		boolean reportMissingLink = true;
		if (PowerEditorXMLParser.getFirstChild(deployElement, "ReportMissingLink") != null) {
			reportMissingLink = "YES".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(deployElement, "ReportMissingLink"));
		}
		this.deployConfig = new DeploymentConfig(baseDir, saveOld, useTSFolder, postDeployScript, reportMissingLink);

		// load audit conifgs
		Element auditElement = PowerEditorXMLParser.getFirstChild(serverElement, "Audit");
		if (auditElement == null) {
			throw new IllegalArgumentException("<Server> tag does not contain <Audit> tag.");
		}
		if (PowerEditorXMLParser.getFirstChild(auditElement, "AuditAll") != null) {
			this.auditAll = "YES".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(auditElement, "AuditAll"));
		}
		else {
			this.auditAll = false;
		}
	}

	public LogConfig getLogConfig(String type) {
		return logConfigMap.get(type);
	}

	public DeploymentConfig getDeploymentConfig() {
		return deployConfig;
	}

	public DBConfig getDatabaseConfig() {
		return dbConfig;
	}

	public boolean auditAll() {
		return auditAll;
	}

}

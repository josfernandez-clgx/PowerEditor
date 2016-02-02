package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ServerConfiguration;

/**
 * Database Connection Manager.
 * This implements its own connection pool.
 * @since PowerEditor 1.0
 */
public final class DBConnectionManager {

	private static final String POOLNAME = "PEDB";
	private static DBConnectionManager instance;

	public static synchronized DBConnectionManager getInstance() {
		if (instance == null) instance = new DBConnectionManager();
		return instance;
	}

	private class ConnectionMonitor extends Thread {

		private final Logger logger;
		private final long interval;

		ConnectionMonitor(int intervalInSeconds) {
			super("DBConnMonitor: interval = " + intervalInSeconds + " (sec)");
			this.logger = Logger.getLogger(getClass());
			this.interval = intervalInSeconds * 1000;
			// critical to set this as a daemon thread
			setDaemon(true);
			setPriority(MIN_PRIORITY);

			start();
		}

		public void run() {
			// runs forever, until server shuts down
			while (true) {
				try {
					logger.info("# of connections in use = " + connectionPool.getNumActive());
					logger.info("# of idle connections   = " + connectionPool.getNumIdle());
				}
				catch (Exception ex) {
					this.logger.error("Error in DB connection monitor.run()", ex);
				}

				logger.debug("sleeping...");
				try {
					sleep(interval);
				}
				catch (InterruptedException e) {
					this.logger.warn("Interrupted while sleeping for " + interval, e);
				}
			}
		}
	}

	private final Logger logger = Logger.getLogger(getClass());
	private final GenericObjectPool connectionPool;

	private DBConnectionManager() {
		connectionPool = new GenericObjectPool(null);
		init();
		// start db connection monitor 
		new ConnectionMonitor(ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig().getMonitorIntervalInSeconds());
	}

	public void freeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				logger.warn("Failed to close " + connection, e);
			}
		}
	}

	public final Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:" + POOLNAME);
	}

	private void init() {
		ServerConfiguration.DBConfig dbConfig = ConfigurationManager.getInstance().getServerConfiguration().getDatabaseConfig();
		try {
			Class.forName(dbConfig.getDriver()).newInstance();
		}
		catch (Exception exception) {
			logger.fatal("Can't initialize JDBC driver: " + dbConfig.getDriver(), exception);
		}
		connectionPool.setMaxActive(dbConfig.getMaxConnections());
		connectionPool.setMinIdle(1);
		if (!UtilBase.isEmpty(dbConfig.getValidationQuery())) {
			connectionPool.setTestOnBorrow(true);
		}
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbConfig.getConnectionStr(), dbConfig.getUser(), dbConfig.getPassword());
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory,
				connectionPool,
				null,
				(UtilBase.isEmpty(dbConfig.getValidationQuery()) ? null : dbConfig.getValidationQuery()),
				false,
				false);
		PoolingDriver driver = new PoolingDriver();
		driver.registerPool(POOLNAME, connectionPool);
		logger.info("DB Connection Manager initialized with " + poolableConnectionFactory);
	}
}
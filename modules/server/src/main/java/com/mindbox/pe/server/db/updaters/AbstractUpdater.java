package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.db.DBConnectionManager;

public abstract class AbstractUpdater {

	protected static final String IN_CLAUSE_LIST_HOLDER = "<in-clause-list>"; // set of ?,?,?... for an SQL "IN" clause

	private static final Pattern IN_CLAUSE_REPLACE_LIST_PATTERN = Pattern.compile(IN_CLAUSE_LIST_HOLDER);

	private Connection mConnection;
	private int connectionRefCount;
	protected final Logger logger;

	/**
	 * @see PowerEditor 3.2.0
	 */
	private boolean isExternalConnection;

	protected AbstractUpdater() {
		this.logger = Logger.getLogger(getClass());
		isExternalConnection = false;
		connectionRefCount = 0;
	}

	protected AbstractUpdater(Connection connection) {
		this.logger = Logger.getLogger(getClass());
		this.mConnection = connection;
		this.isExternalConnection = true;
		connectionRefCount = 1;
	}

	/** 
	 * Used to generate a SQL stmt with an "IN" clause.
	 * To use, declare a query string with an SQL partially completed IN clause such as, 
	 * 
	 * <code>select * from foo where id IN ({@link #IN_CLAUSE_LIST_HOLDER})</code>
	 * @param stmt stmt
	 * @param elementCount elementCount
	 * @return SQL statement
	 */
	protected String fillInClause(String stmt, int elementCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elementCount; i++) {
			sb.append(i == elementCount - 1 ? "?" : "?,");
		}
		return IN_CLAUSE_REPLACE_LIST_PATTERN.matcher(stmt).replaceFirst(sb.toString());
	}

	public synchronized final Connection getConnection() throws SQLException {
		logger.debug(">>> getConnection: " + connectionRefCount);
		Connection connection = mConnection;
		if (connection != null && connection.isClosed()) {
			DBConnectionManager.getInstance().freeConnection(connection);
			connection = null;
		}
		if (connection == null || connection.isClosed()) {
			logger.info("Fetching new connection...");
			connection = DBConnectionManager.getInstance().getConnection();
			isExternalConnection = false;
			if (connection == null) {
				logger.error("Can't get connection");
				throw new SQLException("Connection cannot be obtained: connection manager returned null");
			}
			mConnection = connection;
		}
		connectionRefCount++;
		logger.debug("<<< getConnection: " + connectionRefCount);
		return connection;
	}

	public synchronized final void releaseConnection() {
		logger.debug(">>> releaseConnection: " + connectionRefCount);
		connectionRefCount--;
		if (!isExternalConnection && mConnection != null && connectionRefCount <= 0) {
			logger.debug("... releaseConnection: releasing external connection...");
			DBConnectionManager.getInstance().freeConnection(mConnection);
			mConnection = null;
			connectionRefCount = 0;
		}
		logger.debug("<<< releaseConnection: " + connectionRefCount);
	}

	protected synchronized final void setConnection(Connection connection) {
		mConnection = connection;
		isExternalConnection = true;
	}
}
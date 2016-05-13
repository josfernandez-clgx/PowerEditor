package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.db.DBConnectionManager;

public abstract class AbstractUpdater {
	protected static final String IN_CLAUSE_LIST_HOLDER = "<in-clause-list>"; // set of ?,?,?... for an SQL "IN" clause
	
	protected AbstractUpdater() {
		this.logger = Logger.getLogger(getClass());
		isExternalConnection = false;
		mConnectionRefCount = 0;
	}

	protected AbstractUpdater(Connection connection) {
		this.logger = Logger.getLogger(getClass());
		this.mConnection = connection;
		this.isExternalConnection = true;
		mConnectionRefCount = 1;
	}

	public synchronized final void releaseConnection() {
		logger.debug(">>> releaseConnection: " + mConnectionRefCount);
		mConnectionRefCount--;
		if (!isExternalConnection && mConnection != null && mConnectionRefCount <= 0) {
			logger.debug("... releaseConnection: releasing external connection...");
			DBConnectionManager.getInstance().freeConnection(mConnection);
			mConnection = null;
			mConnectionRefCount = 0;
		}
		logger.debug("<<< releaseConnection: " + mConnectionRefCount);
	}

	public synchronized final Connection getConnection() throws SQLException {
		logger.debug(">>> getConnection: " + mConnectionRefCount);
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
		mConnectionRefCount++;
		logger.debug("<<< getConnection: " + mConnectionRefCount);
		return connection;
	}

	protected synchronized final void setConnection(Connection connection) {
		mConnection = connection;
		isExternalConnection = true;
	}
	
	private static final Pattern IN_CLAUSE_REPLACE_LIST_PATTERN = Pattern.compile(IN_CLAUSE_LIST_HOLDER);
	/** 
	 * Used to generate a SQL stmt with an "IN" clause.
	 * To use, declare a query string with an SQL partially completed IN clause such as, 
	 * 
	 * <code>select * from foo where id IN ({@link #IN_CLAUSE_LIST_HOLDER})</code>
	 */
	protected String fillInClause(String stmt, int elementCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elementCount; i++) {
			sb.append(i==elementCount-1 ? "?" : "?,");
		}
		return IN_CLAUSE_REPLACE_LIST_PATTERN.matcher(stmt).replaceFirst(sb.toString());
	}

	private Connection mConnection;

	private int mConnectionRefCount;

	protected final Logger logger;
	
	/**
	 * @see PowerEditor 3.2.0
	 */
	private boolean isExternalConnection;
}
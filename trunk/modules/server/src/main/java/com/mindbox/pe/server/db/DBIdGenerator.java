package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.exceptions.SapphireException;

/**
 * Responsible for generation of unique ids.
 *
 */
public class DBIdGenerator {

	private class CacheOverflowException extends Exception {

		private static final long serialVersionUID = 930115053667751618L;

		CacheOverflowException() {
		}
	}

	private class DBCachedId {

		private int nextId;
		private int maxId;
		private int idCacheSize;

		public DBCachedId(int i, int j) {
			nextId = i;
			maxId = (i + j) - 1;
			idCacheSize = j;
		}

		int getNextId() {
			return nextId;
		}

		void increment() {
			maxId += idCacheSize;
		}

		public synchronized int next() throws CacheOverflowException {
			int i = nextId;
			if (i > maxId) {
				throw new CacheOverflowException();
			}
			else {
				nextId++;
				return i;
			}
		}

		@Override
		public String toString() {
			return String.format("NextId[next=%d,max=%d,cacheSize=%d]", nextId, maxId, idCacheSize);
		}
	}

	public static final String SEQUENTIAL_ID = "SEQUENTIAL";
	public static final String FILTER_ID = "Filter";
	public static final String GRID_ID = "Grid";

	public static final String AUDIT_ID = "Audit";
	public static final String RULE_ID = "RuleID";

	public static final String[] ALL_ID_TYPES = { SEQUENTIAL_ID, FILTER_ID, GRID_ID, AUDIT_ID, RULE_ID };

	public static final String REGEX_VALID_ID_TYPES = "^(" + SEQUENTIAL_ID + "|" + FILTER_ID + "|" + GRID_ID + "|" + AUDIT_ID + "|" + RULE_ID + ")$";

	private static DBIdGenerator instance;
	private static Map<String, DBCachedId> idMap = new HashMap<String, DBCachedId>();

	public static synchronized DBIdGenerator getInstance() {
		if (instance == null) {
			try {
				instance = new DBIdGenerator();
			}
			catch (SQLException ex) {
				throw new IllegalStateException("Faield to create the instance of DBIdGenerator: " + ex.getMessage());
			}
		}
		return instance;
	}

	public static boolean isSupportedIdType(String name) {
		for (String type : ALL_ID_TYPES) {
			if (type.equals(name)) return true;
		}
		return false;
	}

	private final Logger logger = Logger.getLogger(DBIdGenerator.class);

	private DBIdGenerator() throws SQLException {
		init();
	}

	public synchronized int getCurrentIdForImport(final String type) throws SQLException {
		final DBCachedId dbcachedid = idMap.get(type);
		return dbcachedid == null ? 0 : dbcachedid.getNextId();
	}

	private void incrementDB() throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement("update MB_ID_GENERATOR set next_id=next_id+id_cache_size");
			int i = ps.executeUpdate();
			logger.info("Updated " + i + " rows!");
			connection.commit();
		}
		catch (Exception ex) {
			connection.rollback();
		}
		finally {
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void incrementDB(String s) throws SQLException {
		log("Calling incrementDB for " + s);
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement("update MB_ID_GENERATOR set next_id=next_id+id_cache_size  where id_type=?");
			ps.setString(1, s);
			int i = ps.executeUpdate();
			connection.commit();
			logger.info("Updated " + i + " row(s)!");
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
		catch (Exception ex) {
			connection.rollback();
			throw new SQLException("Error incrementing next id: " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void init() throws SQLException {
		load();
		incrementDB();
	}

	private synchronized void load() throws SQLException {
		idMap.clear();
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection connection = dbconnectionmanager.getConnection();

		PreparedStatement ps = null;
		ResultSet resultset = null;
		try {
			ps = connection.prepareStatement("select id_type, next_id, id_cache_size from MB_ID_GENERATOR");
			resultset = ps.executeQuery();
			logger.info("===========Id Generator ======");
			String s;
			DBCachedId dbcachedid;
			for (; resultset.next(); idMap.put(s, dbcachedid)) {
				s = UtilBase.trim(resultset.getString(1));
				int i = resultset.getInt(2);
				int j = resultset.getInt(3);
				logger.info(s + "; " + i + "; " + j);
				dbcachedid = new DBCachedId(i, j);
			}
		}
		finally {
			DBUtil.closeLocallyManagedResources(resultset, ps);
			dbconnectionmanager.freeConnection(connection);
		}
	}

	private void log(String s) {
		logger.debug(s);
	}

	private synchronized int next(String s) throws SapphireException {
		int i = -1;
		final DBCachedId dbcachedid = idMap.get(s);
		if (dbcachedid == null) {
			throw new SapphireException("Invalid Type: " + s);
		}
		boolean flag = true;
		while (flag) {
			try {
				i = dbcachedid.next();
				flag = false;
			}
			catch (CacheOverflowException ex) {
				logger.info("Cache Overflow detected! Fetching the next ID set from DB...");
				try {
					incrementDB(s);
					dbcachedid.increment();
				}
				catch (SQLException sqlEx) {
					throw new SapphireException(sqlEx.getMessage());
				}
			}
		}
		return i;
	}

	public int nextAuditID() throws SapphireException {
		return next(AUDIT_ID);
	}

	public int nextFilterID() throws SapphireException {
		return next(FILTER_ID);
	}

	public int nextGridID() throws SapphireException {
		return next(GRID_ID);
	}

	public int nextRuleID() throws SapphireException {
		return next(RULE_ID);
	}

	public int nextSequentialID() throws SapphireException {
		return next(SEQUENTIAL_ID);
	}

	public synchronized void setNextID(String type, int seed, int cache) throws SQLException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = dbconnectionmanager.getConnection();

		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("update MB_ID_GENERATOR set next_id=?,id_cache_size=? where id_type=?");
			ps.setInt(1, seed);
			ps.setInt(2, cache);
			ps.setString(3, type);
			int count = ps.executeUpdate();
			if (count == 0) {
				throw new SQLException("No row updated");
			}
			conn.commit();
			logger.info("next-id update successful. reloading data...");

			init();
		}
		catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			throw new SQLException("Error while setting next id for " + type + ", " + seed + ": " + ex.getMessage());
		}
		finally {
			if (ps != null) ps.close();
			dbconnectionmanager.freeConnection(conn);
		}
	}

	@Override
	public String toString() {
		return String.format("IdGenerator[size=%d]", idMap.size());
	}

}

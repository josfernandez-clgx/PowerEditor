/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public final class DBConnectionFactory {

	private static DBConnectionFactory instance = null;

	public static DBConnectionFactory getInstance() {
		if (instance == null) {
			instance = new DBConnectionFactory();
		}
		return instance;
	}

	private final Map<DBConnInfo,Connection> connMap;

	private DBConnectionFactory() {
		connMap = new HashMap<DBConnInfo,Connection>();
	}

	public void finalize() {
		closeAllConnections();
	}

	public void freeConnections() {
		closeAllConnections();
		connMap.clear();
	}
	
	private void closeAllConnections() {
		for (Iterator<DBConnInfo> iter = connMap.keySet().iterator(); iter.hasNext();) {
			DBConnInfo key = iter.next();
			try {
				connMap.get(key).close();
			}
			catch (Exception ex) {}
		}
	}

	public Connection getConnection(DBConnInfo connInfo)
		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (connMap.containsKey(connInfo)) {
			return connMap.get(connInfo);
		}
		else {
			Driver driver = (Driver) Class.forName(connInfo.getDriverName()).newInstance();
			DriverManager.registerDriver(driver);

			Connection conn =
				DriverManager.getConnection(connInfo.getConnectionStr(), connInfo.getUser(), connInfo.getPassword());

			connMap.put(connInfo, conn);
			return conn;
		}
	}

}

/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.db;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Encapsulates database connection information.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public final class DBConnInfo implements Serializable {

	private static final long serialVersionUID = 200401301200000L;

	public static DBConnInfo newODBCConnInfo(String driverClass, String connectionStr, String user, String pwd) {
		return new DBConnInfo(driverClass, connectionStr, user, pwd);
	}

	/*
	 public static DBConnInfo newODBCConnInfo(String dsnName, String user, String pwd) {
	 return new DBConnInfo("sun.jdbc.odbc.JdbcOdbcDriver","jdbc:odbc:"+dsnName, user, pwd);
	 }*/


	private final String driverName;
	private final String connectionStr;
	private final String user;
	private transient final int hashCode;
	private String password = null;

	private DBConnInfo(String driverName, String connStr, String user) {
		this.driverName = driverName;
		this.connectionStr = connStr;
		this.user = user;
		this.hashCode = (driverName + connectionStr + user).hashCode();
	}

	private DBConnInfo(String driverName, String connStr, String user, String pwd) {
		this(driverName, connStr, user);
		this.password = pwd;
	}

	private Object readResolve() throws ObjectStreamException {
		return new DBConnInfo(driverName, connectionStr, user, password);
	}

	public String getConnectionStr() {
		return connectionStr;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getPassword() {
		return password;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String string) {
		password = string;
	}

	public boolean equals(Object arg) {
		if (arg instanceof DBConnInfo) {
			return hashCode == arg.hashCode();
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return hashCode;
	}

	public String toString() {
		return user + "@" + connectionStr;
	}

}
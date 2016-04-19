/*
 * Created on 2005. 3. 28.
 *
 */
package com.mindbox.pe.wrapper.sybase;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;


/**
 * Wrapper for jConnect connection object to get around the
 * &quote;SET CHAINED command not allowed within multi-statement transaction&quote; issue.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
final class PESybaseConnection implements Connection {

	private final Connection conn;

	/**
	 * 
	 */
	PESybaseConnection(Connection conn) {
		this.conn = conn;
	}

	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}

	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}

	public void close() throws SQLException {
		conn.close();
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public void rollback() throws SQLException {
		conn.rollback();
	}

	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}

	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	public void setHoldability(int arg0) throws SQLException {
		conn.setHoldability(arg0);
	}

	public void setTransactionIsolation(int arg0) throws SQLException {
		conn.setTransactionIsolation(arg0);
	}

	public void setAutoCommit(boolean arg0) throws SQLException {
		try {
			conn.rollback();
		}
		catch (Exception ex) {
			// ignore
		}
		conn.setAutoCommit(arg0);
	}

	public void setReadOnly(boolean arg0) throws SQLException {
		conn.setReadOnly(arg0);
	}

	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}

	public void setCatalog(String arg0) throws SQLException {
		conn.setCatalog(arg0);
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}

	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		conn.releaseSavepoint(arg0);
	}

	public void rollback(Savepoint arg0) throws SQLException {
		conn.rollback(arg0);
	}

	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return conn.createStatement(arg0, arg1);
	}

	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		return conn.createStatement(arg0, arg1, arg2);
	}

	public Map<String,Class<?>> getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		conn.setTypeMap(arg0);
	}

	public String nativeSQL(String arg0) throws SQLException {
		return conn.nativeSQL(arg0);
	}

	public CallableStatement prepareCall(String arg0) throws SQLException {
		return conn.prepareCall(arg0);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException {
		return conn.prepareCall(arg0, arg1, arg2);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return conn.prepareCall(arg0, arg1, arg2, arg3);
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return conn.prepareStatement(arg0);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		return conn.prepareStatement(arg0, arg1);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		return conn.prepareStatement(arg0, arg1, arg2);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return conn.prepareStatement(arg0, arg1, arg2, arg3);
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		return conn.prepareStatement(arg0, arg1);
	}

	public Savepoint setSavepoint(String arg0) throws SQLException {
		return conn.setSavepoint(arg0);
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		return conn.prepareStatement(arg0, arg1);
	}

	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return conn.createArrayOf(arg0, arg1);
	}

	public Blob createBlob() throws SQLException {
		return conn.createBlob();
	}

	public Clob createClob() throws SQLException {
		return conn.createClob();
	}

	public NClob createNClob() throws SQLException {
		return conn.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return conn.createSQLXML();
	}

	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return conn.createStruct(arg0, arg1);
	}

	public Properties getClientInfo() throws SQLException {
		return conn.getClientInfo();
	}

	public String getClientInfo(String arg0) throws SQLException {
		return conn.getClientInfo(arg0);
	}

	public boolean isValid(int arg0) throws SQLException {
		return conn.isValid(arg0);
	}

	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		conn.setClientInfo(arg0);
	}

	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		conn.setClientInfo(arg0, arg1);
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return conn.isWrapperFor(arg0);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return conn.unwrap(arg0);
	}

}
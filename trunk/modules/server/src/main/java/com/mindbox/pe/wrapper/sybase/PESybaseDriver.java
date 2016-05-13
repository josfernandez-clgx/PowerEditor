/*
 * Created on 2005. 3. 28.
 *
 */
package com.mindbox.pe.wrapper.sybase;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * This is a wrapper around Sybase JDBC driver (jConnect) to get around the
 * &quote;SET CHAINED command not allowed within multi-statement transaction&quote; issue.
 * <p>
 * This class requires Sybase jConnect driver.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class PESybaseDriver implements Driver {

	static {
		try {
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver");

			PESybaseDriver theDriver = new PESybaseDriver(DriverManager.getDriver("jdbc:sybase:Tds:localhost:5000"));
			DriverManager.registerDriver(theDriver);
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw new IllegalStateException("Error initializing Wrapepr Sybase JDBC Driver: " + ex.getMessage());
		}
	}

	private final Driver sybaseDriver;

	private PESybaseDriver(Driver driver) {
		this.sybaseDriver = driver;
	}

	@Override
	public boolean acceptsURL(String arg0) throws SQLException {
		return arg0 != null && arg0.startsWith("jdbc:pesybase:");
	}

	@Override
	public Connection connect(String arg0, Properties arg1) throws SQLException {
		if (acceptsURL(arg0)) {
			Connection conn = sybaseDriver.connect("jdbc:sybase:" + arg0.substring(14), arg1);
			return (conn == null ? null : new PESybaseConnection(conn));
		}
		return null;
	}

	@Override
	public int getMajorVersion() {
		return sybaseDriver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return sybaseDriver.getMinorVersion();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1) throws SQLException {
		return sybaseDriver.getPropertyInfo(arg0, arg1);
	}

	@Override
	public boolean jdbcCompliant() {
		return sybaseDriver.jdbcCompliant();
	}

}
/*
 * Created on 2004. 4. 15.
 *
 */
package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.Util;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class DBUtil extends DefaultPEDBCProvider {

	public static final ThreadLocal<DateFormat> THREADLOCAL_SD_FORMAT = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};


	private static final String SPACE_PLACE_HOLDER = "<SP>";

	public static final String decodeSpacePlaceHolder(String str) {
		if (str != null && str.indexOf(SPACE_PLACE_HOLDER) > -1) {
			str = str.replaceAll(SPACE_PLACE_HOLDER, " ");
		}
		return str;
	}

	public static final String encodeSpacePlaceHolder(String str) {
		return (str == null ? null : str.replaceAll(" ", SPACE_PLACE_HOLDER));
	}

	public static final String extractBlobValue(ResultSet rs, int columnNo) throws SQLException {
		// this may not work for mySQL - this method provides framework
		// to retrieve BLOB content in a DB independent way

		// Need to make this work when mySQL is supported

		return rs.getString(columnNo);
	}


	private DBUtil() {
	}


	public static Date getDateValue(ResultSet rs, int column) throws SQLException, ParseException {
		String date_str = rs.getString(column);
		if (date_str != null) {
			return THREADLOCAL_SD_FORMAT.get().parse(Util.formatDB2DateString(date_str));
		}
		else {
			return null;
		}

	}


	public static void setDateValue(PreparedStatement ps, int column, Date date) throws SQLException {
		if (date == null) {
			ps.setNull(column, 12);
		}
		else {
			ps.setString(column, THREADLOCAL_SD_FORMAT.get().format(date));
		}
	}

	public static void rollBackLocallyManagedConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			}
			catch (SQLException ex) {
				// don't throw or free connection, assume calling method is calling this from a catch block,
				// and frees connection in its finally block.
				Logger.getLogger("com.mindbox.server.db").error("java.sql.Connection.rollback() failed " + conn);
			}
		}
	}

	public static void closeLocallyManagedStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			}
			catch (SQLException ex) {
				// don't throw an exception, assume calling method is calling this from a catch block
				Logger.getLogger("com.mindbox.server.db").error("java.sql.Statement.close() failed " + statement);
			}
		}
	}

	public static void closeLocallyManagedResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			}
			catch (SQLException ex) {
				// don't throw an exception, assume calling method is calling this from a catch block
				Logger.getLogger("com.mindbox.server.db").error("java.sql.resultSet.close() failed " + resultSet);
			}
		}
	}

	public static void closeLocallyManagedResources(ResultSet resultSet, Statement statement) {
		closeLocallyManagedResultSet(resultSet);
		closeLocallyManagedStatement(statement);

	}
}

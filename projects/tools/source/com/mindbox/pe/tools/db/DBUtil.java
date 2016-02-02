/*
 * Created on 2004. 12. 20.
 *
 */
package com.mindbox.pe.tools.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class DBUtil {

	public static final SimpleDateFormat FORMAT_DATE_TIME_SEC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static String formatDB2DateString(String date_str) {
		if (date_str.length() == 26) {
			date_str = date_str.substring(0, 10) + " " + date_str.substring(11, 13) + ":" + date_str.substring(14, 16) + ":"
					+ date_str.substring(17, 19);
		}
		return date_str;

	}

	public static String getStringValue(ResultSet rs, int column) throws SQLException {
		String value = rs.getString(column);
		return (value == null ? null : value.trim());
	}

	public static Date getDateValue(ResultSet rs, int column) throws SQLException {
		String date_str = rs.getString(column);
		if (date_str != null) {
			try {
				return FORMAT_DATE_TIME_SEC.parse(formatDB2DateString(date_str));
			}
			catch (ParseException ex) {
				ex.printStackTrace();
				return null;
			}
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
			ps.setString(column, FORMAT_DATE_TIME_SEC.format(date));
		}
	}

	private DBUtil() {

	}
}
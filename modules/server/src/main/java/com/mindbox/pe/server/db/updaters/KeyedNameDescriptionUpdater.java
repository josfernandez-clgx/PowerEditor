/*
 * Created on May 27, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.server.db.updaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @since PowerEditor 1.0
 */
public abstract class KeyedNameDescriptionUpdater extends AbstractUpdater {

	/**
	 * Default constructor.
	 *
	 */
	protected KeyedNameDescriptionUpdater() {
	}

	protected void insert(Connection conn, int key, String name, String description) throws SQLException {
		if (key < 0) throw new IllegalArgumentException("Invalid key: " + key);
		if (name == null) throw new NullPointerException("name cannot be null");
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getInsertQuery());
			ps.setInt(1, key);
			ps.setString(2, name);
			ps.setString(3, (description == null ? "" : description));
			
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to insert for " + key + " with " + name);
			}
		}
		finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
	
	protected void delete(Connection conn, int key) throws SQLException {
		if (key < 0) throw new IllegalArgumentException("Invalid key: " + key);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getDeleteQuery());
			ps.setInt(1, key);
			
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to delete " + key);
			}
		}
		finally {
			if (ps != null) {
				ps.close();
			}
		}
		
	}
	
	protected void update(Connection conn, int key, String name, String description) throws SQLException {
		if (key < 0) throw new IllegalArgumentException("Invalid key: " + key);
		if (name == null) throw new NullPointerException("name cannot be null");
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(getUpdateQuery());
			ps.setString(1, name);
			ps.setString(2, (description == null ? "" : description));
			ps.setInt(3, key);
			
			int count = ps.executeUpdate();
			if (count < 1) {
				throw new SQLException("Failed to update for " + key + " with " + name);
			}
		}
		finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
	
	protected abstract String getInsertQuery();
	
	protected abstract String getDeleteQuery();
	
	protected abstract String getUpdateQuery();
}

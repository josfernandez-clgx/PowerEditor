package com.mindbox.pe.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.db.updaters.AbstractUpdater;

/**
 * Required to be implemented by all Updaters that update types which reference a DateSynonym.
 */
public abstract class DateSynonymReferenceUpdater extends AbstractUpdater {
	public DateSynonymReferenceUpdater() {
		super();
	}
	
	public DateSynonymReferenceUpdater(Connection conn) {
		super(conn);
	}

	public abstract void replaceDateSynonymReferences(DateSynonym[] toBeReplaced, DateSynonym replacement) throws SQLException;
	
	protected void replaceDateSynonymReferencesInIntersectionTable(DateSynonym[] toBeReplaced, DateSynonym replacement, 
			String effectiveDateUpdateSql, String expirationDateUpdateSql) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> Replacing " + toBeReplaced.length + " Date Synonym refs with " + replacement.getID());
		}

		Connection conn = getConnection();
		PreparedStatement ps = conn.prepareStatement(fillInClause(effectiveDateUpdateSql, toBeReplaced.length));
		ps.setInt(1, replacement.getID());
		for (int i = 0; i < toBeReplaced.length; i++) {
			ps.setInt(i+2, toBeReplaced[i].getID());
		}
		int count = ps.executeUpdate();
		ps.close();
		
		if (logger.isInfoEnabled()) {
			logger.info("Replaced " + count + " effective date synonym references with " + replacement.getID());
		}
		
		ps = conn.prepareStatement(fillInClause(expirationDateUpdateSql, toBeReplaced.length));
		ps.setInt(1, replacement.getID());
		for (int i = 0; i < toBeReplaced.length; i++) {
			ps.setInt(i+2, toBeReplaced[i].getID());
		}
		count = ps.executeUpdate();
		ps.close();
		
		if (logger.isInfoEnabled()) {
			logger.info("Replaced " + count + " expiration date synonym references with " + replacement.getID());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("<<< Replacing Date Synonym refs.");
		}
	}
}

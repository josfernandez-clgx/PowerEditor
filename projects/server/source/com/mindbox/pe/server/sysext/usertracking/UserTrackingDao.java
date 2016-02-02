package com.mindbox.pe.server.sysext.usertracking;

import java.sql.SQLException;
import java.util.Date;

/**
 * DAO for user trackgin data.
 * 
 * @author Geneho Kim
 * @since 5.8.2
 */
public interface UserTrackingDao {

	void enableUserRecord(String userId, String modifiedBy) throws SQLException;

	boolean hasUserTrackingRecord(String userId) throws SQLException;

	boolean isUserDisabled(String userId) throws SQLException;

	void insertUserTrackingData(String userId, String modifiedBy) throws SQLException;

	void updateLastLoginDate(String userId, Date lastLoginDate) throws SQLException;

	void updateLastModifiedDetails(String userId, Date lastModifiedDate, String modifiedBy) throws SQLException;
}

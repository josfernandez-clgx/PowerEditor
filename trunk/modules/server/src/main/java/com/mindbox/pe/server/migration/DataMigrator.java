package com.mindbox.pe.server.migration;

import java.sql.SQLException;

public interface DataMigrator {

	void migrateData(String targetVersion) throws SQLException;
}

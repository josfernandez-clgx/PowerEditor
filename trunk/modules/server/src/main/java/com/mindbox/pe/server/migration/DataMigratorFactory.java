package com.mindbox.pe.server.migration;

import java.util.HashMap;
import java.util.Map;

public class DataMigratorFactory {

	private static final Map<String, DataMigrator> DATA_MIGRATOR_MAP = new HashMap<String, DataMigrator>();

	public static DataMigrator getDataMigrator(final String targetVersion) {
		initInstances();
		return DATA_MIGRATOR_MAP.get(targetVersion);
	}

	private static void initInstances() {
		if (DATA_MIGRATOR_MAP.isEmpty()) {
			DATA_MIGRATOR_MAP.put("5.9.0", new Version590DataMigrator());
			DATA_MIGRATOR_MAP.put("5.9.1", new Version590DataMigrator());
		}
	}

	private DataMigratorFactory() {
	}
}

package com.mindbox.pe.server.spi.db;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 */
public interface PEDataUpdater {

	// 4.5.0 - moved to {@link com.mindbox.pe.server.spi.UserManagementProvider}

	GenericEntityDataUpdater getGenericEntityDataUpdater();
}

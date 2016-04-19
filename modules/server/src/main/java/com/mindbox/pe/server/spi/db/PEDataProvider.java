package com.mindbox.pe.server.spi.db;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.0.0
 */
public interface PEDataProvider  {
	
	// 4.5.0 - moved to {@link com.mindbox.pe.server.spi.UserManagementProvider}
	//UserDataProvider getUserDataProvider();

	/**
	 * Gets generic entity data provider.
	 * @return GenericEntityDataProvider
	 * @since PowerEditor 3.0.0
	 */
	GenericEntityDataProvider getGenericEntityDataProvider();
}

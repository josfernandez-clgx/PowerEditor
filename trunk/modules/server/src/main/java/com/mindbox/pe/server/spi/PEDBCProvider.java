package com.mindbox.pe.server.spi;

import com.mindbox.pe.server.spi.db.PEDataProvider;
import com.mindbox.pe.server.spi.db.PEDataUpdater;

/**
 * PowerEditor data provider.
 * Responsible for managing data storage for all PE data that are maintained on the server.
 * @author Gene Kim
 * @author MindBox
 */
public interface PEDBCProvider {
	
	PEDataProvider getDataProvider();
	
	PEDataUpdater getDataUpdater();
	
	PEDBCProvider createInstance();
	
	GuidelineRuleProvider getGuidelineRuleProvider();
	
	UserManagementProvider getUserManagementProvider();
}

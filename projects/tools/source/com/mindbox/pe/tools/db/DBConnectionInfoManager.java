/*
 * Created on 2004. 12. 17.
 *
 */
package com.mindbox.pe.tools.db;


/**
 * Manages DB connections.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 * @see com.mindbox.pe.tools.db.DBConnInfo
 */
public interface DBConnectionInfoManager {

	/**
	 * Gets the currently selected DB connection info.
	 * @return selected DB conn info; <code>null</code>
	 */
	DBConnInfo getSelectedDBConnInfo();
}
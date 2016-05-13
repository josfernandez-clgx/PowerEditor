/*
 * Created on 2005. 8. 9.
 *
 */
package com.mindbox.pe.client;


/**
 * Preference Manager for Clients.
 * @author Geneho Kim
 * @since PowerEditor 4.3.6
 */
public interface PreferenceManager {

	/**
	 * Retrieves grid column width for the specified template id.
	 * @param templateID id of the template 
	 * @return column widths as int array, if found; <code>null</code>, otherwise
	 */
	int[] getStoredGridColumnWidths(int templateID);
	
	/**
	 * Stores grid column width for the specified template id.
	 * @param templateID id of the template
	 * @param columnWidths column widths to store
	 */
	void storeGridColumnWidths(int templateID, int[] columnWidths);
	
	/**
	 * Clears column width cache for the template id.
	 * @param templateID id of the template
	 */
	void clearGridColumnWidths(int templateID);
}

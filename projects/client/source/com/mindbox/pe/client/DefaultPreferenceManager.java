/*
 * Created on 2005. 8. 9.
 *
 */
package com.mindbox.pe.client;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Geneho Kim
 * @since PowerEditor 4.3.6
 */
class DefaultPreferenceManager implements PreferenceManager {

	private static DefaultPreferenceManager instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	static DefaultPreferenceManager getInstance() {
		if (instance == null) {
			instance = new DefaultPreferenceManager();
		}
		return instance;
	}


	private final Map<Integer, int[]> columnWidthMap;

	private DefaultPreferenceManager() {
		columnWidthMap = new HashMap<Integer, int[]>();
	}

	public int[] getStoredGridColumnWidths(int templateID) {
		synchronized (columnWidthMap) {
			return columnWidthMap.get(new Integer(templateID));
		}
	}

	public void storeGridColumnWidths(int templateID, int[] columnWidths) {
		synchronized (columnWidthMap) {
			columnWidthMap.put(new Integer(templateID), columnWidths);
		}
	}

	public void clearGridColumnWidths(int templateID) {
		synchronized (columnWidthMap) {
			Integer key = new Integer(templateID);
			if (columnWidthMap.containsKey(key)) {
				columnWidthMap.remove(key);
			}
		}
	}

}

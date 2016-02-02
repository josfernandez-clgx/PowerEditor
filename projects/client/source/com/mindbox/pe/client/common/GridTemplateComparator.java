package com.mindbox.pe.client.common;

import java.util.Comparator;

import com.mindbox.pe.model.GridTemplate;


/**
 * @author Geneho Kim

 * @since PowerEditor 
 */
public class GridTemplateComparator implements Comparator<GridTemplate> {

	private static GridTemplateComparator instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static GridTemplateComparator getInstance() {
		if (instance == null) {
			instance = new GridTemplateComparator();
		}
		return instance;
	}

	private GridTemplateComparator() {
	}

	public int compare(GridTemplate arg0, GridTemplate arg1) {
		if (arg0 == arg1) return 0;
		int result = arg0.getName().compareTo(arg1.getName());
		if (result == 0) {
			if (arg0.getVersion() == null) {
				return (arg1.getVersion() == null ? 0 : 1);
			}
			else {
				return arg0.getVersion().compareTo(arg1.getVersion());
			}
		}
		else {
			return result;
		}
	}

}
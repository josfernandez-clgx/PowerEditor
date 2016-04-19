/*
 * Created on 2004. 12. 22.
 *
 */
package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.DateSynonym;


/**
 * Date Synonym comparator.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class DateSynonymComparator implements Comparator<DateSynonym> {

	private static DateSynonymComparator instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static DateSynonymComparator getInstance() {
		if (instance == null) {
			instance = new DateSynonymComparator();
		}
		return instance;
	}

	private DateSynonymComparator() {
	}
	
	public int compare(DateSynonym obj1, DateSynonym obj2) {
		if (obj1 == obj2) {
			return 0;
		}
		if (obj1 == null && obj2 != null) {
			return 1;
		}
		else if (obj1 != null && obj2 == null) {
			return -1;
		}
		else {
			return obj1.getName().compareTo(obj2.getName());
		}
	}

}

package com.mindbox.pe.model.comparator;

import java.util.Comparator;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class GenericObjectComparator implements Comparator<Object> {

	private static GenericObjectComparator instance = null;

	public static GenericObjectComparator getInstance() {
		if (instance == null) {
			instance = new GenericObjectComparator();
		}
		return instance;
	}
	
	private GenericObjectComparator() {
	}
	
	public int compare(Object obj1, Object obj2) {
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
			return obj1.toString().compareTo(obj2.toString());
		}
	}

}

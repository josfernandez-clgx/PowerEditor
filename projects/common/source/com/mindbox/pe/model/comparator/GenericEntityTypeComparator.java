package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.GenericEntityType;

/**
 * Comparator for {@link com.mindbox.pe.model.GenericEntityType} instances.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.5.0
 */
public class GenericEntityTypeComparator implements Comparator<GenericEntityType> {

	private static GenericEntityTypeComparator instance = null;

	public static GenericEntityTypeComparator getInstance() {
		if (instance == null) {
			instance = new GenericEntityTypeComparator();
		}
		return instance;
	}
	
	
	private GenericEntityTypeComparator() {
	}


	public boolean equals(Object obj) {
		return obj instanceof GenericEntityTypeComparator;
	}
	
	public int compare(GenericEntityType obj1, GenericEntityType obj2) {
		if (obj1 == obj2 || obj1.getDisplayName() == obj2.getDisplayName()) {
			return 0;
		}
		else {
			return obj1.getDisplayName().compareTo(obj2.getDisplayName());
		}
	}
}

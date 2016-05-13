package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.TypeEnumValue;

/**
 * Comparator for {@link TypeEnumValue} instances that sorts by display label.
 * This compares name field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.3.1
 */
public class TypeEnumValueComparator implements Comparator<TypeEnumValue> {

	private static TypeEnumValueComparator instance = null;

	public static TypeEnumValueComparator getInstance() {
		if (instance == null) {
			instance = new TypeEnumValueComparator();
		}
		return instance;
	}
	
	private TypeEnumValueComparator() {
	}
	
	public boolean equals(Object obj) {
		return obj instanceof TypeEnumValueComparator;
	}
	
	public int compare(TypeEnumValue obj1, TypeEnumValue obj2) {
		if (obj1 == obj2 || obj1.getID() == obj2.getID()) {
			return 0;
		}
		else {
			return obj1.getDisplayLabel().compareTo(obj2.getDisplayLabel());
		}
	}
}

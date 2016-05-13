package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.IDNameObject;

/**
 * Comparator for {@link IDNameObject} instances.
 * This compares name field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public class IDNameObjectComparator<T extends IDNameObject> implements Comparator<T> {

	public boolean equals(Object obj) {
		return obj instanceof IDNameObjectComparator;
	}

	public int compare(T obj1, T obj2) {
		if (obj1 == obj2 || obj1.getID() == obj2.getID()) {
			return 0;
		}
		else {
			return obj1.getName().compareTo(obj2.getName());
		}
	}
}

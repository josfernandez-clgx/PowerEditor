package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.AbstractIDObject;

/**
 * Comparator for {@link com.mindbox.pe.model.AbstractIDObject} instances.
 * This compares name field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.20
 */
public class IDObjectComparator<T extends AbstractIDObject> implements Comparator<T> {

	public boolean equals(Object obj) {
		return obj instanceof IDObjectComparator;
	}

	public int compare(AbstractIDObject obj1, AbstractIDObject obj2) {
		if (obj1 == obj2) return 0;
		return obj1.getID() - obj2.getID();
	}
}

package com.mindbox.pe.client.common.filter;

import java.util.Comparator;

import com.mindbox.pe.model.filter.PersistentFilterSpec;

/**
 * Comparator for filters.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class FiltersComparator<T extends PersistentFilterSpec> implements Comparator<T> {

	public FiltersComparator() {
	}

	public int compare(PersistentFilterSpec obj, PersistentFilterSpec obj1) throws ClassCastException {
		if (obj == obj1) return 0;
		return obj.getName().compareTo(obj1.getName());
	}
}

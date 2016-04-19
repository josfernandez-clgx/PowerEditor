package com.mindbox.pe.model.comparator;

import java.util.Comparator;

import com.mindbox.pe.model.GenericCategory;

public class GenericCategoryComparator implements Comparator<GenericCategory> {

	public static GenericCategoryComparator getSortByNameInstance() {
		if (nameInstance == null) {
			nameInstance = new GenericCategoryComparator(SORT_BY_NAME);
		}
		return nameInstance;
	}

	public static GenericCategoryComparator getSortBySortOrderInstance() {
		if (sortOrderInstance == null) {
			sortOrderInstance = new GenericCategoryComparator(SORT_BY_SORT_ORDER);
		}
		return sortOrderInstance;
	}

	private static GenericCategoryComparator nameInstance = null;
	private static GenericCategoryComparator sortOrderInstance = null;
	private static final int SORT_BY_NAME = 1;
	private static final int SORT_BY_SORT_ORDER = 2;

	private final int sortBy;

	private GenericCategoryComparator(int sortBy) {
		this.sortBy = sortBy;
	}

	public int compare(GenericCategory category1, GenericCategory category2) {
		if (category1 == category2) return 0;
		switch (sortBy) {
		case SORT_BY_SORT_ORDER:
			return new Integer(category1.getSortOrderIndex()).compareTo(new Integer(category2.getSortOrderIndex()));
		default:
			return category1.getName().compareTo(category2.getName());
		}
	}
}

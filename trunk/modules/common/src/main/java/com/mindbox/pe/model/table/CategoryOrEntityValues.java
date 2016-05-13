package com.mindbox.pe.model.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryOrEntityValues extends EnumValues<CategoryOrEntityValue> {

	private static final long serialVersionUID = 20070515000011L;

	public static CategoryOrEntityValues parseCategoryOrEntityValues(String s, String entityType, boolean allowEntity, boolean allowCategory) {
		EnumValues<String> enumValues = parseValue(s, false, null);
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		for (int i = 0; i < enumValues.size(); i++) {
			CategoryOrEntityValue value = CategoryOrEntityValue.valueOf(enumValues.get(i), entityType, allowEntity, allowCategory);
			if (value != null) {
				values.add(value);
			}
		}
		values.setSelectionExclusion(enumValues.isSelectionExclusion());
		return values;
	}

	public CategoryOrEntityValues() {
	}

	private CategoryOrEntityValues(CategoryOrEntityValues source) {
		super(source);
	}

	public GridCellValue copy() {
		return new CategoryOrEntityValues(this);
	}

	private int[] getIDs(boolean forEntity) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size(); i++) {
			CategoryOrEntityValue value = this.get(i);
			if (value.isForEntity() == forEntity) {
				list.add(value.getId());
			}
		}
		int[] ids = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ids[i] = list.get(i);
		}
		return ids;
	}

	/**
	 * @return int[] containing entityIDs
	 */
	public int[] getCategoryIDs() {
		return getIDs(false);
	}

	/**
	 * @return int[] containing categoryIDs
	 */
	public int[] getEntityIDs() {
		return getIDs(true);
	}

	public boolean hasID(boolean forEntity, int id) {
		for (int i = 0; i < size(); i++) {
			CategoryOrEntityValue value = this.get(i);
			if (value.isForEntity() == forEntity && value.getId() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This returns <code>false</code> if this is empty.
	 * @return <code>true</code> if this has at least one reference to generic category; <code>false</code>, otherwise
	 */
	public boolean hasGenericCategoryReference() {
		for (int i = 0; i < size(); i++) {
			CategoryOrEntityValue value = this.get(i);
			if (!value.isForEntity()) {
				return true;
			}
		}
		return false;
	}

	public void removeEntityID(int id) {
		for (Iterator<CategoryOrEntityValue> iter = this.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = iter.next();
			if (element.isForEntity() && element.getId() == id) {
				iter.remove();
			}
		}
	}

	public void removeCategoryID(int id) {
		for (Iterator<CategoryOrEntityValue> iter = this.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = iter.next();
			if (!element.isForEntity() && element.getId() == id) {
				iter.remove();
			}
		}
	}
}

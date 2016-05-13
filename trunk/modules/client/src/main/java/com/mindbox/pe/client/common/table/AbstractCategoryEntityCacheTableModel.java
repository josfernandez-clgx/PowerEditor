package com.mindbox.pe.client.common.table;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.ui.AbstractSelectionTableModel;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * Model for Guideline report table. This caches channels, investors, products, and categories for
 * displaying names of them.
 * 
 * @since PowerEditor 2.1.0
 */
public abstract class AbstractCategoryEntityCacheTableModel<T extends AbstractIDObject> extends AbstractSelectionTableModel<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	protected AbstractCategoryEntityCacheTableModel(String... columnNames) {
		super(columnNames);
	}

	protected final CategoryOrEntityValues getGenericEntityContext(GenericEntityType type, GuidelineContext[] context) {
		if (context == null) return null;
		for (int i = 0; i < context.length; i++) {
			if (context[i].getGenericEntityType() == type || type == ClientUtil.getEntityConfigHelper().findEntityTypeForCategoryType(context[i].getGenericCategoryType())) {
				CategoryOrEntityValues values = new CategoryOrEntityValues();
				int[] ids = context[i].getIDs();
				if (ids != null && ids.length > 0) {
					for (int j = 0; j < ids.length; j++) {
						values.add(new CategoryOrEntityValue(type, (context[i].getGenericCategoryType() <= 0), ids[j]));
					}
				}
				return values;
			}
		}
		return null;
	}

}

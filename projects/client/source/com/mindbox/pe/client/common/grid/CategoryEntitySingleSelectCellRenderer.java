package com.mindbox.pe.client.common.grid;

import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

/**
 * Grid cell renderer for single select entity columns.
 * 
 * @author Geneho Kim
 * @since 4.5.0
 */
public class CategoryEntitySingleSelectCellRenderer extends AbstractCategoryEntityCellRenderer {

	public CategoryEntitySingleSelectCellRenderer(ColumnDataSpecDigest dataSpecDigest) {
		super(dataSpecDigest);
	}

	protected String getRendererTextAndSetIcon(Object value) {
		CategoryOrEntityValue categoryOrEntityValue = null;
		if (value != null) {
			if (value instanceof CategoryOrEntityValue) {
				categoryOrEntityValue = (CategoryOrEntityValue) value;
			}
			else if (value instanceof String) {
				categoryOrEntityValue = CategoryOrEntityValue.valueOf(
						(String) value,
						dataSpecDigest.getType(),
						dataSpecDigest.isEntityAllowed(),
						dataSpecDigest.isCategoryAllowed());
			}
		}
		setIcon(categoryOrEntityValue == null ? null : categoryOrEntityValue.isForEntity() ? entityIcon : categoryIcon);
		return (categoryOrEntityValue == null ? null : getDisplayValue(categoryOrEntityValue));
	}
	
}

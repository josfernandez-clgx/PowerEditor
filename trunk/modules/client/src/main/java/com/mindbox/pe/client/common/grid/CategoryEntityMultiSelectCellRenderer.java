package com.mindbox.pe.client.common.grid;

import java.util.Iterator;

import javax.swing.Icon;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * Grid cell render for multi-select entity or category columns.
 * @author Geneho Kim
 *
 */
public class CategoryEntityMultiSelectCellRenderer extends AbstractCategoryEntityCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static String getDisplayValue(CategoryOrEntityValues values, boolean includeNot, boolean includeHtml) {
		return values == null || values.isEmpty() ? null : getValueString(values, includeNot, includeHtml);
	}

	private static String getValueString(CategoryOrEntityValues values, boolean includeNot, boolean includeHtml) {
		StringBuilder buff = new StringBuilder();

		if (values.isSelectionExclusion() && includeNot) {
			// This html is only used by RuleTreeRendererColRefValue, which isn't even in the grid package
			// It'd probably be better to move this out of here and remove the includeHtml param.   But for now
			// I'm just refactoring, so leaving as is.
			buff.append(includeHtml ? "<html><body><font color='red'><b>NOT</b></font>&nbsp;" : "NOT ");
		}
		for (Iterator<CategoryOrEntityValue> iter = values.iterator(); iter.hasNext();) {
			CategoryOrEntityValue element = (CategoryOrEntityValue) iter.next();
			buff.append(CategoryEntitySingleSelectCellRenderer.getDisplayValue(element));
			if (iter.hasNext()) buff.append(',');
		}
		if (includeHtml && values.isSelectionExclusion() && includeNot) {
			buff.append("</body></html>");
		}
		return buff.toString();
	}

	public CategoryEntityMultiSelectCellRenderer(ColumnDataSpecDigest dataSpecDigest) {
		super(dataSpecDigest);
	}

	protected String getRendererTextAndSetIcon(Object value) {
		if (value == null) {
			setIcon(null);
			return null;
		}

		CategoryOrEntityValues entityValues = null;
		if (value instanceof CategoryOrEntityValues) {
			entityValues = (CategoryOrEntityValues) value;
		}
		else if (value instanceof String) {
			entityValues = CategoryOrEntityValues.parseCategoryOrEntityValues((String) value, dataSpecDigest.getEntityType(), dataSpecDigest.isEntityAllowed(), dataSpecDigest.isCategoryAllowed());
		}

		setIcon(getIcon(entityValues));
		return (entityValues == null || entityValues.isEmpty() ? null : getDisplayValue(entityValues, false, false));
	}

	public Icon getIcon(CategoryOrEntityValues catEntVals) {
		Icon icon = null;
		if (catEntVals == null || catEntVals.isEmpty()) {
			icon = null;
		}
		else {
			boolean isSelectionExclusion = catEntVals.isSelectionExclusion();
			if (!UtilBase.isEmpty(catEntVals.getCategoryIDs()) && !UtilBase.isEmpty(catEntVals.getEntityIDs())) {
				icon = ClientUtil.getInstance().makeImageIcon(isSelectionExclusion ? "image.not.category.and.entity" : "image.node.category.and.entity");
			}
			else if (!UtilBase.isEmpty(catEntVals.getCategoryIDs())) {
				icon = ClientUtil.getInstance().makeImageIcon(isSelectionExclusion ? "image.not.category" : "image.node.category");
			}
			else if (!UtilBase.isEmpty(catEntVals.getEntityIDs())) {
				icon = ClientUtil.getInstance().makeImageIcon(isSelectionExclusion ? "image.not.entity" : "image.node.entity");
			}

		}

		return icon;
	}
}
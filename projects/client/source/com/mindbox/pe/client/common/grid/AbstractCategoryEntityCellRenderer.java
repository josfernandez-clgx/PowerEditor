package com.mindbox.pe.client.common.grid;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

/**
 * Grid cell renderer for single select entity columns.
 * 
 * @author Geneho Kim
 * @since 4.5.0
 */
public abstract class AbstractCategoryEntityCellRenderer extends DefaultTableCellRenderer {

	public static String getDisplayValue(CategoryOrEntityValue value) {
		if (value == null)
			return null;
		if (value.isForEntity()) {
			return EntityModelCacheFactory.getInstance().getGenericEntityName(value.getEntityType(), value.getId(), "");
		}
		else {
			return EntityModelCacheFactory.getInstance().getGenericCategoryName(value.getEntityType(), value.getId(), "");
		}
	}

	protected final ColumnDataSpecDigest dataSpecDigest;
	protected final ImageIcon categoryIcon = ClientUtil.getInstance().makeImageIcon("image.node.category");
	protected final ImageIcon entityIcon = ClientUtil.getInstance().makeImageIcon("image.node.entity");

	public AbstractCategoryEntityCellRenderer(ColumnDataSpecDigest dataSpecDigest) {
		this.dataSpecDigest = dataSpecDigest;
	}

	protected abstract String getRendererTextAndSetIcon(Object value);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		return super.getTableCellRendererComponent(table, getRendererTextAndSetIcon(value), isSelected, hasFocus, row, col);
	}

}

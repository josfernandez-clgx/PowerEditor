package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.common.grid.CategoryEntityMultiSelectCellRenderer;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * Table cell renderer for {@link com.mindbox.pe.model.table.CategoryOrEntityValues}.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.2.0
 */
public final class CategoryOrEntityValuesTabelCellRenderer extends JLabel implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public CategoryOrEntityValuesTabelCellRenderer() {
		super();
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable arg0, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof CategoryOrEntityValues) {
			CategoryOrEntityValues catEntVals = (CategoryOrEntityValues) value;
			setIcon(catEntVals.isEmpty() ? null : getIcon(catEntVals));
			setText(CategoryEntityMultiSelectCellRenderer.getDisplayValue(catEntVals, false, false));
		}
		else {
			setIcon(null);
			setText(null);
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

	private Icon getIcon(CategoryOrEntityValues catEntVals) {
		boolean forEntity = ((CategoryOrEntityValue) catEntVals.getEnumValue(0)).isForEntity();
		boolean isSelectionExclusion = catEntVals.isSelectionExclusion();

		if (forEntity) {
			return ClientUtil.getInstance().makeImageIcon(isSelectionExclusion ? "image.not.entity" : "image.node.entity");
		}
		else { // for category
			return ClientUtil.getInstance().makeImageIcon(isSelectionExclusion ? "image.not.category" : "image.node.category");
		}
	}
}

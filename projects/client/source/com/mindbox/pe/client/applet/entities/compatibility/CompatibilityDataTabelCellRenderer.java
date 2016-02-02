/*
 * Created on Jan 6, 2004
 */
package com.mindbox.pe.client.applet.entities.compatibility;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.2.0
 */
public final class CompatibilityDataTabelCellRenderer extends JLabel implements TableCellRenderer {

	public CompatibilityDataTabelCellRenderer() {
		super();
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(
		JTable arg0,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column) {
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		setText(value.toString());
		return this;
	}

}

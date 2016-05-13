/*
 * Created on Jun 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.2.0
 */
public class IDNameObjectTableCellRenderer extends JLabel implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final ImageIcon icon;

	public IDNameObjectTableCellRenderer(String imageKey) {
		if (imageKey != null) {
			icon = ClientUtil.getInstance().makeImageIcon(imageKey);
		}
		else {
			icon = null;
		}
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable arg0, Object value, boolean isSelected, boolean isFocus, int arg4, int arg5) {
		if (value == null || value.toString().length() == 0) {
			setText("");
			setIcon(null);
		}
		else if (value instanceof IDNameObject) {
			this.setText(((IDNameObject) value).getName());
			setIcon(icon);
		}
		else {
			this.setText(value.toString());
			setIcon(icon);
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

}

package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.TypeEnumValue;

/**
 * List cell renderer for {@link com.mindbox.pe.model.TypeEnumValue} objects.
 * @author Gene Kim
 * @since PowerEditor 4.3.1
 */
public class TypeEnumValueCellRenderer extends JLabel implements ListCellRenderer {


	public TypeEnumValueCellRenderer(String imageKey) {
		if (imageKey != null) {
			setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
		}
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else if (value instanceof TypeEnumValue) {
			this.setText(((TypeEnumValue) value).getDisplayLabel());
		}
		else {
			this.setText(value.toString());
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

}
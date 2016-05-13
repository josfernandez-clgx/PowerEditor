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
public class TypeEnumValueCellRenderer extends JLabel implements ListCellRenderer<TypeEnumValue> {
	private static final long serialVersionUID = -3951228734910107454L;


	public TypeEnumValueCellRenderer(String imageKey) {
		if (imageKey != null) {
			setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
		}
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends TypeEnumValue> arg0, TypeEnumValue value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else {
			this.setText(value.getDisplayLabel());
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}
}
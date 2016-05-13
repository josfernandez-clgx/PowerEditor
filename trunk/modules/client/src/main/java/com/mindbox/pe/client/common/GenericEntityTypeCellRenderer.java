package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.GenericEntityType;

/**
 * List Cell renderer for {@link GenericEntityType}.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public class GenericEntityTypeCellRenderer extends JLabel implements ListCellRenderer<GenericEntityType> {

	private static final long serialVersionUID = -3951228734910107454L;

	public GenericEntityTypeCellRenderer(String imageKey) {
		if (imageKey != null) {
			setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
		}
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends GenericEntityType> arg0, GenericEntityType value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else {
			this.setText(ClientUtil.getInstance().getLabel(value));
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

}

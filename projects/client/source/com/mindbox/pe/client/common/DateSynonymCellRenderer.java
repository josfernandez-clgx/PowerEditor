package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.DateSynonym;

/**
 * Date synonym cell renderer.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class DateSynonymCellRenderer extends JLabel implements ListCellRenderer {

	private boolean showName = true;

	public DateSynonymCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else if (value instanceof DateSynonym) {
			this.setText((showName ? ((DateSynonym) value).getName() : UIConfiguration.FORMAT_DATE_TIME_SEC.format(((DateSynonym) value).getDate())));
		}
		else {
			this.setText(value.toString());
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}

}

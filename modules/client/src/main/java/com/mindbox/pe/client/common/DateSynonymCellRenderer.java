package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;

/**
 * Date synonym cell renderer.
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class DateSynonymCellRenderer extends JLabel implements ListCellRenderer<DateSynonym> {

	private static final long serialVersionUID = -3951228734910107454L;

	private boolean showName = true;

	public DateSynonymCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends DateSynonym> arg0, DateSynonym value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else {
			this.setText((showName ? value.getName() : Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(value.getDate())));
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}
}

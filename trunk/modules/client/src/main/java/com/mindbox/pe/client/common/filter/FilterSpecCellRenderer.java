package com.mindbox.pe.client.common.filter;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.model.filter.PersistentFilterSpec;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class FilterSpecCellRenderer extends JLabel implements ListCellRenderer<PersistentFilterSpec> {
	private static final long serialVersionUID = -3951228734910107454L;

	@Override
	public Component getListCellRendererComponent(JList<? extends PersistentFilterSpec> arg0, PersistentFilterSpec value, int arg2, boolean arg3, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else {
			this.setText(value.getName());
		}
		return this;
	}
}

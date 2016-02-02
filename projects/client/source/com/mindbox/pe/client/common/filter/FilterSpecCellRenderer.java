package com.mindbox.pe.client.common.filter;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.model.filter.AbstractPersistentFilterSpec;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class FilterSpecCellRenderer extends JLabel implements ListCellRenderer {

	public Component getListCellRendererComponent(JList arg0, Object value, int arg2, boolean arg3, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else if (value instanceof AbstractPersistentFilterSpec) {
			this.setText(((AbstractPersistentFilterSpec<?>)value).getName());
		}
		else {
			this.setText(value.toString());
		}
		return this;
	}

}

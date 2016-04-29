/*
 * Created on Jun 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameObjectCellRenderer<T extends IDNameObject> extends JLabel implements ListCellRenderer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public IDNameObjectCellRenderer(String imageKey) {
		if (imageKey != null) {
			setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
		}
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> arg0, T value, int index, boolean isSelected, boolean arg4) {
		if (value == null) {
			setText("");
		}
		else {
			this.setText(value.getName());
		}
		setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
		return this;
	}

}

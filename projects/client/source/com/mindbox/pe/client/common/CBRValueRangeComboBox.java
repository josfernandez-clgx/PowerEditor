/*
 * Created on Jun 30, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRValueRange;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CBRValueRangeComboBox extends JComboBox {

	private static class CBRValueRangeCellRenderer extends JLabel implements ListCellRenderer {

		public CBRValueRangeCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
			if (value == null) {
				setText("");
			}
			else if (value instanceof CBRValueRange) {
				this.setText(((CBRValueRange) value).getName());
			}
			else {
				this.setText(value.toString());
			}
			setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
			return this;
		}
	}

	public static CBRValueRangeComboBox createInstance() throws ServerException {
		return new CBRValueRangeComboBox(ClientUtil.fetchAllCBRValueRanges());
	}

	private CBRValueRangeComboBox(CBRValueRange[] valueRanges) {
		super(valueRanges);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRValueRangeCellRenderer(null));
	}


	public CBRValueRange getSelectedCBRValueRange() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRValueRange) {
			return (CBRValueRange) obj;
		}
		else {
			return null;
		}
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public void selectCBRValueRange(CBRValueRange valueRange) {
		if (valueRange == null) return;
		setSelectedItem(valueRange);
	}
}
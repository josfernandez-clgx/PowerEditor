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
import com.mindbox.pe.model.cbr.CBRValueRange;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CBRValueRangeComboBox extends JComboBox<CBRValueRange> {

	private static class CBRValueRangeCellRenderer extends JLabel implements ListCellRenderer<CBRValueRange> {

		private static final long serialVersionUID = -3951228734910107454L;

		public CBRValueRangeCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CBRValueRange> arg0, CBRValueRange value, int index, boolean isSelected, boolean arg4) {
			if (value == null) {
				setText("Any");
			}
			else {
				this.setText(value.getName());
			}
			setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
			return this;
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;

	public static CBRValueRangeComboBox createInstance() throws ServerException {
		return new CBRValueRangeComboBox(ClientUtil.fetchAllCBRValueRanges());
	}

	private CBRValueRangeComboBox(CBRValueRange[] valueRanges) {
		super(valueRanges);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRValueRangeCellRenderer(null));
	}

	public void clearSelection() {
		setSelectedIndex(0);
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

	public void selectCBRValueRange(CBRValueRange valueRange) {
		if (valueRange == null) return;
		setSelectedItem(valueRange);
	}
}
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
import com.mindbox.pe.model.cbr.CBRScoringFunction;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CBRScoringFunctionComboBox extends JComboBox<CBRScoringFunction> {

	private static class CBRScoringFunctionCellRenderer extends JLabel implements ListCellRenderer<CBRScoringFunction> {

		private static final long serialVersionUID = -3951228734910107454L;

		public CBRScoringFunctionCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CBRScoringFunction> arg0, CBRScoringFunction value, int index, boolean isSelected, boolean arg4) {
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

	private static final long serialVersionUID = -3951228734910107454L;

	public static CBRScoringFunctionComboBox createInstance() throws ServerException {
		return new CBRScoringFunctionComboBox(ClientUtil.fetchAllCBRScoringFunctions());
	}

	private CBRScoringFunctionComboBox(CBRScoringFunction[] scoringFunctions) {
		super(scoringFunctions);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRScoringFunctionCellRenderer(null));
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public CBRScoringFunction getSelectedCBRScoringFunction() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRScoringFunction) {
			return (CBRScoringFunction) obj;
		}
		else {
			return null;
		}
	}

	public void selectCBRScoringFunction(CBRScoringFunction scoringFunction) {
		if (scoringFunction == null) {
			return;
		}
		setSelectedItem(scoringFunction);
	}
}
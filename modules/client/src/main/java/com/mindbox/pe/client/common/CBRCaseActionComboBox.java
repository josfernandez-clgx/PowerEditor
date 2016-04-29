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
import com.mindbox.pe.model.cbr.CBRCaseAction;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CBRCaseActionComboBox extends JComboBox<CBRCaseAction> {

	private static class CBRCaseActionCellRenderer extends JLabel implements ListCellRenderer<CBRCaseAction> {
		private static final long serialVersionUID = -3951228734910107454L;

		public CBRCaseActionCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CBRCaseAction> arg0, CBRCaseAction value, int index, boolean isSelected, boolean arg4) {
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

	public static CBRCaseActionComboBox createInstance() throws ServerException {
		return new CBRCaseActionComboBox(ClientUtil.fetchAllCBRCaseActions());
	}

	private CBRCaseActionComboBox(CBRCaseAction[] caseActions) {
		super(caseActions);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRCaseActionCellRenderer(null));
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public CBRCaseAction getSelectedCBRCaseAction() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRCaseAction) {
			return (CBRCaseAction) obj;
		}
		else {
			return null;
		}
	}

	public void selectCBRCaseAction(CBRCaseAction caseAction) {
		if (caseAction == null) return;
		setSelectedItem(caseAction);
	}
}
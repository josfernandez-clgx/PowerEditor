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
import com.mindbox.pe.model.cbr.CBRCaseClass;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CBRCaseClassComboBox extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static class CBRCaseClassCellRenderer extends JLabel implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		public CBRCaseClassCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
			if (value == null) {
				setText("");
			}
			else if (value instanceof CBRCaseClass) {
				this.setText(((CBRCaseClass) value).getName());
			}
			else {
				this.setText(value.toString());
			}
			setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
			return this;
		}
	}

	public static CBRCaseClassComboBox createInstance() throws ServerException {
		return new CBRCaseClassComboBox(ClientUtil.fetchAllCBRCaseClasses());
	}

	private CBRCaseClassComboBox(CBRCaseClass[] caseClasses) {
		super(caseClasses);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRCaseClassCellRenderer(null));
	}


	public CBRCaseClass getSelectedCBRCaseClass() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRCaseClass) {
			return (CBRCaseClass) obj;
		}
		else {
			return null;
		}
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public void selectCBRCaseClass(CBRCaseClass caseClass) {
		if (caseClass == null) return;
		setSelectedItem(caseClass);
	}
}
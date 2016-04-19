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
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttributeType;

/**
 * @author pklerk
 * @author MindBox, LLC
 * @since PowerEditor 4.1.0
 */
public final class CBRAttributeTypeComboBox extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static class CBRAttributeTypeCellRenderer extends JLabel implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		public CBRAttributeTypeCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
			if (value == null) {
				setText("");
			}
			else if (value instanceof CBRAttributeType) {
				this.setText(((CBRAttributeType) value).getName());
			}
			else {
				this.setText(value.toString());
			}
			setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
			return this;
		}
	}

	public static CBRAttributeTypeComboBox createInstance() throws ServerException {
		return new CBRAttributeTypeComboBox(ClientUtil.fetchAllCBRAttributeTypes());
	}

	public static CBRAttributeTypeComboBox createInstance(boolean allowNull) throws ServerException {
		if (!allowNull) return createInstance();
		CBRAttributeType[] ats = ClientUtil.fetchAllCBRAttributeTypes();
		Object[] newats = new Object[ats.length + 1];
		newats[0] = "Any";
		for (int i = 0; i < ats.length; i++)
			newats[i + 1] = ats[i];
		return new CBRAttributeTypeComboBox(newats);
	}


	private CBRAttributeTypeComboBox(Object[] attributeTypes) {
		super(attributeTypes);
		UIFactory.setLookAndFeel(this);
		setRenderer(new CBRAttributeTypeCellRenderer(null));
	}


	public CBRAttributeType getSelectedCBRAttributeType() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRAttributeType) {
			return (CBRAttributeType) obj;
		}
		else {
			return null;
		}
	}

	public int getSelectedCBRAttributeTypeID() {
		CBRAttributeType at = this.getSelectedCBRAttributeType();
		return at == null ? Persistent.UNASSIGNED_ID : at.getId();
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public void selectCBRAttributeType(CBRAttributeType attributeType) {
		if (attributeType == null) this.setSelectedIndex(0);
		setSelectedItem(attributeType);
	}

	public void selectCBRAttributeTypeFromID(int id) {
		if (id == Persistent.UNASSIGNED_ID)
			setSelectedIndex(0);
		else {
			for (int i = 1; i < this.getItemCount(); i++) {
				CBRAttributeType at = (CBRAttributeType) this.getItemAt(i);
				if (at.getID() == id) {
					setSelectedItem(at);
					return;
				}
			}
		}
	}
}
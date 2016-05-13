/*
 * Created on Jun 30, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.filter.CBRAttributeSearchFilter;

/**
 * @author pklerk
 * @author MindBox, LLC
 * @since PowerEditor 4.1.0
 */
public final class CBRAttributeComboBox extends JComboBox<CBRAttribute> {

	private static class CBRAttributeCellRenderer extends JLabel implements ListCellRenderer<CBRAttribute> {

		private static final long serialVersionUID = -3951228734910107454L;

		public CBRAttributeCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CBRAttribute> arg0, CBRAttribute value, int index, boolean isSelected, boolean arg4) {
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

	public static CBRAttributeComboBox createInstance() throws ServerException {
		return new CBRAttributeComboBox(false);
	}

	public static CBRAttributeComboBox createInstance(boolean allowNull) throws ServerException {
		return new CBRAttributeComboBox(allowNull);
	}

	private boolean allowNull = false;

	public CBRAttributeComboBox(boolean allowNull) {
		super();
		this.setRenderer(new CBRAttributeCellRenderer(null));
		this.allowNull = allowNull;
	}

	public void clearSelection() {
		setSelectedIndex(0);
	}

	public CBRAttribute getSelectedCBRAttribute() {
		Object obj = super.getSelectedItem();
		if (obj instanceof CBRAttribute) {
			return (CBRAttribute) obj;
		}
		else {
			return null;
		}
	}

	public int getSelectedCBRAttributeID() {
		CBRAttribute at = this.getSelectedCBRAttribute();
		return at == null ? Persistent.UNASSIGNED_ID : at.getId();
	}

	public void populateAttributes(int caseBaseID) {
		try {
			CBRAttributeSearchFilter sf = new CBRAttributeSearchFilter();
			sf.setCaseBaseID(caseBaseID);
			List<CBRAttribute> atList = ClientUtil.getCommunicator().search(sf);
			DefaultComboBoxModel<CBRAttribute> model = (DefaultComboBoxModel<CBRAttribute>) this.getModel();
			model.removeAllElements();
			if (allowNull) {
				model.addElement(null);
			}
			Iterator<CBRAttribute> it = atList.iterator();
			while (it.hasNext()) {
				model.addElement(it.next());
			}
		}
		catch (Exception e) {
			ClientUtil.getLogger().error(e);
		}
	}

	public void selectCBRAttribute(CBRAttribute attribute) {
		if (attribute == null) {
			this.setSelectedIndex(0);
		}
		setSelectedItem(attribute);
	}

	public void selectCBRAttributeFromID(int id) {
		if (id == Persistent.UNASSIGNED_ID) {
			setSelectedIndex(0);
		}
		else {
			for (int i = 1; i < this.getItemCount(); i++) {
				CBRAttribute at = this.getItemAt(i);
				if (at.getID() == id) {
					setSelectedItem(at);
					return;
				}
			}
		}
	}
}
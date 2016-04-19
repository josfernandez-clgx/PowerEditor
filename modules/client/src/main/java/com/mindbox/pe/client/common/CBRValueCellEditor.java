package com.mindbox.pe.client.common;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.cbr.CBRValueRange;

/**
 * CBR value cell editor.
 * @author deklerk
 */
public class CBRValueCellEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static class ValueCellTextField extends JTextField {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;

		protected class ValueCellDocument extends PlainDocument {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3951228734910107454L;

			public void insertString(int i, String s, AttributeSet attributeset) throws BadLocationException {
				if (anythingAllowed) {
					super.insertString(i, s, attributeset);
				}
				else if (floatsAllowed) {
					char ac[] = s.toCharArray();
					char ac1[] = new char[ac.length];
					int j = 0;
					int k = 0;
					for (int l = 0; l < ac.length; l++)
						if (Character.isDigit(ac[l]))
							ac1[j++] = ac[l];
						else if (negativeAllowed && l == 0 && ac[l] == '-' && i == 0) {
							ac1[j++] = ac[l];
						}
						else if (ac[l] == '.' && k <= 1) {
							ac1[j++] = ac[l];
							k++;
						}
					super.insertString(i, new String(ac1, 0, j), attributeset);
				}
				else {
					char ac[] = s.toCharArray();
					char ac1[] = new char[ac.length];
					int j = 0;
					for (int k = 0; k < ac.length; k++)
						if (Character.isDigit(ac[k]) || (negativeAllowed && (k == 0 && ac[k] == '-' && i == 0))) ac1[j++] = ac[k];
					super.insertString(i, new String(ac1, 0, j), attributeset);
				}
			}

			public ValueCellDocument() {
			}
		}

		protected Document createDefaultModel() {
			return new ValueCellDocument();
		}

		public ValueCellTextField(int size) {
			super(size);
		}

		public void setValueRange(CBRValueRange vr) {
			if (vr.isNumericAllowed()) {
				anythingAllowed = false;
				negativeAllowed = vr.isNegativeAllowed();
				floatsAllowed = vr.isFloatAllowed();
			}
			else
				anythingAllowed = true;
		}

		private boolean negativeAllowed = true;
		private boolean floatsAllowed = true;
		private boolean anythingAllowed = true;
	}

	private static class ValueCellComboBox extends JComboBox {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3951228734910107454L;
		ValueCellTextField editField;

		class ValueCellComboBoxEditor extends BasicComboBoxEditor {
			public ValueCellComboBoxEditor() {
				super();
				editField = new ValueCellTextField(15);
			}

			public void setValueRange(CBRValueRange vr) {
				editField.setValueRange(vr);
			}

			public Component getEditorComponent() {
				return editField;
			}

			public Object getItem() {
				return editField.getText();
			}

			public void setItem(Object item) {
				if (item != null)
					editField.setText(item.toString());
				else
					editField.setText("");
			}
		}

		public ValueCellComboBox() {
			super();
			this.setEditor(new ValueCellComboBoxEditor());
		}

		void initForAttribute(CBRAttribute att, String value) {
			if (att == null) return;
			CBRValueRange vr = att.getValueRange();
			((ValueCellComboBoxEditor) this.getEditor()).setValueRange(vr);
			DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
			model.removeAllElements();
			if (vr.isEnumeratedValuesAllowed()) {
				this.setEditable(false);
				List<CBREnumeratedValue> evList = att.getEnumeratedValues();
				Iterator<CBREnumeratedValue> it = evList.iterator();
				while (it.hasNext()) {
					CBREnumeratedValue ev = it.next();
					model.addElement(ev.getName());
				}
				this.setSelectedItem(value);
			}
			else {
				this.setEditable(true);
				((ValueCellComboBoxEditor) this.getEditor()).setItem(value);
			}
		}
	}

	/**
	 * Default constructor.
	 */
	public CBRValueCellEditor() {
		super(new ValueCellComboBox());
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		ValueCellComboBox combo = (ValueCellComboBox) getComponent();
		// TT 2113: get att from server, not from model
		CBRAttribute attribute = (CBRAttribute) table.getModel().getValueAt(row, 0);
		try {
			attribute = (CBRAttribute) ClientUtil.getCommunicator().fetch(attribute.getID(), PeDataType.CBR_ATTRIBUTE, false);
		}
		catch (ServerException e) {
			ClientUtil.getLogger().warn("Failed to get CBR attribute of id " + attribute.getID() + ",name=" + attribute.getName(), e);
		}
		combo.initForAttribute(attribute, (String) value);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

}

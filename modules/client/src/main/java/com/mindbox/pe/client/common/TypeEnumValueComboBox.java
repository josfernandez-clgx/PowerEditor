package com.mindbox.pe.client.common;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.TypeEnumValue;

/**
 * @author Gene Kim
 * @since PowerEditor 4.3.1
 */
public final class TypeEnumValueComboBox extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public TypeEnumValueComboBox(ComboBoxModel model) {
		super();
		UIFactory.setLookAndFeel(this);
		setRenderer(new TypeEnumValueCellRenderer(null));
		setFocusable(true);
		setModel(model);
	}

	public TypeEnumValue getSelectedEnumValueObject() {
		Object obj = super.getSelectedItem();
		if (obj instanceof TypeEnumValue) {
			return (TypeEnumValue) obj;
		}
		else {
			return null;
		}
	}

	public String getSelectedEnumValueValue() {
		Object obj = super.getSelectedItem();
		if (obj instanceof TypeEnumValue) {
			return ((TypeEnumValue) obj).getValue();
		}
		else {
			return null;
		}
	}

	public int getSelectedTypeEnumValueID() {
		Object selected = getSelectedItem();
		if (selected instanceof TypeEnumValue) {
			return ((TypeEnumValue) selected).getID();
		}
		else {
			return -1;
		}
	}

	public final void selectTypeEnumValue(int enumValueID) {
		if (enumValueID == -1) return;
		ComboBoxModel model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof TypeEnumValue) {
				if (((TypeEnumValue) item).getID() == enumValueID) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public final void selectTypeEnumValue(String value) {
		if (value == null) setSelectedIndex(-1);
		ComboBoxModel model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof TypeEnumValue) {
				if (((TypeEnumValue) item).getValue().equals(value)) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}
}
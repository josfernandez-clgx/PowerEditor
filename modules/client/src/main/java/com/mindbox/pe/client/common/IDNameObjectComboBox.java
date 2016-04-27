package com.mindbox.pe.client.common;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameObjectComboBox extends JComboBox<IDNameObject> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	// TODO use an instance with the name of 'Any'
	private static final IDNameObject ANY_INSTANCE = null;

	public IDNameObjectComboBox(boolean hasEmptyItem, ListCellRenderer<IDNameObject> renderer) {
		super();
		if (renderer == null) throw new NullPointerException("renderer cannot be null");
		UIFactory.setLookAndFeel(this);
		if (hasEmptyItem) {
			addItem(ANY_INSTANCE);
		}
		setRenderer(renderer);
		setFocusable(true);
	}

	public IDNameObjectComboBox(boolean hasEmptyItem, String iconKey) {
		this(hasEmptyItem, new IDNameObjectCellRenderer(iconKey));
	}

	public IDNameObject getSelectedIDNameObject() {
		Object obj = super.getSelectedItem();
		if (obj instanceof IDNameObject) {
			return (IDNameObject) obj;
		}
		else {
			return null;
		}
	}

	public int getSelectedObjectID() {
		Object selected = getSelectedItem();
		if (selected instanceof IDNameObject) {
			return ((IDNameObject) selected).getID();
		}
		else {
			return -1;
		}
	}

	public final void selectObject(int objectID) {
		if (objectID == -1) return;
		ComboBoxModel<IDNameObject> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof IDNameObject) {
				if (((IDNameObject) item).getID() == objectID) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public final void selectObject(String name) {
		if (name == null) return;
		ComboBoxModel<IDNameObject> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object item = model.getElementAt(i);
			if (item instanceof IDNameObject) {
				if (((IDNameObject) item).getName().equals(name)) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public void setValueList(List<? extends IDNameObject> objectList) {
		for (IDNameObject object : objectList) {
			addItem(object);
		}
	}


}

package com.mindbox.pe.client.common;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Combo box model that supports refreshing (redrawing) items in the model.
 * @author Geneho Kim
 */
public class RefreshableComboBoxModel<T> extends DefaultComboBoxModel<T> {

	private static final long serialVersionUID = 5662895468259037911L;

	public RefreshableComboBoxModel() {
		super();
	}

	/**
	 * Creates a new instance populated with items in the specified combo box model.
	 * @param model the model to get the items from
	 */
	@SuppressWarnings("unchecked")
	public RefreshableComboBoxModel(ComboBoxModel<T> model) {
		rebuild(model);
		model.addListDataListener(new ListDataListener() {
			@Override
			public void contentsChanged(ListDataEvent e) {
				rebuild((ComboBoxModel<T>) e.getSource());
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				rebuild((ComboBoxModel<T>) e.getSource());
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				rebuild((ComboBoxModel<T>) e.getSource());
			}
		});
	}

	private void rebuild(ComboBoxModel<T> model) {
		Object selectedObject = getSelectedItem();
		removeAllElements();
		for (int i = 0; i < model.getSize(); i++) {
			addElement(model.getElementAt(i));
		}
		setSelectedItem(selectedObject);
	}

	public synchronized void refresh() {
		fireContentsChanged(this, 0, getSize() - 1);
	}
}

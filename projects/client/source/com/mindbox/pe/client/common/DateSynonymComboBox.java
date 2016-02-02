package com.mindbox.pe.client.common;

import java.util.Date;

import javax.swing.JComboBox;

import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.DateSynonym;

/**
 * Date synonym editing combo box.
 * @author deklerk
 */
public class DateSynonymComboBox extends JComboBox {

	public DateSynonymComboBox(boolean hasEmpty, boolean createModel) {
		super();
		setModel(new RefreshableComboBoxModel(createModel ? EntityModelCacheFactory.getInstance().createDateSynonymComboModel(hasEmpty)
				: EntityModelCacheFactory.getInstance().getDateSynonymComboModel(hasEmpty)));
		this.setRenderer(new DateSynonymCellRenderer());
		UIFactory.setLookAndFeel(this);
	}

	void refresh(boolean showName) {
		((DateSynonymCellRenderer) getRenderer()).setShowName(showName);
		((RefreshableComboBoxModel) getModel()).refresh();
	}

	public Date getDate() {
		Object selectedItem = getSelectedItem();

		if ((selectedItem == null) || !(selectedItem instanceof DateSynonym)) {
			return null;
		}

		return ((DateSynonym) selectedItem).getDate();
	}

	public synchronized DateSynonym getValue() {
		Object selectedItem = getSelectedItem();

		if ((selectedItem == null) || !(selectedItem instanceof DateSynonym)) {
			return null;
		}

		return (DateSynonym) selectedItem;
	}

	public synchronized void setDate(Date date) {
		if (date == null) {
			setValue(null);
		}
		else {
			setValue(EntityModelCacheFactory.getInstance().findDateSynonym(date));
		}
	}

	/**
	 * @param dateSynonym
	 * Sets the selected item of the combo box to the value passed
	 * in. If the value is not in the it adds it.
	 */
	public synchronized void setValue(DateSynonym dateSynonym) {
		if (getModel() != null && dateSynonym != null) {
			for (int i = 0; i < getModel().getSize(); i++) {
				Object ds = getModel().getElementAt(i);
				if (ds instanceof DateSynonym && ((DateSynonym) ds).equals(dateSynonym)) {
					getModel().setSelectedItem(dateSynonym);
					return;
				}
			}
			addItem(dateSynonym);
			getModel().setSelectedItem(dateSynonym);
		}
		else {
			setSelectedIndex(-1);
		}
	}
}

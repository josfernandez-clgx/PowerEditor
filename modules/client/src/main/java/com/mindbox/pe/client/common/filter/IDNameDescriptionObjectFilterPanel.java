package com.mindbox.pe.client.common.filter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.filter.NameDescriptionSearchFilter;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameDescriptionObjectFilterPanel<T extends AbstractIDNameDescriptionObject, B extends ButtonPanel> extends IDNameObjectFilterPanel<T, B> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private JTextField descField;

	/**
	 * 
	 * @param selectionPanel
	 * @param filterEntityType
	 * @param hideManagementButtons
	 */
	public IDNameDescriptionObjectFilterPanel(AbstractSelectionPanel<T, B> selectionPanel, PeDataType filterEntityType, boolean hideManagementButtons) {
		super(selectionPanel, filterEntityType, hideManagementButtons);
	}

	protected final String getDescFieldText() {
		return descField.getText();
	}

	protected void clearSearchFields() {
		super.clearSearchFields();
		this.descField.setText("");
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);
		this.descField = new JTextField(10);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.desc.contains"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, descField);
	}

	protected SearchFilter<T> getSearchFilterFromFields() {
		NameDescriptionSearchFilter<T> filter = new NameDescriptionSearchFilter<T>(super.filterEntityType);
		filter.setNameCriterion(getNameFieldText());
		filter.setDescriptionCriterion(descField.getText());
		return filter;
	}

}

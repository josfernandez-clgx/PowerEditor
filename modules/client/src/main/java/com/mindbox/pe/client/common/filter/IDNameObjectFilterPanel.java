package com.mindbox.pe.client.common.filter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.selection.AbstractSelectionPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.filter.NameSearchFilter;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class IDNameObjectFilterPanel<T extends IDNameObject, B extends ButtonPanel> extends AbstractPersistedFilterPanel<T, B> {

	private static final long serialVersionUID = -3951228734910107454L;

	private JTextField nameField;

	public IDNameObjectFilterPanel(AbstractSelectionPanel<T, B> selectionPanel, PeDataType filterEntityType, boolean hideManagementButtons) {
		super(selectionPanel, filterEntityType, hideManagementButtons);
	}

	@Override
	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.nameField = new JTextField(10);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.name.contains"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, nameField);
	}

	@Override
	protected void clearSearchFields() {
		this.nameField.setText("");
	}

	@Override
	public void discardChanges() {
		selectionPanel.discardChangesInDetails();
	}

	protected final String getNameFieldText() {
		return nameField.getText();
	}

	@Override
	protected SearchFilter<T> getSearchFilterFromFields() {
		NameSearchFilter<T> filter = new NameSearchFilter<T>(super.filterEntityType);
		filter.setNameCriterion(nameField.getText());
		return filter;
	}

	// Next three implement default behavior for PowerEditorTabPanel interface
	@Override
	public boolean hasUnsavedChanges() {
		return selectionPanel.hasChangesInDetails();
	}

	@Override
	public boolean panelActionSaveCheck() {
		boolean hasChanges = this.hasUnsavedChanges();
		if (hasChanges) {
			Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
			if (result == null) {
				return false;
			}
			else if (result.booleanValue()) {
				try {
					this.saveChanges();
					return true;
				}
				catch (CanceledException e) {
					return false;
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
					return false;
				}
			}
			else {
				this.discardChanges();
				return true;
			}
		}
		return true;
	}

	@Override
	public void saveChanges() throws CanceledException, ServerException {
		boolean saved = selectionPanel.saveChangesInDetails();
		if (!saved) {
			throw CanceledException.getInstance();
		}
	}

}

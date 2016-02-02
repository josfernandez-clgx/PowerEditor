package com.mindbox.pe.client.common.detail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class IDNameObjectDetailPanel<T extends IDNameObject,B extends ButtonPanel> extends AbstractDetailPanel<T,B> {

	private JTextField nameField;

	/**
	 * @param entityType
	 */
	public IDNameObjectDetailPanel(EntityType entityType) {
		super(entityType);
	}

	protected final String getNameFieldText() {
		return nameField.getText();
	}

	public void clearFields() {
		setForViewOnly(true);
		nameField.setText("");
		this.currentObject = null;
	}

	protected void populateDetails(T object) {
		this.nameField.setText(object.getName());
	}

	public void populateForClone(T object) {
		this.nameField.setText(object.getName());
	}

	protected void setEnabledFields(boolean enabled) {
		nameField.setEnabled(enabled);
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.nameField = new JTextField(10);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.name"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, nameField);

	}

	protected void addDocumentListener(DocumentListener dl) {
		nameField.getDocument().addDocumentListener(dl);
	}

	protected void removeDocumentListener(DocumentListener dl) {
		nameField.getDocument().removeDocumentListener(dl);
	}

	/**
	 * Overwrite to perform other/additional input validation.
	 * That is, validate values of the fields here.
	 * @throws InputValidationException if an input field contains an invalid entry
	 * @since PowerEditor 4.2.0.
	 */
	protected void validateFields() throws InputValidationException {
		if (nameField.getText().equals(""))
			throw new InputValidationException(ClientUtil.getInstance().getMessage("msg.warning.invalid.no.name"));
	}
}

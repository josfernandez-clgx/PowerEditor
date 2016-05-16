package com.mindbox.pe.client.common.detail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.IDNameObject;
import com.mindbox.pe.model.PeDataType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class IDNameObjectDetailPanel<T extends IDNameObject, B extends ButtonPanel> extends AbstractDetailPanel<T, B> {
	private static final long serialVersionUID = -3951228734910107454L;

	private JTextField nameField;

	/**
	 * @param entityType entityType
	 */
	public IDNameObjectDetailPanel(PeDataType entityType) {
		super(entityType);
	}

	@Override
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

	@Override
	public void clearFields() {
		setForViewOnly(true);
		nameField.setText("");
		this.currentObject = null;
	}

	protected final String getNameFieldText() {
		return nameField.getText();
	}

	@Override
	protected void populateDetails(T object) {
		this.nameField.setText(object.getName());
	}

	@Override
	public void populateForClone(T object) {
		this.currentObject = null;
		this.nameField.setText(object.getName() + Constants.COPY_TEXT);
	}

	protected void removeDocumentListener(DocumentListener dl) {
		nameField.getDocument().removeDocumentListener(dl);
	}

	@Override
	protected void setEnabledFields(boolean enabled) {
		nameField.setEnabled(enabled);
	}

	/**
	 * Overwrite to perform other/additional input validation.
	 * That is, validate values of the fields here.
	 * @throws InputValidationException if an input field contains an invalid entry
	 * @since PowerEditor 4.2.0.
	 */
	@Override
	protected void validateFields() throws InputValidationException {
		if (nameField.getText().equals("")) throw new InputValidationException(ClientUtil.getInstance().getMessage("msg.warning.invalid.no.name"));
	}
}

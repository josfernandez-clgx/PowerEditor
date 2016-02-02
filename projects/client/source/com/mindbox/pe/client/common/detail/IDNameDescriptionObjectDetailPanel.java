/*
 * Created on Jun 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common.detail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.EntityType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class IDNameDescriptionObjectDetailPanel<T extends AbstractIDNameDescriptionObject, B extends ButtonPanel> extends
		IDNameObjectDetailPanel<T, B> {

	private JTextField descField;

	public IDNameDescriptionObjectDetailPanel(EntityType entityType) {
		super(entityType);
	}

	protected final String getDescFieldText() {
		return descField.getText();
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.descField = new JTextField(10);

		super.addComponents(bag, c);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.desc"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, descField);
	}

	protected void addDocumentListener(DocumentListener dl) {
		super.addDocumentListener(dl);
		descField.getDocument().addDocumentListener(dl);
	}

	protected void removeDocumentListener(DocumentListener dl) {
		super.removeDocumentListener(dl);
		descField.getDocument().removeDocumentListener(dl);
	}

	public void clearFields() {
		super.clearFields();
		this.descField.setText("");
	}

	protected void populateDetails(T object) {
		super.populateDetails(object);
		this.descField.setText(((AbstractIDNameDescriptionObject) object).getDescription());
	}

	public void populateForClone(T object) {
		super.populateForClone(object);
		this.descField.setText(((AbstractIDNameDescriptionObject) object).getDescription());
	}

	protected void setEnabledFields(boolean enabled) {
		super.setEnabledFields(enabled);
		this.descField.setEnabled(enabled);
	}

}

/*
 * Created on 2004. 6. 28.
 */
package com.mindbox.pe.client.applet.admin.request;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.PhaseSelectField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.model.process.ProcessRequest;

/**
 * Request Detail Panel.
 *
 * @author kim
 * @since PowerEditor  
 */
final class RequestDetailPanel extends JPanel {

	private final JFormattedTextField nameField;
	private final JFormattedTextField typeField;
	private final JFormattedTextField purposeField;
	private final JFormattedTextField initFunctionField;
	private final JTextField descField;
	private final JTextField dispNameField;
	private final PhaseSelectField phaseField;
	
	private ProcessRequest request = null;

	public RequestDetailPanel() {
		nameField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		typeField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		purposeField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		initFunctionField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		descField = new JTextField();
		dispNameField = new JTextField();
		
		phaseField = new PhaseSelectField(true, false);

		initPanel();
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.name"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, nameField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.name.display"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, dispNameField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.desc"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, descField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.type.request"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, typeField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.init.function"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, initFunctionField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.purpose"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, purposeField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.phase"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, phaseField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());
	}
	
	void setRequest(ProcessRequest request) {
		this.request = request;
		if (request == null) {
			clearFields();
		}
		else {
			updateFields();
		}
	}
	
	public void setEnabled(boolean enabled) {
		nameField.setEnabled(enabled);
		descField.setEnabled(enabled);
		dispNameField.setEnabled(enabled);
		typeField.setEnabled(enabled);
		initFunctionField.setEnabled(enabled);
		purposeField.setEnabled(enabled);
		phaseField.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	private void clearFields() {
		nameField.setValue(null);
		descField.setText("");
		dispNameField.setText("");
		typeField.setValue(null);
		initFunctionField.setValue(null);
		purposeField.setValue(null);
		phaseField.setValue(null);
	}
	
	private void updateFields() {
		nameField.setValue(request.getName());
		descField.setText(request.getDescription());
		dispNameField.setText(request.getDisplayName());
		typeField.setValue(request.getRequestType());
		initFunctionField.setValue(request.getInitFunction());
		purposeField.setValue(request.getPurpose());
		phaseField.setValue(request.getPhase());
	}
	
	ProcessRequest getRequest() {
		updateFromFields();
		return request;
	}
	
	private void updateFromFields() {
		if (request == null) {
			request = new ProcessRequest(-1, nameField.getText(),descField.getText());
		}
		else {
			request.setName(nameField.getText());
			request.setDescription(descField.getText());
		}
		request.setDisplayName(dispNameField.getText());
		request.setPurpose(purposeField.getText());
		request.setInitFunction(initFunctionField.getText());
		request.setRequestType(typeField.getText());
		request.setPhase(phaseField.getValue());
	}
}
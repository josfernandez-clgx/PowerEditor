/*
 * Created on 2004. 6. 28.
 */
package com.mindbox.pe.client.applet.admin.phase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.PhaseSelectField;
import com.mindbox.pe.client.common.PhaseTaskSelectField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseFactory;

/**
 * Request Detail Panel.
 *
 * @author kim
 * @since PowerEditor  
 */
final class PhaseDetailPanel extends JPanel {

	private final JFormattedTextField nameField;
	private final JTextField dispNameField;
	private final PhaseSelectField parentPhaseField;
	private final PhaseSelectField prereqPhaseField;
	private final PhaseTaskSelectField taskField;
	private final JCheckBox disjunctCheckbox;

	private Phase phase = null;

	public PhaseDetailPanel() {
		nameField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		dispNameField = new JTextField();

		parentPhaseField = new PhaseSelectField(false, false);
		prereqPhaseField = new PhaseSelectField(false, true);
		taskField = new PhaseTaskSelectField();
		disjunctCheckbox = UIFactory.createCheckBox("checkbox.phase.disjunctive");

		initPanel();
		parentPhaseField.setEnabled(false);
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
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.phase.parent"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, parentPhaseField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.phase.prereq"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, prereqPhaseField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, new JLabel(""));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, disjunctCheckbox);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.task"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, taskField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());
	}

	void setPhase(Phase phase) {
		this.phase = phase;
		if (phase == null) {
			clearFields();
		}
		else {
			updateFields();
		}
		setEnabled(true);
		if (phase != null) {
			prereqPhaseField.setEnabled(phase.getParent() != null);
		}
		else {
			prereqPhaseField.setEnabled(true);
		}
	}

	public void setEnabled(boolean enabled) {
		nameField.setEnabled(enabled);
		dispNameField.setEnabled(enabled);
		prereqPhaseField.setEnabled(enabled);
		taskField.setEnabled(enabled);
		disjunctCheckbox.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	private void clearFields() {
		nameField.setText("");
		dispNameField.setText("");
		parentPhaseField.setValue(null);
		prereqPhaseField.setValues(null);
		taskField.setValue(null);
		disjunctCheckbox.setSelected(false);
	}

	private void updateFields() {
		nameField.setText(phase.getName());
		dispNameField.setText(phase.getDisplayName());
		parentPhaseField.setValue(phase.getParent());
		taskField.setValue(phase.getPhaseTask());
		disjunctCheckbox.setSelected(phase.isDisjunctivePrereqs());
		prereqPhaseField.setValues(phase.getPrerequisites());
	}

	Phase getPhase() {
		updateFromFields();
		return phase;
	}

	private void updateFromFields() {
		if (phase == null) {
			phase = PhaseFactory.createPhase(PhaseFactory.TYPE_SEQUENCE, -1, nameField.getText(), dispNameField.getText());
		}
		else {
			phase.setName(nameField.getText());
			phase.setDisplayName(dispNameField.getText());
		}
		phase.setPhaseTask(taskField.getValue());
		phase.setDisjunctivePrereqs(disjunctCheckbox.isSelected());
		phase.setPrerequisites(prereqPhaseField.getValues());
	}
}
/*
 * Created on 2004. 6. 28.
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.DateSynonym;

/**
 * Request Detail Panel.
 *
 * @author kim
 * @since PowerEditor  
 */
final class DateSynonymDetailPanel extends JPanel {

	private final JTextField nameField;
	private final JTextField descField;
	private final MDateDateField dateField;
	private final JButton generateNameButton;

	private DateSynonym dateSynonym = null;

	public DateSynonymDetailPanel() {
		nameField = new JTextField();
		descField = new JTextField();
		dateField = new MDateDateField(true, true);
		generateNameButton = UIFactory.createJButton(null, "image.btn.calender", new GenerateNameL(), "button.tooltip.generate.name");


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

		c.weightx = 0.95;
		c.gridwidth = GridBagConstraints.WEST;
		UIFactory.addComponent(this, bag, c, nameField);
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		UIFactory.addComponent(this, bag, c, generateNameButton);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.date"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, dateField.getJComponent());

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.desc"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, descField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());
	}

	void setDateSynonym(DateSynonym dateSynonym) {
		this.dateSynonym = dateSynonym;
		if (dateSynonym == null) {
			clearFields();
		}
		else {
			updateFields();
		}
		setEnabled(true);
	}

	public void setDateFieldEnabled(boolean enabled) {
		dateField.setEnabled(enabled);
	}

	public void setEnabled(boolean enabled) {
		nameField.setEnabled(enabled);
		descField.setEnabled(enabled);
		dateField.setEnabled(enabled);
		generateNameButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	private void clearFields() {
		nameField.setText("");
		descField.setText("");
		dateField.setValue(null);
	}

	private void updateFields() {
		nameField.setText(dateSynonym.getName());
		descField.setText(dateSynonym.getDescription());
		dateField.setValue(dateSynonym.getDate());
	}

	DateSynonym getDateSynonym() throws InputValidationException {
		updateFromFields();
		return dateSynonym;
	}

	private void updateFromFields() throws InputValidationException {
		if (dateField.getDate() == null)
			throw new InputValidationException(ClientUtil.getInstance().getMessage(
					"msg.warning.invalid.date",
					new Object[] { ClientUtil.dateFormatter.toPattern() }));
		if (dateSynonym == null) {
			dateSynonym = new DateSynonym(-1, "", null, dateField.getDate());
		}
		else {
			dateSynonym.setDate(dateField.getDate());
		}

		dateSynonym.setName(nameField.getText());
		dateSynonym.setDescription(descField.getText());
	}

	private final class GenerateNameL implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try {
			if (DateSynonymDetailPanel.this.getDateSynonym() == null || DateSynonymDetailPanel.this.getDateSynonym().getDate() == null) {
				DateSynonymDetailPanel.this.nameField.setText("");
			}
			else {
				DateSynonymDetailPanel.this.nameField.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(
						DateSynonymDetailPanel.this.getDateSynonym().getDate()));
			}
			}
			catch (InputValidationException ex) {
				ClientUtil.getInstance().showErrorMessage(ex.getMessage());
			}
		}
	}

}
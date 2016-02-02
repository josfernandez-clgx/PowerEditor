/*
 * Created on 2005. 2. 24.
 *
 */
package com.mindbox.pe.tools.migration;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.tools.util.FileChooserField;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Inna Nill
 * @author MindBox LLC
 * @since PowerEditor 4.2.0
 */
public class DomainMigrationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368807955372168743L;

	private class CheckBoxL implements ActionListener {

		private final int checkboxID;

		public CheckBoxL(int checkboxID) {
			this.checkboxID = checkboxID;
		}

		public void actionPerformed(ActionEvent e) {
			if (checkboxID == 0)
				isDeployCheckboxChosen = ((JCheckBox) e.getSource()).isSelected();
			else if (checkboxID == 1) isLinkCheckboxChosen = ((JCheckBox) e.getSource()).isSelected();
		}
	}

	private class MigrateL implements ActionListener {

		public MigrateL() {
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (!validateValues(inFileField.getValue(), outFileField.getValue())) {
					displayError("Migration unsuccessful!\nInput file=" + inFileField.getValue() + "\nOutput file=" + outFileField.getValue());
				}
				else {
					DomainFileRepairFor4_2.getInstance().process(
							isDeployCheckboxChosen,
							isLinkCheckboxChosen,
							inFileField.getValue(),
							outFileField.getValue(),
							false);
				}
			}
			catch (Exception ex) {
				displayError(ex.toString());
			}
			finally {
				//System.out.println("<<< Finished repairing domain xml file.");
			}
		}
	}

	private final JCheckBox deployIDsCheckbox, linkDeployValueCheckbox;
	private final JLabel chooseInFileLabel, chooseOutFileLabel;
	private final FileChooserField inFileField;
	private final FileChooserField outFileField;
	private final JButton migrateButton;
	private boolean isDeployCheckboxChosen, isLinkCheckboxChosen;

	public DomainMigrationPanel() {
		setName("PowerEditor Data Repair Tool");

		deployIDsCheckbox = new JCheckBox("Add DeployIDs to Enums");
		linkDeployValueCheckbox = new JCheckBox("Add DeployValue to DomainClassLink");
		deployIDsCheckbox.setSelected(false);
		linkDeployValueCheckbox.setSelected(false);
		chooseInFileLabel = new JLabel("Input domain file ");
		chooseOutFileLabel = new JLabel("Output domain file ");
		inFileField = new FileChooserField(false);
		outFileField = new FileChooserField(false);
		migrateButton = new JButton("Migrate");

		initPanel();

		deployIDsCheckbox.addActionListener(new CheckBoxL(0));
		linkDeployValueCheckbox.addActionListener(new CheckBoxL(1));
		migrateButton.addActionListener(new MigrateL());
	}

	private void initPanel() {
		JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		checkBoxPanel.add(deployIDsCheckbox);
		checkBoxPanel.add(linkDeployValueCheckbox);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(migrateButton);

		/*
		 topPanel.add(checkBoxPanel);
		 topPanel.add(inFileChooserPanel);
		 topPanel.add(outFileChooserPanel);
		 topPanel.add(buttonPanel);*/

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(this, bag, c, checkBoxPanel);

		c.weightx = 0.0;
		c.gridwidth = 1;
		SwingUtil.addComponent(this, bag, c, chooseInFileLabel);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(this, bag, c, inFileField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		SwingUtil.addComponent(this, bag, c, chooseOutFileLabel);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(this, bag, c, outFileField);

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(this, bag, c, buttonPanel);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(this, bag, c, Box.createVerticalGlue());
	}

	private boolean validateValues(String inputFile, String outputFile) {
		if ((inputFile == null) || (outputFile == null) || (inputFile.length() <= 0) || (outputFile.length() <= 0))
			return false;
		else
			return true;
	}

	private void displayError(String errMessage) {
		JOptionPane.showMessageDialog(this, (Object) errMessage, "Error:", JOptionPane.ERROR_MESSAGE);
	}
}
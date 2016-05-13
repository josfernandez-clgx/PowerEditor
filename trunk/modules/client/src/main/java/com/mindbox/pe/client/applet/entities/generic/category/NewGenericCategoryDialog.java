package com.mindbox.pe.client.applet.entities.generic.category;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.GenericCategorySelectField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.ValidationErrorReportDialog;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;

public class NewGenericCategoryDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static GenericCategory newGenericCategory(Frame owner, int categoryType, GenericCategory desiredParent) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("New Category");

		NewGenericCategoryDialog panel = new NewGenericCategoryDialog(categoryType, dialog, null);
		if (desiredParent != null) {
			panel.setParentGenericCategory(desiredParent);
		}

		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.category;
	}

	private class CreateL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (validateAndUpdateFromFields()) {
				try {
					int catID = ClientUtil.getCommunicator().save(NewGenericCategoryDialog.this.category, false);

					NewGenericCategoryDialog.this.category.setID(catID);
					dialog.dispose();
				}
				catch (ValidationException ex) {
					ValidationErrorReportDialog.showErrors(ex, false);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
			}
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			category = null;
			dialog.dispose();
		}
	}

	private final JTextField nameField;
	private final GenericCategorySelectField parentCatField;
	private GenericCategory category = null;
	private JDialog dialog;
	private final int categoryType;
	private final boolean allowEmptyParent;
	private final DateSelectorComboField effDateCombo;
	private final DateSelectorComboField expDateCombo;

	private NewGenericCategoryDialog(int categoryType, JDialog dialog, GenericCategory category) {
		this.dialog = dialog;
		this.category = category;
		this.categoryType = categoryType;
		this.allowEmptyParent = (category != null && category.isRoot());
		nameField = new JTextField(10);
		parentCatField = new GenericCategorySelectField(categoryType, false);
		parentCatField.setAllowDelete(false);
		effDateCombo = new DateSelectorComboField(true, true, true);
		expDateCombo = new DateSelectorComboField(true, true, true);

		initDialog();

		setSize(400, 220);
	}

	private void setParentGenericCategory(GenericCategory cat) {
		parentCatField.setValue(cat);
	}

	public void setEnabled(boolean enabled) {
		nameField.setEditable(enabled);
		parentCatField.setEnabled(enabled);
	}

	private void initDialog() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
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
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.category.parent"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, parentCatField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.date.activation"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, effDateCombo);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.date.expiration"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, expDateCombo);

		if (dialog != null) {
			JButton createButton = new JButton("Accept");
			createButton.addActionListener(new CreateL());
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CancelL());

			JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
			buttonPanel.add(createButton);
			buttonPanel.add(cancelButton);

			PanelBase.addComponent(this, bag, c, new JSeparator());
			c.insets.top = 12;
			c.insets.bottom = 4;
			PanelBase.addComponent(this, bag, c, buttonPanel);
		}
		else {
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			c.gridheight = GridBagConstraints.REMAINDER;
			c.weighty = 1.0;
			UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());
		}
	}

	GenericCategory getGenericCategory() {
		return category;
	}

	boolean validateAndUpdateFromFields() {
		// disable to allow editing root category
		// this works just because editing parent is disallowed.
		category = new GenericCategory(-1, nameField.getText(), categoryType);
		category.setRootIndicator(allowEmptyParent);

		if (parentCatField.hasValue()) {
			category.addParentKey(new DefaultMutableTimedAssociationKey(parentCatField.getGenericCategoryID(), effDateCombo.getValue(), expDateCombo.getValue()));
		}
		return true;
	}

}

/*
 * Created on 2005. 2. 23.
 *
 */
package com.mindbox.pe.client.applet.template.rule;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.DomainClassSelectField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.RuleElementFactory;


/**
 * Dialog for editing exist expression.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
class ExistExpressionEditDialog extends JPanel {

	public static ExistExpression editExistExpression(ExistExpression existExpression) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("Edit Exist Expression");
		ExistExpressionEditDialog panel = new ExistExpressionEditDialog(dialog, existExpression);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.existExpression;
	}

	public static ExistExpression createExistExpression() {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("New Exist Expression");
		ExistExpressionEditDialog panel = new ExistExpressionEditDialog(dialog, null);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.existExpression;
	}

	private class CreateL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (updateExistExpression()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			existExpression = null;
			dialog.dispose();
		}
	}


	private final DomainClassSelectField classSelectField;
	private final JTextField objectNameField;
	private final JTextField excludedObjectNameField;
	private final JDialog dialog;
	private ExistExpression existExpression = null;

	private ExistExpressionEditDialog(JDialog dialog, ExistExpression existExpression) {
		this.dialog = dialog;
		this.existExpression = existExpression;
		classSelectField = new DomainClassSelectField(false);
		objectNameField = new JTextField();
		excludedObjectNameField = new JTextField();
		
		initPanel();

		populateFields();

		setSize(500, 280);
	}
	
	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		JLabel label = UIFactory.createFormLabel("label.domain.class.parent");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, classSelectField);

		label = UIFactory.createFormLabel("label.object.name");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, objectNameField);

		label = UIFactory.createFormLabel("label.object.exc.name");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

//		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, excludedObjectNameField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createLabel("label.object.exc.ampersand"));
		
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new CreateL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		PanelBase.addComponent(this, bag, c, new JSeparator());
		c.insets.top = 12;
		c.insets.bottom = 8;
		PanelBase.addComponent(this, bag, c, buttonPanel);
	}
	
	private void populateFields() {
		if (existExpression != null) {
			classSelectField.setValue(existExpression.getClassName());
			if (existExpression.getObjectName() != null) {
				objectNameField.setText(existExpression.getObjectName());
			}
			if (existExpression.getExcludedObjectName() != null) {
				excludedObjectNameField.setText(existExpression.getExcludedObjectName());
			}
		}
		else {
			classSelectField.setValue(null);
			objectNameField.setText("");
			excludedObjectNameField.setText("");
		}
	}

	private boolean updateExistExpression() {
		if (ClientUtil.isEmpty(classSelectField.getValue())) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.domain.class.parent")});
			return false;
		}
		else if (!ClientUtil.isEmpty(objectNameField.getText()) && objectNameField.getText().equals(excludedObjectNameField.getText())) {
			ClientUtil.getInstance().showWarning("msg.warning.equal", new Object[]{ClientUtil.getInstance().getLabel("label.object.name"),
					ClientUtil.getInstance().getLabel("label.object.exc.name")});
			return false;
		}
		
		if (existExpression == null) {
			existExpression = RuleElementFactory.getInstance().createExistExpression(classSelectField.getValue());
		}
		else {
			existExpression.setClassName(classSelectField.getValue());
		}
		if (!ClientUtil.isEmpty(objectNameField.getText())) {
			existExpression.setObjectName(objectNameField.getText());
		}
		else {
			existExpression.setObjectName(null);
		}
		if (!ClientUtil.isEmpty(excludedObjectNameField.getText())) {
			existExpression.setExcludedObjectName(excludedObjectNameField.getText());
		}
		else {
			existExpression.setExcludedObjectName(null);
		}
		return true;
	}
}
/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.template.guideline;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.model.ColumnAttributeItemDigest;

/**
 * Dialog for creating/editing column attribute item.
 *
 * @author kim
 * @since PowerEditor 4.3.7
 */
class ColumnAttributeItemDialog extends JPanel {

	public static ColumnAttributeItemDigest newColumnAttributeItem() {
		ColumnAttributeItemDialog instance = new ColumnAttributeItemDialog(null);
		JDialog dialog = UIFactory.createAsModelDialog("d.title.new.col.attr.item", instance);
		instance.dialog = dialog;

		dialog.setVisible(true);

		return instance.attributeItem;
	}

	public static ColumnAttributeItemDigest editColumnAttributeItem(ColumnAttributeItemDigest attributeItem) {
		ColumnAttributeItemDialog instance = new ColumnAttributeItemDialog(attributeItem);
		JDialog dialog = UIFactory.createAsModelDialog("d.title.edit.col.attr.item", instance);
		instance.dialog = dialog;

		dialog.setVisible(true);

		return instance.attributeItem;
	}

	private class AcceptL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (updateFromFields()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			attributeItem = null;
			dialog.dispose();
		}
	}

	private final JButton okButton, cancelButton;
	private final JTextField displayLabelField;
	private final AttributeReferenceSelectField attrSelectField;
	private JDialog dialog;
	private ColumnAttributeItemDigest attributeItem = null;
	private final char[] illegalCharsForDisplayName={',','$','.','%','"'};//added in 4.5.10

	private ColumnAttributeItemDialog(ColumnAttributeItemDigest attributeItem) {
		this.attributeItem = attributeItem;
		displayLabelField = new JTextField();
		attrSelectField = new AttributeReferenceSelectField();

		cancelButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.cancel"), null, new CancelL(), null);
		okButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.ok"), null, new AcceptL(), null);

		initPanel();
		setSize(420, 160);

		populateFields();
	}

	private void populateFields() {
		if (attributeItem != null) {
			displayLabelField.setText(attributeItem.getDisplayValue());
			String[] strs = attributeItem.getName().split("\\.");
			if (strs.length > 1) {
				attrSelectField.setValue(strs[0], strs[1]);
			}
		}
		else {
			displayLabelField.setText(null);
			attrSelectField.setValue(null,null);
		}
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();

		// build Step 1 panel
		JPanel panel = UIFactory.createJPanel(bag);
		panel.setBorder(UIFactory.createTitledBorder("Enter Attribute to be available for Dynamic String"));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.name.display"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, displayLabelField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.attribute"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, attrSelectField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(panel, bag, c, Box.createVerticalGlue());

		JPanel bp = UIFactory.createFlowLayoutPanelCenterAlignment(4, 4);
		bp.add(okButton);
		bp.add(cancelButton);

		setLayout(new BorderLayout(4, 4));

		add(panel, BorderLayout.CENTER);
		add(bp, BorderLayout.SOUTH);
	}

	private boolean updateFromFields() {
		if (ClientUtil.isEmpty(displayLabelField.getText())) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name.display")});
			return false;
		}		
		if (ClientUtil.isEmpty(attrSelectField.getValue())) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.attribute")});
			return false;
		}

		// give warning if any of the illegal characters are entered in Display Name
		String chrToStr="";//needed for display only
		for(int i=0;i<illegalCharsForDisplayName.length;i++){
			chrToStr += (illegalCharsForDisplayName[i]+"  ");
		}
		if(ClientUtil.hasIllegalCharacters(illegalCharsForDisplayName,displayLabelField.getText())){
			ClientUtil.getInstance().showWarning("msg.warning.invalid.characters", new Object[] { 
					ClientUtil.getInstance().getLabel("label.name.display"),chrToStr
					});
			return false;
		}
		
		if (attributeItem == null) {
			attributeItem = new ColumnAttributeItemDigest();
		}
		attributeItem.setDisplayValue(displayLabelField.getText());
		attributeItem.setName(attrSelectField.getValue());

		return true;
	}
}
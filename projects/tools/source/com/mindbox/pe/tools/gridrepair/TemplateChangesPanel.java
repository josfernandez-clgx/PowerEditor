/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TemplateChangesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5373713536258324108L;

	private static class TemplateColumnChangeSpecImpl implements TemplateColumnChangeSpec {
		private final int id;
		private final int[] added, deleted;

		private TemplateColumnChangeSpecImpl(int id, String addedStr, String deletedStr) {
			this.id = id;
			this.added = (addedStr == null ? null : UtilBase.toIntArray(addedStr));
			this.deleted = (deletedStr == null ? null : UtilBase.toIntArray(deletedStr));
		}
		public int getTemplateID() {
			return id;
		}
		public int[] removedColumns() {
			return deleted;
		}
		public int[] addedColumnPositions() {
			return added;
		}
		public String toString() {
			return "Template=" + id +",addedColPos="+UtilBase.toString(added)+",deletedCols="+UtilBase.toString(deleted);
		}
	}
	
	private class TemplateChangeSpecImpl implements TemplateChangeSpec {
		public int numberOfColumnChanges() {
			return changeTableModel.getRowCount();
		}
		public TemplateColumnChangeSpec getColumnChangeSpecAt(int index) {
			return (TemplateColumnChangeSpec) changeTable.getValueAt(index,-1);
		}
		public String toString() {
			return "TemplateChangeSpec[size="+changeTable.getRowCount()+"]";
		}
	}

	private class AddL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (validateFields()) {
				TemplateColumnChangeSpec spec =
					new TemplateColumnChangeSpecImpl(
						idField.getValue().intValue(),
						addedField.getText(),
						deletedField.getText());
				changeTableModel.addData(spec);
			}
		}
	}

	private class DeleteL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			changeTableModel.removeData(getSelectedColumnChangeSpec());
		}
	}

	private class UpdateL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			TemplateColumnChangeSpec spec =
				new TemplateColumnChangeSpecImpl(
					idField.getValue().intValue(),
					addedField.getText(),
					deletedField.getText());
			changeTableModel.removeData(getSelectedColumnChangeSpec());
			changeTableModel.addData(spec);			
		}
	}

	private class TableSelectionL implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			if (changeTable.getSelectedRow() >= 0) {
				setEnabledSelectionAwares(true);
				populateFields();
			}
			else {
				clearFields();
				setEnabledSelectionAwares(false);
			}
		}
	}

	private final TemplateChangeTableModel changeTableModel;
	private final TemplateChangeTable changeTable;
	private final NumberTextField idField;
	private final JTextField deletedField, addedField;
	private final JButton addButton, deleteButton, updateButton;
	private TemplateChangeSpec templateChangeSpec = null;

	public TemplateChangesPanel() {
		changeTableModel = new TemplateChangeTableModel();
		changeTable = new TemplateChangeTable(changeTableModel);
		changeTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		changeTable.getSelectionModel().addListSelectionListener(new TableSelectionL());

		this.idField = new NumberTextField(1);
		this.deletedField = new JTextField();
		this.addedField = new JTextField();
		
		addButton = new JButton("Add");
		addButton.addActionListener(new AddL());
		deleteButton = new JButton("Remove");
		deleteButton.addActionListener(new DeleteL());
		updateButton = new JButton("Update");
		updateButton.addActionListener(new UpdateL());

		initPanel();

		setEnabledSelectionAwares(false);
		idField.requestFocus();
	}

	private TemplateColumnChangeSpec getSelectedColumnChangeSpec() {
		return (TemplateColumnChangeSpec) changeTable.getModel().getValueAt(changeTable.getSelectedRow(), -1);
	}

	private void setEnabledSelectionAwares(boolean enabled) {
		deleteButton.setEnabled(enabled);
		updateButton.setEnabled(enabled);
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("Template ID:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, idField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("Added Column Positions*:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, addedField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("Delete Columns*:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, deletedField);

		JLabel instLabel = new JLabel("* Specify as comma separted integers");
		instLabel.setFont(new Font("System",0,10));
		SwingUtil.addComponent(this, bag, c, instLabel);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;

		SwingUtil.addComponent(this, bag, c, new JSeparator());

		JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bPanel.add(addButton);
		bPanel.add(updateButton);
		bPanel.add(deleteButton);

		SwingUtil.addComponent(this, bag, c, bPanel);

		c.gridheight = 1;
		c.weighty = 1.0;
		SwingUtil.addComponent(this, bag, c, new JScrollPane(changeTable));
	}

	private void clearFields() {
		idField.setText("");
		addedField.setText(null);
		deletedField.setText(null);
	}

	private void populateFields() {
		TemplateColumnChangeSpec spec = getSelectedColumnChangeSpec();
		if (spec != null) {
			idField.setValue(spec.getTemplateID());
			addedField.setText(UtilBase.toString(spec.addedColumnPositions()));
			deletedField.setText(UtilBase.toString(spec.removedColumns()));
		}
		else {
			clearFields();
		}
	}

	private boolean validateFields() {
		if (idField.getValue() == null) {
			SwingUtil.showWarning("Please enter template ID");
			idField.requestFocus();
			return false;
		}
		if (SwingUtil.isEmpty(addedField) && SwingUtil.isEmpty(deletedField)) {
			SwingUtil.showWarning("Please provide at least one added or delete column number.");
			deletedField.requestFocus();
			return false;
		}
		if (!SwingUtil.isEmpty(addedField)) {
			try {
				UtilBase.toIntArray(addedField.getText());
			}
			catch (Exception ex) {
				SwingUtil.showWarning("Added column potisions you specified is invalid.");
				return false;
			}
		}
		if (!SwingUtil.isEmpty(deletedField)) {
			try {
				UtilBase.toIntArray(deletedField.getText());
			}
			catch (Exception ex) {
				SwingUtil.showWarning("Delete columns you specified is invalid.");
				return false;
			}
		}
		return true;
	}

	public TemplateChangeSpec getTemplateChangeSpec() {
		if (templateChangeSpec == null) {
			templateChangeSpec = new TemplateChangeSpecImpl();
		}
		return templateChangeSpec;
	}

}

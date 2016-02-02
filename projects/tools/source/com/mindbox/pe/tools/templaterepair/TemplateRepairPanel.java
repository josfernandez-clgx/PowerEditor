/*
 * Created on 2005. 5. 10.
 *
 */
package com.mindbox.pe.tools.templaterepair;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.mindbox.pe.tools.InvalidSpecException;
import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionInfoManager;
import com.mindbox.pe.tools.migration.AbstractMigrationDetailPanel;
import com.mindbox.pe.tools.util.SwingUtil;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class TemplateRepairPanel extends AbstractMigrationDetailPanel {

	private static boolean isValidAttributeName(String value) {
		return value != null && value.length() > 0 && value.matches("^[a-zA-Z0-9_]+\\.[a-zA-Z0-9]+$");
	}

	private class MigrateL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else {
				migrate(dbConnInfoManager.getSelectedDBConnInfo());
			}
		}
	}

	private class PreProcessL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else {
				SwingUtil.setCursorToWait(panel);
				try {
					((JButton) e.getSource()).setEnabled(false);
					preprocess(dbConnInfoManager.getSelectedDBConnInfo());
				}
				finally {
					((JButton) e.getSource()).setEnabled(true);
					SwingUtil.setCursorToNormal(panel);
				}
			}
		}
	}

	private final JTextField oldAttributeField = new JTextField();
	private final JTextField newAttributeField = new JTextField();
	private final TemplateColumnRuleInfoTable table;
	private final TemplateColumnRuleInfoTableModel tableModel;
	private final DBConnectionInfoManager dbConnInfoManager;

	public TemplateRepairPanel(DBConnectionInfoManager dbConnInfoManager) {
		this.dbConnInfoManager = dbConnInfoManager;
		this.tableModel = new TemplateColumnRuleInfoTableModel();
		this.table = new TemplateColumnRuleInfoTable(tableModel);
	}

	protected void initPanel() {
		panel.setLayout(new BorderLayout(0, 0));

		GridBagLayout bag = new GridBagLayout();
		JPanel fieldPanel = new JPanel(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.weightx = 0.0;
		c.gridwidth = 1;
		SwingUtil.addComponent(fieldPanel, bag, c, new JLabel("Old Attribute Name (class.attribute):"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(fieldPanel, bag, c, oldAttributeField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		SwingUtil.addComponent(fieldPanel, bag, c, new JLabel("New Attribute Name (class.attribute):"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(fieldPanel, bag, c, newAttributeField);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		SwingUtil.addComponent(fieldPanel, bag, c, new JScrollPane(table)); //Box.createVerticalGlue());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton button = new JButton("Preprocess");
		buttonPanel.add(button);
		button.addActionListener(new PreProcessL());

		button = new JButton("Migrate");
		buttonPanel.add(button);
		button.addActionListener(new MigrateL());

		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(fieldPanel, BorderLayout.CENTER);
	}

	private boolean validateFields() {
		if (!isValidAttributeName(oldAttributeField.getText())) {
			SwingUtil.showWarning("Old Attribute Name field does not have a valid domain attribute name.");
			oldAttributeField.requestFocus();
			return false;
		}
		if (!isValidAttributeName(newAttributeField.getText())) {
			SwingUtil.showWarning("New Attribute Name field does not have a valid domain attribute name.");
			newAttributeField.requestFocus();
			return false;
		}
		return true;
	}

	protected void preprocess(DBConnInfo connInfo) {
		if (validateFields()) {
			String[] strs = oldAttributeField.getText().split("\\.");
			if (strs == null || strs.length < 2) {

			}
			else {
				try {
					TemplateColumnRuleInfo[] result = TemplateRepairWorker.getInstance().retrieveTemplateColumRulesToUpdate(
							strs[0],
							strs[1],
							connInfo);
				
					tableModel.setValues(result);
				}
				catch (InvalidSpecException e) {
					SwingUtil.showWarning("The specified old attribute '" + oldAttributeField.getText()+"' is invalid: " + e.getMessage());
				}
				catch (Exception ex) {
					ex.printStackTrace();
					SwingUtil.showWarning("Failed to retrieve template/column rule info from " + connInfo + ": " + ex.getMessage());
				}
			}
		}
	}

	protected void migrate(DBConnInfo connInfo) {
		if (validateFields()) {
			if (SwingUtil.yesorno("Are you sure you want to migrate PowerEditor data as specified?\nThis cannot be undone.\n")) {
				SwingUtil.setCursorToWait(panel);
				String[] oldStrs = oldAttributeField.getText().split("\\.");
				if (oldStrs == null || oldStrs.length < 2) {
					// show error
					return;
				}
				String[] newStrs = newAttributeField.getText().split("\\.");
				if (newStrs == null || newStrs.length < 2) {
					// show error
					return;
				}
				try {
					// update template/column rules
					int count = TemplateRepairWorker.getInstance().updateTemplateColumnRules(
							oldStrs[0],
							oldStrs[1],
							newStrs[0],
							newStrs[1],
							tableModel.getValues(),
							connInfo);
					SwingUtil.showInfo("Updated " + count + " template/column rules.");
					tableModel.clearDataList();
				}
				catch (InvalidSpecException e) {
					e.printStackTrace();
					SwingUtil.showWarning("The specified attributes are invalid: " + e.getMessage());
				}
				catch (Exception ex) {
					ex.printStackTrace();
					SwingUtil.showWarning("Failed to retrieve template data from " + connInfo + ": " + ex.getMessage());
				}
				finally {
					SwingUtil.setCursorToNormal(panel);
				}
			}
		}
	}
}
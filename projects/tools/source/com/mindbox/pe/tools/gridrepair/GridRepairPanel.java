/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.gridrepair;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mindbox.pe.tools.db.DBConnectionFactory;
import com.mindbox.pe.tools.db.DBConnectionInfoManager;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class GridRepairPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368807955372168743L;

	private class AddL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else if (templatePanel.getTemplateChangeSpec().numberOfColumnChanges() < 1) {
				SwingUtil.showWarning("No template changes are specified. Please specify at least one.");
			}
			else if (SwingUtil.yesorno("Are you sure you want to repair PowerEditor data as specified?\nThis cannot be undone.\n")) {
				repairData();
			}
		}
	}

	private final DBConnectionInfoManager dbConnInfoManager;
	private final TemplateChangesPanel templatePanel;

	public GridRepairPanel(DBConnectionInfoManager dbConnInfoManager) {
		setName("PowerEditor Data Repair Tool");
		this.dbConnInfoManager = dbConnInfoManager;
		this.templatePanel = new TemplateChangesPanel();

		initPanel();
	}

	private void initPanel() {
		setLayout(new BorderLayout());

		templatePanel.setBorder(BorderFactory.createTitledBorder("Template Change Specification"));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton button = new JButton("Repair Data");
		buttonPanel.add(button);
		button.addActionListener(new AddL());

		add(templatePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void repairData() {
		SwingUtil.setCursorToWait(this);
		try {
			int count = GridDataRepairWorker.getInstance().repairData(
					DBConnectionFactory.getInstance().getConnection(dbConnInfoManager.getSelectedDBConnInfo()),
					templatePanel.getTemplateChangeSpec());

			SwingUtil.setCursorToNormal(this);
			SwingUtil.showInfo("Successfully repaired all data: " + count + " row(s) were updated.");
		}
		catch (Exception ex) {
			SwingUtil.setCursorToNormal(this);
			SwingUtil.showError("Failed to repair data - no changes to the database were made:\n- " + ex.getMessage());
		}
	}

}
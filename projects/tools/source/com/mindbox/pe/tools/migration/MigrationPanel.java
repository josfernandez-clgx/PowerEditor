/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.migration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionInfoManager;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class MigrationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368807955372168743L;

	private class CheckBoxL implements ActionListener {

		private final int tabID;

		public CheckBoxL(int tabID) {
			this.tabID = tabID;
		}

		public void actionPerformed(ActionEvent e) {
			tab.setEnabledAt(tabID, ((JCheckBox) e.getSource()).isSelected());
			if (tab.isEnabledAt(tabID)) {
				tab.setSelectedIndex(tabID);
			}
		}
	}

	private class MigrateL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else if (SwingUtil.yesorno("Are you sure you want to migrate PowerEditor data as specified?\nThis cannot be undone.\n")) {
				migrateDB(dbConnInfoManager.getSelectedDBConnInfo());
			}
		}
	}

	private class PreProcessL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else {
				try {
					((JButton) e.getSource()).setEnabled(false);
					preprocess(dbConnInfoManager.getSelectedDBConnInfo());
				}
				finally {
					((JButton) e.getSource()).setEnabled(true);
				}
			}
		}
	}

	private final DBConnectionInfoManager dbConnInfoManager;
	private final TemplatePanel templatePanel;
	private final Guideline4x42Panel guidelinePanel;
	private final Parameter4x42Panel parameterPanel;
	private final CBR4x42Panel cbrPanel;
	private final GuidelineActionParameterPanel actionParameterPanel;
	private final JCheckBox templateCheckbox, guidelineCheckbox, parameterCheckbox, cbrCheckbox;
	private final JCheckBox guidelineParameterCheckbox;
	private final JTabbedPane tab;

	public MigrationPanel(DBConnectionInfoManager dbConnInfoManager) {
		setName("PowerEditor Data Repair Tool");
		this.dbConnInfoManager = dbConnInfoManager;
		this.tab = new JTabbedPane();

		templatePanel = new TemplatePanel();
		guidelinePanel = new Guideline4x42Panel();
		parameterPanel = new Parameter4x42Panel();
		cbrPanel = new CBR4x42Panel();
		actionParameterPanel = new GuidelineActionParameterPanel();

		templateCheckbox = new JCheckBox("Templates");
		guidelineCheckbox = new JCheckBox("Guideline Dates");
		parameterCheckbox = new JCheckBox("Parameters");
		guidelineParameterCheckbox = new JCheckBox("Guideline Action Parameters");
		
		cbrCheckbox = new JCheckBox("CBR");
		guidelineCheckbox.setSelected(false);
		templateCheckbox.setSelected(false);
		parameterCheckbox.setSelected(false);
		cbrCheckbox.setSelected(false);
		guidelineParameterCheckbox.setSelected(false);

		initPanel();
		
		templateCheckbox.addActionListener(new CheckBoxL(0));
		guidelineCheckbox.addActionListener(new CheckBoxL(1));
		parameterCheckbox.addActionListener(new CheckBoxL(2));
		cbrCheckbox.addActionListener(new CheckBoxL(3));
		guidelineParameterCheckbox.addActionListener(new CheckBoxL(4));
	}

	private void initPanel() {
		setLayout(new BorderLayout());

		JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		checkBoxPanel.add(templateCheckbox);
		checkBoxPanel.add(guidelineCheckbox);
		checkBoxPanel.add(parameterCheckbox);
		checkBoxPanel.add(cbrCheckbox);
		//checkBoxPanel.add(guidelineParameterCheckbox);

		tab.addTab("Templates", templatePanel.getPanel());
		tab.addTab("Guideline Dates", guidelinePanel.getPanel());
		tab.addTab("Parameter Dates", parameterPanel.getPanel());
		tab.addTab("CBR Dates", cbrPanel.getPanel());
		//tab.addTab("Guideline Action Parameters", actionParameterPanel.getPanel());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton button = new JButton("Preprocess");
		buttonPanel.add(button);
		button.addActionListener(new PreProcessL());

		button = new JButton("Migrate");
		buttonPanel.add(button);
		button.addActionListener(new MigrateL());

		JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 0));
		topPanel.add(buttonPanel);
		topPanel.add(checkBoxPanel);

		add(tab, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		
		tab.setEnabledAt(0, false);
		tab.setEnabledAt(1, false);
		tab.setEnabledAt(2, false);
		tab.setEnabledAt(3, false);
		//tab.setEnabledAt(4, false);

	}

	private synchronized void migrateDB(DBConnInfo connInfo) {
		SwingUtil.setCursorToWait(this);
		try {
			if (templateCheckbox.isSelected()) templatePanel.migrate(connInfo);
			if (guidelineCheckbox.isSelected()) guidelinePanel.migrate(connInfo);
			if (parameterCheckbox.isSelected()) parameterPanel.migrate(connInfo);
			if (cbrCheckbox.isSelected()) cbrPanel.migrate(connInfo);
			if (guidelineParameterCheckbox.isSelected()) actionParameterPanel.migrate(connInfo);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showError("Migration error: " + ex.getMessage());
		}
		finally {
			SwingUtil.setCursorToNormal(this);
		}
	}

	private synchronized void preprocess(DBConnInfo connInfo) {
		SwingUtil.setCursorToWait(this);
		try {
			if (templateCheckbox.isSelected()) templatePanel.preprocess(connInfo);
			if (guidelineCheckbox.isSelected()) guidelinePanel.preprocess(connInfo);
			if (parameterCheckbox.isSelected()) parameterPanel.preprocess(connInfo);
			if (cbrCheckbox.isSelected()) cbrPanel.preprocess(connInfo);
			if (guidelineParameterCheckbox.isSelected()) actionParameterPanel.preprocess(connInfo);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showError("Preprocessing error: " + ex.getMessage());
		}
		finally {
			SwingUtil.setCursorToNormal(this);
		}
	}
}
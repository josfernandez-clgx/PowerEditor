/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.mindbox.pe.tools.db.DBConnectionPanel;
import com.mindbox.pe.tools.gridrepair.GridRepairPanel;
import com.mindbox.pe.tools.migration.DomainMigrationPanel;
import com.mindbox.pe.tools.migration.MigrationPanel;
import com.mindbox.pe.tools.templaterepair.TemplateRepairPanel;
import com.mindbox.pe.tools.util.PreferenceUtil;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class PowerEditorTool {

	private static class WindowL extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			exit();
		}
	}

	static void exit() {
		if (SwingUtil.yesorno("Are you sure you want to exit?")) {
			PreferenceUtil.savePreferences();
			Logger logger = Logger.getLogger("PEDataRepairor");
			logger.info("Exiting");
			System.exit(0);
		}
	}


	public static void main(String[] args) throws Exception {
		SwingUtil.setLookAndFeelToOS();

		LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));

		Logger logger = Logger.getLogger("PowerEditorTool");
		logger.info("Started");

		PreferenceUtil.initialize();

		final WindowL windowListener = new WindowL();

		DBConnectionPanel dbConnPanel = new DBConnectionPanel();
		dbConnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "PowerEditor Database Connection Settings"));

		GridRepairPanel gridRepairPanel = new GridRepairPanel(dbConnPanel);
		DomainMigrationPanel domainMigrationPanel = new DomainMigrationPanel();
		MigrationPanel migration4x42Panel = new MigrationPanel(dbConnPanel);
		TemplateRepairPanel templateRepairPanel = new TemplateRepairPanel(dbConnPanel);

		JTabbedPane tab = new JTabbedPane();
		//tab.addTab("Migration: Any to 4.2", domainMigrationPanel);
		tab.addTab("Migration: 4.x to 4.2", migration4x42Panel);
		//tab.addTab("Migration: 3.3.x to 4.2", new JPanel());
		tab.addTab("Template Repair (4.0 or later)", templateRepairPanel.getPanel());
		tab.addTab("Grid Repair (3.3.x or earlier)", gridRepairPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dbConnPanel, tab);

		JButton button = new JButton("Exit");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				windowListener.windowClosing(null);
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(button);

		JPanel dbUtilityPanel = new JPanel(new BorderLayout(0, 0));
		dbUtilityPanel.setName("PowerEditor Tools");
		dbUtilityPanel.add(splitPane, BorderLayout.CENTER);
		dbUtilityPanel.add(buttonPanel, BorderLayout.SOUTH);

		JTabbedPane domainUtilityTab = new JTabbedPane();
		domainUtilityTab.addTab("Migrate Domain (Any to 4.2)", domainMigrationPanel);
		
		JTabbedPane mainTab = new JTabbedPane();
		mainTab.addTab("Database Tools", dbUtilityPanel);
		mainTab.addTab("Domain XML Tools", domainUtilityTab);
		
		System.out.println("Pref size = " + dbUtilityPanel.getPreferredSize());
		dbUtilityPanel.setPreferredSize(new Dimension(640, dbUtilityPanel.getPreferredSize().height));

		mainTab.setName(" by MindBox");
		SwingUtil.setParent(mainTab);
		SwingUtil.showAsNewFrame(mainTab, windowListener);

		// change icon to wait
		SwingUtil.setCursorToWait(mainTab);

		// read preferences
		PreferenceUtil.readSavedDBInfo();
		dbConnPanel.initWithData();

		// change icon to normal
		SwingUtil.setCursorToNormal(mainTab);
	}

	private PowerEditorTool() {
	}

}
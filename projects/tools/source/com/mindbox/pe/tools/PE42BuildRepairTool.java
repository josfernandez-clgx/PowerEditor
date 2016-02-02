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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionInfoManager;
import com.mindbox.pe.tools.db.DBConnectionPanel;
import com.mindbox.pe.tools.migration.GuidelineActionParameterPanel;
import com.mindbox.pe.tools.util.PreferenceUtil;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class PE42BuildRepairTool {

	private static class WindowL extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			exit();
		}
	}

	static void exit() {
		if (SwingUtil.yesorno("Are you sure you want to exit?")) {
			PreferenceUtil.savePreferences();
			Logger logger = Logger.getLogger("PE42BuildRepairTool");
			logger.info("Exiting");
			System.exit(0);
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


	public static void main(String[] args) throws Exception {
		SwingUtil.setLookAndFeelToOS();

		LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));

		Logger logger = Logger.getLogger("PE42BuildRepairTool");
		logger.info("Started");

		PreferenceUtil.initialize();

		final WindowL windowListener = new WindowL();

		DBConnectionPanel dbConnPanel = new DBConnectionPanel();
		dbConnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "PowerEditor Database Connection Settings"));

		PE42BuildRepairTool instance = new PE42BuildRepairTool(dbConnPanel);
		JPanel panel = instance.getJPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dbConnPanel, panel);

		JButton button = new JButton("Exit");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				windowListener.windowClosing(null);
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(button);

		JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
		mainPanel.setName("PowerEditor 4.2 Build Repair Tool");
		mainPanel.add(splitPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		System.out.println("Pref size = " + mainPanel.getPreferredSize());
		mainPanel.setPreferredSize(new Dimension(640, mainPanel.getPreferredSize().height));

		SwingUtil.setParent(mainPanel);
		SwingUtil.showAsNewDialog(mainPanel, false, windowListener);

		// change icon to wait
		SwingUtil.setCursorToWait(mainPanel);

		// read preferences
		PreferenceUtil.readSavedDBInfo();
		dbConnPanel.initWithData();

		// change icon to normal
		SwingUtil.setCursorToNormal(mainPanel);
	}

	private final DBConnectionInfoManager dbConnInfoManager;
	private final GuidelineActionParameterPanel actionParameterPanel;
	private JPanel panel = null;

	private PE42BuildRepairTool(DBConnectionInfoManager dbConnInfoManager) {
		this.dbConnInfoManager = dbConnInfoManager;
		actionParameterPanel = new GuidelineActionParameterPanel();
	}

	private synchronized void migrateDB(DBConnInfo connInfo) {
		SwingUtil.setCursorToWait(panel);
		try {
			actionParameterPanel.migrate(connInfo);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showError("Migration error: " + ex.getMessage());
		}
		finally {
			SwingUtil.setCursorToNormal(panel);
		}
	}

	private synchronized void preprocess(DBConnInfo connInfo) {
		SwingUtil.setCursorToWait(panel);
		try {
			actionParameterPanel.preprocess(connInfo);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showError("Preprocessing error: " + ex.getMessage());
		}
		finally {
			SwingUtil.setCursorToNormal(panel);
		}
	}

	private JPanel getJPanel() {
		if (panel == null) {
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JButton button = new JButton("Preprocess");
			buttonPanel.add(button);
			button.addActionListener(new PreProcessL());

			button = new JButton("Migrate");
			buttonPanel.add(button);
			button.addActionListener(new MigrateL());

			panel = new JPanel(new BorderLayout());
			panel.add(buttonPanel, BorderLayout.NORTH);
			panel.add(actionParameterPanel.getPanel(), BorderLayout.CENTER);
		}
		return panel;
	}
}
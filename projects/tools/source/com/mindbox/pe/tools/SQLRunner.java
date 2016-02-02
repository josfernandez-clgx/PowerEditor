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
import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.mindbox.pe.tools.db.DBConnInfo;
import com.mindbox.pe.tools.db.DBConnectionFactory;
import com.mindbox.pe.tools.db.DBConnectionInfoManager;
import com.mindbox.pe.tools.db.DBConnectionPanel;
import com.mindbox.pe.tools.util.PreferenceUtil;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class SQLRunner {

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


	public static void main(String[] args) throws Exception {
		Locale.setDefault(Locale.US);

		SwingUtil.setLookAndFeelToOS();

		LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));

		Logger logger = Logger.getLogger("SQLRunner");
		logger.info("Started");

		PreferenceUtil.initialize();

		final WindowL windowListener = new WindowL();

		DBConnectionPanel dbConnPanel = new DBConnectionPanel();
		dbConnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "PowerEditor Database Connection Settings"));

		SQLRunner instance = new SQLRunner(dbConnPanel);
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

	private class RunL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (dbConnInfoManager.getSelectedDBConnInfo() == null) {
				SwingUtil.showWarning("Please specify PowerEditor DB configuration first.");
			}
			else {
				run(dbConnInfoManager.getSelectedDBConnInfo());
			}
		}
	}


	private final DBConnectionInfoManager dbConnInfoManager;
	private final JTextArea queryTextArea;
	private final JTextArea resultArea;
	private JPanel panel = null;

	private SQLRunner(DBConnectionInfoManager dbConnInfoManager) {
		this.dbConnInfoManager = dbConnInfoManager;
		queryTextArea = new JTextArea(10, 100);
		resultArea = new JTextArea(20, 100);
		resultArea.setEditable(false);
	}

	private synchronized void run(DBConnInfo connInfo) {
		SwingUtil.setCursorToWait(panel);
		try {
			String text = queryTextArea.getText();
			if (text != null && text.length() > 0) {
				Connection conn = DBConnectionFactory.getInstance().getConnection(connInfo);
				BufferedReader in = new BufferedReader(new StringReader(text));
				StringBuffer buff = new StringBuffer();
				for (String line = in.readLine(); line != null; line = in.readLine()) {
					if (line.length() > 0) {
						buff.append(line);
					}
					if (line.endsWith(";") || line.trim().length() == 0) {
						if (line.endsWith(";")) buff.deleteCharAt(buff.length() - 1);
						runQuery(conn, buff.toString().trim());
						buff.delete(0, buff.length());
					}
				}
				if (buff.length() > 1) {
					runQuery(conn, buff.toString().trim());
				}
				DBConnectionFactory.getInstance().freeConnections();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			SwingUtil.showError("Error: " + ex.getMessage());
		}
		finally {
			SwingUtil.setCursorToNormal(panel);
		}
	}

	private void runQuery(Connection conn, String query) throws SQLException {
		if (query == null || query.trim().length() == 0) return;
		System.out.print("Executing: " + query);
		if (query.toUpperCase().startsWith("SELECT")) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = conn.prepareStatement(query);
				rs = ps.executeQuery();

				ResultSetMetaData rsmd = rs.getMetaData();
				int colCount = rsmd.getColumnCount();

				resultArea.append(System.getProperty("line.separator"));

				int count = 0;
				while (rs.next()) {
					for (int i = 1; i <= colCount; i++) {
						if (i > 1) resultArea.append(",");
						resultArea.append(rsmd.getColumnName(i) + '=' + rs.getString(i));
					}
					resultArea.append(System.getProperty("line.separator"));
					++count;
				}
				rs.close();
				rs = null;

				resultArea.append(count + " rows returned");
				resultArea.append(System.getProperty("line.separator"));
			}
			finally {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			}
		}
		else {
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(query);
				int count = ps.executeUpdate();
				resultArea.append(System.getProperty("line.separator"));
				resultArea.append("Updated " + count + " rows");
				resultArea.append(System.getProperty("line.separator"));
			}
			finally {
				if (ps != null) ps.close();
			}
		}
		System.out.println("... DONE!");
	}

	private JPanel getJPanel() {
		if (panel == null) {
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JButton button = new JButton("Run");
			buttonPanel.add(button);
			button.addActionListener(new RunL());

			JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			sp.setTopComponent(new JScrollPane(queryTextArea));
			sp.setBottomComponent(new JScrollPane(resultArea));
			sp.setMinimumSize(new Dimension(400, 400));

			panel = new JPanel(new BorderLayout());
			panel.add(buttonPanel, BorderLayout.NORTH);
			panel.add(sp, BorderLayout.CENTER);
		}
		return panel;
	}
}
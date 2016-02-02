/*
 * Created on 2004. 1. 30.
 *
 */
package com.mindbox.pe.tools.db;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mindbox.pe.tools.util.PreferenceUtil;
import com.mindbox.pe.tools.util.SwingUtil;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class DBConnectionPanel extends JPanel implements DBConnectionInfoManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1368807955372168743L;

	private class ComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object value = sessionCombo.getSelectedItem();
			if (value != null && value instanceof DBConnInfo) {
				populateFields((DBConnInfo) value);
			}
		}
	}

	private class DriverComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String value = (String) driverCombo.getSelectedItem();
			if (value != null && value.length() > 0) {
				if (value.indexOf("sqlserver") > 0) {
					connectionSampleLabel.setText("jdbc:microsoft:sqlserver://<host>:<port>;SelectMethod=Cursor;DatabaseName=<database-name>");
				}
				else if (value.indexOf("mysql") > 0) {
					connectionSampleLabel.setText("jdbc:mysql://<host>:<port>/<database-name>");
				}
				else if (value.indexOf("odbc") > 0) {
					connectionSampleLabel.setText("jdbc:odbc:<DSN-name>");
				}
				else if (value.indexOf("oracle") >= 0) {
					connectionSampleLabel.setText("jdbc:oracle:thin:@<host>:<port>:<sid>");
				}
				else {
					connectionSampleLabel.setText("");
				}
			}
		}
	}

	private DBConnInfo currDBConnInfo = null;
	private final JTextField dsnField, userField;
	private final JComboBox driverCombo;
	private final JPasswordField pwdField;
	private final DefaultComboBoxModel comboModel;
	private final JComboBox sessionCombo;
	private final JTextField connectionSampleLabel;

	public DBConnectionPanel() {
		this.dsnField = new JTextField();
		this.userField = new JTextField();
		this.pwdField = new JPasswordField();
		this.driverCombo = new JComboBox();
		driverCombo.setEditable(true);
		connectionSampleLabel = new JTextField();
		connectionSampleLabel.setEditable(false);

		comboModel = new DefaultComboBoxModel();
		sessionCombo = new JComboBox(comboModel);
		sessionCombo.addActionListener(new ComboL());

		driverCombo.addActionListener(new DriverComboL());

		initPanel();
		initCombo();
	}

	private void initCombo() {
		driverCombo.addItem("sun.jdbc.odbc.JdbcOdbcDriver");
		driverCombo.addItem("com.mysql.jdbc.Driver");
		driverCombo.addItem("oracle.jdbc.driver.OracleDriver");
		driverCombo.addItem("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		/*
		try {
			Package pkg = Package.getPackage("com.microsoft.jdbc.sqlserver");
			if (pkg != null) {
				driverCombo.addItem("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			}
		}
		catch (Exception ex) {
		}
		try {
			Package pkg = Package.getPackage("com.mysql.jdbc");
			if (pkg != null) {
				driverCombo.addItem("com.mysql.jdbc.Driver");
			}
		}
		catch (Exception ex) {
		}
		try {
			Package pkg = Package.getPackage("oracle.jdbc.driver");
			if (pkg != null) {
				driverCombo.addItem("oracle.jdbc.driver.OracleDriver");
			}
		}
		catch (Exception ex) {
		}*/
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
		SwingUtil.addComponent(this, bag, c, new JLabel("Saved Sessions:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, sessionCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("JDBC Driver:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, driverCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("Connection:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, dsnField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("(Example)"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, connectionSampleLabel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("User Name:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, userField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		SwingUtil.addComponent(this, bag, c, new JLabel("Password:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		SwingUtil.addComponent(this, bag, c, pwdField);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JButton testButton = new JButton("Test Connection");
		testButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (hasValidConnectionInfo()) {
					testConnection();
				}
				else {
					SwingUtil.showWarning("Please provide all fields with a valid connection value.");
					if (SwingUtil.isEmpty(dsnField)) {
						dsnField.requestFocus();
					}
					else if (SwingUtil.isEmpty(userField)) {
						userField.requestFocus();
					}
					else if (SwingUtil.isEmpty(pwdField)) {
						pwdField.requestFocus();
					}
				}
			}
		});

		buttonPanel.add(testButton);

		c.insets.top = 20;
		SwingUtil.addComponent(this, bag, c, buttonPanel);
	}

	public synchronized void initWithData() {
		comboModel.addElement(null);
		DBConnInfo[] savedInfo = PreferenceUtil.getSavedDBInfo();
		if (savedInfo != null) {
			for (int i = 0; i < savedInfo.length; i++) {
				comboModel.addElement(savedInfo[i]);
			}
		}
		dsnField.requestFocus();
	}

	public synchronized DBConnInfo getSelectedDBConnInfo() {
		updateCurrentInfo();
		return currDBConnInfo;
	}

	private boolean hasValidConnectionInfo() {
		return !SwingUtil.isEmpty(dsnField) && !SwingUtil.isEmpty(userField) && !SwingUtil.isEmpty(pwdField);
	}

	/**
	 * Make sure all fields have valid text.
	 *
	 */
	private void testConnection() {
		updateCurrentInfo();

		try {
			DBConnectionFactory.getInstance().getConnection(currDBConnInfo);
			SwingUtil.showInfo("Connection successfully made to " + currDBConnInfo + ".");
		}
		catch (Exception ex) {
			String msg = "Failed to connected to " + currDBConnInfo + ":\n" + ex.getMessage();
			SwingUtil.showError(msg);
		}

	}

	private void populateFields(DBConnInfo info) {
		driverCombo.setSelectedItem(info.getDriverName());
		dsnField.setText(info.getConnectionStr());
		userField.setText(info.getUser());
		pwdField.setText(info.getPassword());

		currDBConnInfo = info;
	}

	synchronized void updateFields() {
		updateCurrentInfo();
	}

	private void updateCurrentInfo() {
		if (dsnField.getText() == null || dsnField.getText().length() == 0) return;
		currDBConnInfo = DBConnInfo.newODBCConnInfo(
				(String) driverCombo.getSelectedItem(),
				dsnField.getText().trim(),
				userField.getText().trim(),
				new String(pwdField.getPassword()));

		if (PreferenceUtil.addToPreference(currDBConnInfo)) {
			comboModel.addElement(currDBConnInfo);
		}
	}

}
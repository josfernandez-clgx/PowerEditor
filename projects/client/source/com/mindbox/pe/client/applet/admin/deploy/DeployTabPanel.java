package com.mindbox.pe.client.applet.admin.deploy;

import static com.mindbox.pe.client.ClientUtil.checkDeployPermission;
import static com.mindbox.pe.client.ClientUtil.getApplet;
import static com.mindbox.pe.client.ClientUtil.getCommunicator;
import static com.mindbox.pe.client.ClientUtil.getInstance;
import static com.mindbox.pe.client.ClientUtil.getLogger;
import static com.mindbox.pe.client.ClientUtil.getLowestStatus;
import static com.mindbox.pe.client.ClientUtil.handleRuntimeException;
import static com.mindbox.pe.client.ClientUtil.printInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.filter.panel.DataFilterPanel;
import com.mindbox.pe.client.common.filter.panel.GuidelineFilterPanel;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

public class DeployTabPanel extends PanelBase {

	private static String DEPLOYMENT_ALREADY_IN_PROGRESS_MSG = "DeploymentAlreadyInProgressMsg";

	private static String MONITOR_FORM_LBL = "checkbox.deploy.monitor";
	private static String DIR_FORM_LBL = "label.deploy.directory";
	private static String PERCENT_COMPLETE_FORM_LBL = "label.deploy.percent.complete";
	private static String NUM_RULES_FORM_LBL = "label.deploy.num.rules";
	private static String NUM_OBJECTS_FORM_LBL = "label.deploy.num.objects";
	private static String NUM_ERRORS_FORM_LBL = "label.deploy.num.errors";

	private class SelectToggleL extends AbstractThreadedActionAdapter {

		private boolean selectState = false;

		public synchronized void performAction(ActionEvent event) throws Exception {
			dataFilterPanel.setAllSelectionCriteria(selectState);
			guidelineFilterPanel.setEnabledPanel(selectState);

			// clear it so that nothing is filter out when select all button is selected, except for status
			if (selectState) {
				guidelineFilterPanel.clearSelectionCriteria();
			}

			selectState = !selectState;
			selectAllButton.setText(getInstance().getLabel((selectState ? "button.select.all" : "button.select.none")));
		}
	}

	private class ShowErrorL extends AbstractThreadedActionAdapter {

		private final JPanel errorPanel;
		private final JEditorPane editorPane;

		protected ShowErrorL() {
			editorPane = new JEditorPane();
			editorPane.setEditable(false);

			errorPanel = UIFactory.createJPanel(new BorderLayout());
			errorPanel.add(new JLabel("Deploy Error Log:"), BorderLayout.NORTH);
			errorPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
			errorPanel.setPreferredSize(new Dimension(600, 440));
		}

		public void performAction(ActionEvent event) {
			try {
				editorPane.setText(getCommunicator().getDeployErrorString(runID));
				JOptionPane.showMessageDialog(getApplet(), errorPanel, "Deploy Errors", JOptionPane.PLAIN_MESSAGE);
			}
			catch (Exception exception) {
				exception.printStackTrace();
				handleRuntimeException(exception);
			}
		}
	}

	private class DeployActionAdapter extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			deployButton.setEnabled(false);
			try {
				if (!dataFilterPanel.hasSelection()) {
					getInstance().showWarning("msg.warning.empty.data.selection");
				}
				else {
					GuidelineReportFilter filter = dataFilterPanel.getFilter();

					String specifiedStatus = filter.getThisStatusAndAbove();
					if (specifiedStatus == null) {
						specifiedStatus = getLowestStatus();
					}

					// Check status-specific deploy privilege
					getLogger().info("Checking deploy permission for " + specifiedStatus);
					if (checkDeployPermission(specifiedStatus)) {

						filter.setOptimizeRuleGeneration(optimizeRuleGenCheckBox.isSelected());
						mainTab.setSelectedIndex(1);

						startLongTask();
						processDeployment(filter, exportPoliciesCheckBox.isSelected());
					}
					else {
						getInstance().showWarning("msg.warning.deploy.not.authorized", specifiedStatus);
					}
				}
			}
			catch (Exception exception) {
				handleRuntimeException(exception);
			}
			finally {
				endLongTask();
				deployButton.setEnabled(true);
			}
		}

		private void processDeployment(GuidelineReportFilter filter, boolean exportPolicies) {
			DeployResponse dr = deploy(filter, exportPolicies);

			if (dr.getGenerateRunId() > 0) {
				reset();

				boolean flag = monitorCheckBox.isSelected();
				if (flag) {
					MonitorThread monitorthread = new MonitorThread(dr.getGenerateRunId());
					monitorthread.start();
				}
				printInfo("RunId = " + dr.getGenerateRunId());
			}
			else if (dr.getGenerateRunId() < 0) {
				JOptionPane.showMessageDialog(
						getApplet(),
						getInstance().getMessage(DeployTabPanel.DEPLOYMENT_ALREADY_IN_PROGRESS_MSG),
						getInstance().getMessage("ServerErrorMsgTitle"),
						0);
				return;
			}
		}
	}

	private class MonitorThread extends Thread {

		private Runnable monitorRunnable;
		private List<GenerateStats> statsList;

		public MonitorThread(int i) {
			runID = i;
			monitorRunnable = new Runnable() {

				public void run() {
					getLogger().debug("stats = " + statsList + " runID=" + runID);
					deployDirField.setText(statsList.get(0).getDeployDir());
					deployProgressBar.setValue(statsList != null ? GenerateStats.computePercentage(statsList) : 0);
					numRulesField.setText(Integer.toString(GenerateStats.computeRuleCount(statsList)));
					numObjectsField.setText(Integer.toString(GenerateStats.computeObjectCount(statsList)));
					numErrorsField.setText(Integer.toString(GenerateStats.computeErrorCount(statsList)));
				}

			};
		}

		public void run() {
			boolean flag = true;
			getParent().setCursor(UIFactory.getWaitCursor());
			try {
				startLongTask();
				while (flag) {
					try {
						Thread.currentThread();
						Thread.sleep(500L);
						statsList = monitor(runID);
						flag = statsList != null ? GenerateStats.isRunning(statsList) : false;
						SwingUtilities.invokeLater(monitorRunnable);
					}
					catch (InterruptedException interruptedexception) {
						interruptedexception.printStackTrace();
					}
					catch (Exception ex) {
						handleRuntimeException(ex);
						getInstance().showWarning("msg.error.stop.monitor.deploy");
						flag = false;
					}
				} // while
				endLongTask();

				reportStatus();
			}
			finally {
				deployButton1.setEnabled(true);
				getParent().setCursor(UIFactory.getDefaultCursor());
			}
		}

		private void reportStatus() {
			if (GenerateStats.computeErrorCount(statsList) == 0) {
				getInstance().showInformation("msg.info.success.deploy");
				showErrorButton.setEnabled(false);
				showErrorButton.setVisible(false);
			}
			else {
				getInstance().showWarning("msg.warning.deploy.errors");
				showErrorButton.setEnabled(true);
				showErrorButton.setVisible(true);
			}
		}
	}

	// deploy components
	private final JTabbedPane mainTab;
	private final DataFilterPanel dataFilterPanel;
	private final GuidelineFilterPanel guidelineFilterPanel;
	private JButton selectAllButton;
	private final JButton deployButton, deployButton1;

	// monitor components
	private ImageIcon busyIcon, notBusyIcon;
	private JCheckBox monitorCheckBox;
	private JCheckBox exportPoliciesCheckBox;
	private JCheckBox optimizeRuleGenCheckBox;
	private JLabel statusBar, deployDirField, numRulesField, numObjectsField, numErrorsField;
	private JProgressBar deployProgressBar;
	private final JButton showErrorButton;
	private int runID = 0;

	public DeployTabPanel() {
		guidelineFilterPanel = new GuidelineFilterPanel(true);
		guidelineFilterPanel.setBorder(UIFactory.createTitledBorder(getInstance().getLabel("label.title.policy.selection.criteria")));

		dataFilterPanel = new DataFilterPanel(guidelineFilterPanel, true);
		// don't show non-applicable components
		setSpecifiedComponetsInvisible();

		selectAllButton = UIFactory.createJButton("button.select.none", null, new SelectToggleL(), null);

		mainTab = new JTabbedPane();
		mainTab.setFont(PowerEditorSwingTheme.boldFont);

		// monitor
		deployDirField = new JLabel();
		numRulesField = new JLabel();
		numObjectsField = new JLabel();
		numErrorsField = new JLabel();

		showErrorButton = UIFactory.createButton(
				getInstance().getLabel("button.display.error"),
				"image.btn.small.view",
				new ShowErrorL(),
				null);
		showErrorButton.setVisible(false);

		deployButton = UIFactory.createJButton("button.deploy", null, new DeployActionAdapter(), null);
		deployButton1 = UIFactory.createJButton("button.deploy", null, new DeployActionAdapter(), null);

		busyIcon = getInstance().makeImageIcon("image.blank");
		notBusyIcon = getInstance().makeImageIcon("image.blank");

		statusBar = new JLabel(getInstance().makeImageIcon("image.blank"));

		monitorCheckBox = UIFactory.createCheckBox(MONITOR_FORM_LBL);
		monitorCheckBox.setSelected(true);

		exportPoliciesCheckBox = UIFactory.createCheckBox("checkbox.export.policies");
		exportPoliciesCheckBox.setSelected(false);

		optimizeRuleGenCheckBox = UIFactory.createCheckBox("checkbox.optimize.rulegen");
		optimizeRuleGenCheckBox.setSelected(true);

		deployProgressBar = new JProgressBar();
		deployProgressBar.setStringPainted(true);
		deployProgressBar.setForeground(Color.blue);
		deployProgressBar.setBorder(BorderFactory.createRaisedBevelBorder());

		addComponents();

		// set filter panel checkboxes properly
		dataFilterPanel.setAllSelectionCriteria(true);
		guidelineFilterPanel.setVisibleStatusOneOrMoreField(false);
		guidelineFilterPanel.setThisStatusOrAboveStatus(Constants.DRAFT_STATUS);
	}

	protected void addComponents() {
		JPanel monitorPanel = UIFactory.createJPanel();
		addMonitorComponents(monitorPanel);

		JPanel detailPanel = UIFactory.createJPanel();
		UIFactory.setLookAndFeel(detailPanel);
		addDeployComponents(detailPanel);

		mainTab.addTab(getInstance().getLabel("label.title.deploy.details"), detailPanel);
		mainTab.addTab(getInstance().getLabel("label.title.deploy.monitor"), monitorPanel);

		setLayout(new BorderLayout(4, 4));
		add(mainTab, BorderLayout.CENTER);
	}

	private void addMonitorComponents(JPanel jpanel) {
		jpanel.setLayout(new BorderLayout());

		JPanel deployPanel = new JPanel() {
			public Insets getInsets() {
				return new Insets(20, 20, 20, 20);
			}
		};

		UIFactory.setLookAndFeel(deployPanel);
		GridBagLayout bag = new GridBagLayout();
		deployPanel.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, UIFactory.createLabel(DIR_FORM_LBL));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(deployPanel, bag, c, deployDirField);

		PanelBase.addFormSeparator(deployPanel, bag, c);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, UIFactory.createLabel(PERCENT_COMPLETE_FORM_LBL));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(deployPanel, bag, c, deployProgressBar);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, UIFactory.createLabel(NUM_RULES_FORM_LBL));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(deployPanel, bag, c, numRulesField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, UIFactory.createLabel(NUM_OBJECTS_FORM_LBL));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(deployPanel, bag, c, numObjectsField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, UIFactory.createLabel(NUM_ERRORS_FORM_LBL));
		c.gridwidth = 1;
		c.weightx = 1.0;
		PanelBase.addComponent(deployPanel, bag, c, numErrorsField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		PanelBase.addComponent(deployPanel, bag, c, showErrorButton);

		jpanel.add(deployPanel, BorderLayout.NORTH);
	}

	private void addDeployComponents(JPanel jpanel) {
		GridBagLayout bag = new GridBagLayout();
		JPanel deployPanel = UIFactory.createJPanel(bag);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(deployPanel, bag, c, deployButton);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(deployPanel, bag, c, selectAllButton);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(deployPanel, bag, c, exportPoliciesCheckBox);

		c.weightx = 0.0;
		c.gridwidth = 1;
		addComponent(deployPanel, bag, c, monitorCheckBox);

		c.weightx = 0.1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		addComponent(deployPanel, bag, c, Box.createHorizontalGlue());

		// main panel
		jpanel.setBorder(UIFactory.createTitledBorder(getInstance().getLabel("label.deploy.data")));
		jpanel.setLayout(new BorderLayout(4, 10));
		jpanel.add(deployPanel, BorderLayout.NORTH);

		JSplitPane splitPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataFilterPanel, guidelineFilterPanel);
		jpanel.add(splitPane, BorderLayout.CENTER);
	}

	void reset() {
		deployProgressBar.setValue(0);
		numRulesField.setText("");
		numObjectsField.setText("");
		numErrorsField.setText("");
	}

	void startLongTask() {
		setStatus(busyIcon);
	}

	private List<GenerateStats> monitor(int i) throws ServerException {
		return getCommunicator().retrieveDeployStats(i);
	}

	void setStatus(ImageIcon imageicon) {
		statusBar.setIcon(imageicon);
	}

	void endLongTask() {
		setStatus(notBusyIcon);
	}

	private DeployResponse deploy(GuidelineReportFilter filter, boolean exportPolicies) {
		try {
			DeployResponse dr = getCommunicator().deploy(filter, exportPolicies);
			return dr;
		}
		catch (Exception exception) {
			handleRuntimeException(exception);
			return null;
		}
	}

	private void setSpecifiedComponetsInvisible() {
		// don't show these components
		dataFilterPanel.getTemplateCheckBoxField().setVisible(false);

		dataFilterPanel.getGuidelineActionCheckBox().setVisible(false);
		dataFilterPanel.getTestConditionCheckBox().setVisible(false);
		dataFilterPanel.getDateSynonymCheckBox().setVisible(false);
		dataFilterPanel.getSecurityCheckBox().setVisible(false);

		guidelineFilterPanel.setVisibleChangesOnDateField(false);
		guidelineFilterPanel.setVisibleAttributeField(false);

	}
}
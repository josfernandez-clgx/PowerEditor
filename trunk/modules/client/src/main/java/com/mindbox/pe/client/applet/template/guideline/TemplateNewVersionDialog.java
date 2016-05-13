/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.applet.template.guideline;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.guidelines.search.TemplateReportTable;
import com.mindbox.pe.client.applet.guidelines.search.TemplateReportTableModel;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * Dialog for creating new version of templates.
 *
 * @author kim
 * @since PowerEditor  4.2.0
 */
public class TemplateNewVersionDialog extends JPanel {
	private class BackL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			--currentStep;
			showCorrectPanel();
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			dateSynonym = null;
			dialog.dispose();
		}
	}


	private class FinishL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (updateFromFields()) {
				dialog.dispose();
			}
		}
	}

	private class NextL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			doNext();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static NewTemplateCutOverDetail newTemplateVersion(final int sourceTemplateID, final GridTemplate template) {
		final TemplateNewVersionDialog instance = new TemplateNewVersionDialog(sourceTemplateID, template);
		final JDialog dialog = UIFactory.createAsModelDialog("d.title.new.template.version", instance);
		instance.dialog = dialog;

		dialog.setVisible(true);

		return new NewTemplateCutOverDetail(instance.dateSynonym, (instance.dateSynonym == null ? null : instance.getSelectedCutOverGuidelines()));
	}

	private JDialog dialog;
	private final GridTemplate template;
	private final int sourceTemplateID;
	private final JTextField versionField;
	private final DateSelectorComboField dsField;
	private DateSynonym dateSynonym = null;
	private int currentStep = 1;
	private final CardLayout card;
	private final JPanel detailPanel;
	private final JButton finishButton, backButton, nextButton, cancelButton;
	private final TemplateReportTableModel cutOverTableModel, nonCOTableModel;
	private final TemplateReportTable cutOverTemplateReporTable;
	private final JLabel versionLabel, cutoverDateLabel;

	private TemplateNewVersionDialog(final int sourceTemplateID, final GridTemplate template) {
		this.sourceTemplateID = sourceTemplateID;
		this.template = template;
		this.cutOverTableModel = new TemplateReportTableModel();
		this.nonCOTableModel = new TemplateReportTableModel();

		this.cutOverTemplateReporTable = new TemplateReportTable(cutOverTableModel);
		this.cutOverTemplateReporTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		finishButton = UIFactory.createButton("Finish", null, new FinishL(), null);
		backButton = UIFactory.createButton("Back", null, new BackL(), null);
		nextButton = UIFactory.createButton("Next", null, new NextL(), null);
		cancelButton = UIFactory.createButton("Cancel", null, new CancelL(), null);

		versionLabel = new JLabel();
		cutoverDateLabel = new JLabel();

		versionField = new JTextField();
		dsField = new DateSelectorComboField(true, true, false);

		card = new CardLayout();
		detailPanel = UIFactory.createJPanel(card);

		initPanel();
		setSize(600, 480);
		backButton.setEnabled(false);
		finishButton.setEnabled(false);
	}

	private void doNext() {
		if (updateFromFields()) {
			++currentStep;
			showCorrectPanel();
		}
	}

	private List<GuidelineReportData> getSelectedCutOverGuidelines() {
		return Collections.unmodifiableList(cutOverTemplateReporTable.getSelectedDataObjects());
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();

		// build Step 1 panel
		JPanel panel = UIFactory.createJPanel(bag);
		panel.setBorder(UIFactory.createTitledBorder("Step 1: Enter new version and cutover date"));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.version"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, versionField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.date.cutover"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, dsField);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(panel, bag, c, Box.createVerticalGlue());

		detailPanel.add(panel, "1");

		// build Step 2 panel
		bag = new GridBagLayout();
		c.weighty = 0.0;
		c.gridheight = 1;

		panel = UIFactory.createJPanel(bag);
		panel.setBorder(UIFactory.createTitledBorder("Step 2: Review Cutover Guidelines"));

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.version"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, versionLabel);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.date.cutover"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, cutoverDateLabel);

		final JSplitPane sp = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT);

		JScrollPane scrollPane = new JScrollPane(cutOverTemplateReporTable);
		scrollPane.setBorder(UIFactory.createTitledBorder("Guidelines to be cut over"));
		final JPanel topCutOverPanel = UIFactory.createBorderLayoutPanel(0, 4);
		topCutOverPanel.add(UIFactory.createFormLabel("label.select.cutover.guidelines"), BorderLayout.NORTH);
		topCutOverPanel.add(scrollPane, BorderLayout.CENTER);

		sp.setTopComponent(topCutOverPanel);

		scrollPane = new JScrollPane(new TemplateReportTable(nonCOTableModel));
		scrollPane.setBorder(UIFactory.createTitledBorder("Guidelines to remain as is"));
		sp.setBottomComponent(scrollPane);
		sp.setDividerLocation(0.5f);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(panel, bag, c, sp);

		detailPanel.add(panel, "2");

		JPanel bp = UIFactory.createFlowLayoutPanelCenterAlignment(4, 4);
		bp.add(backButton);
		bp.add(nextButton);
		bp.add(new JSeparator());
		bp.add(finishButton);
		bp.add(cancelButton);

		setLayout(new BorderLayout(4, 4));

		add(detailPanel, BorderLayout.CENTER);
		add(bp, BorderLayout.SOUTH);
	}

	private void showCorrectPanel() {
		if (currentStep == 2) {
			setCursor(UIFactory.getWaitCursor());
			try {
				versionLabel.setText(versionField.getText());
				cutoverDateLabel.setText(Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(dsField.getDate()));

				List<List<GuidelineReportData>> list = ClientUtil.getCommunicator().findCutoverGuidelines(sourceTemplateID, dsField.getValue());

				List<GuidelineReportData> cutOverList = list.get(0);
				List<GuidelineReportData> nonCutOverList = list.get(1);
				cutOverTableModel.setDataList(cutOverList);
				nonCOTableModel.setDataList(nonCutOverList);

				finishButton.setEnabled(true);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				finishButton.setEnabled(false);
				ClientUtil.getInstance().showErrorDialog("msg.error.find.cutover.guideline", new Object[] { ex.getMessage() });
				--currentStep;
			}
			finally {
				setCursor(UIFactory.getDefaultCursor());
			}
		}
		else {
			finishButton.setEnabled(false);
		}

		card.show(detailPanel, String.valueOf(currentStep));
		backButton.setEnabled(currentStep > 1);
		nextButton.setEnabled(currentStep < 2);
	}

	private boolean updateFromFields() {
		if (currentStep == 1) {
			DateSynonym dateSynonymFromPanel = dsField.getValue();
			if (UtilBase.isEmpty(versionField.getText())) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.version") });
				return false;
			}
			if (dateSynonymFromPanel == null || dateSynonymFromPanel.getName() == null || dateSynonymFromPanel.getName().length() == 0) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.date.cutover") });
				return false;
			}
			this.dateSynonym = dateSynonymFromPanel;
			this.template.setVersion(versionField.getText());
		}
		else if (currentStep == 2) {
			if (cutOverTableModel.getRowCount() < 1) {
				if (!ClientUtil.getInstance().showConfirmation("msg.question.empty.cutover.guidelines")) {
					return false;
				}
			}
			else if (getSelectedCutOverGuidelines().isEmpty()) {
				if (!ClientUtil.getInstance().showConfirmation("msg.question.empty.cutover.selection")) {
					return false;
				}
			}
		}
		return true;
	}
}
/*
 * Created on 2004. 12. 9.
 */
package com.mindbox.pe.client.applet.report;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.FileChooserField;
import com.mindbox.pe.model.report.GuidelineReportSpec;

/**
 * Request Detail Panel.
 *
 * @author kim
 * @since PowerEditor  4.2.0
 */
final class GuidelineReportSpecPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static GuidelineReportSpecPanel instance = null;

	/**
	 * Gets the one and only instance of this class.
	 * @return the only instance
	 */
	public static GuidelineReportSpecPanel getInstance() {
		if (instance == null) {
			instance = new GuidelineReportSpecPanel();
		}
		return instance;
	}

	private GuidelineReportSpec reportSpec = null;
	private final FileChooserField fileChooser;
	private final JCheckBox gridCheckBox, rowCheckBox;
	private final JCheckBox commentsCheckBox, creationDateCheckBox;
	private final JCheckBox statusCheckBox, statusChangeDateCheckBox;

	private GuidelineReportSpecPanel() {
		fileChooser = new FileChooserField(FileChooserField.Operation.SAVE, true, false);
		gridCheckBox = UIFactory.createCheckBox("checkbox.report.generate.grid");
		rowCheckBox = UIFactory.createCheckBox("checkbox.report.generate.row");
		creationDateCheckBox = UIFactory.createCheckBox("label.date.creation");
		statusCheckBox = UIFactory.createCheckBox("label.status");
		statusChangeDateCheckBox = UIFactory.createCheckBox("label.date.lastStatus");
		commentsCheckBox = UIFactory.createCheckBox("label.comments");

		initPanel();

		gridCheckBox.setSelected(true);
		rowCheckBox.setSelected(true);
		commentsCheckBox.setSelected(true);
		creationDateCheckBox.setSelected(true);
		statusCheckBox.setSelected(true);
		statusChangeDateCheckBox.setSelected(true);
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, gridCheckBox);
		UIFactory.addComponent(this, bag, c, rowCheckBox);

		JPanel fieldPanel = UIFactory.createJPanel(new GridLayout(4, 1, 2, 2));
		fieldPanel.add(creationDateCheckBox);
		fieldPanel.add(statusCheckBox);
		fieldPanel.add(statusChangeDateCheckBox);
		fieldPanel.add(commentsCheckBox);
		fieldPanel.setBorder(UIFactory.createTitledBorder("Select Fields"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, fieldPanel);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.file.target"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, fileChooser);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		UIFactory.addComponent(this, bag, c, Box.createVerticalGlue());

		setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.options")));
	}

	void setGuidelineReportSpec(GuidelineReportSpec reportSpec) {
		this.reportSpec = reportSpec;
		if (reportSpec == null) {
			clearFields();
		}
		else {
			updateFields();
		}
	}

	private void clearFields() {
		fileChooser.setValue(ClientUtil.generateReportFilename());
	}

	private void updateFields() {
		fileChooser.setValue((reportSpec.getLocalFilename() == null ? ClientUtil.generateReportFilename() : reportSpec.getLocalFilename()));
		gridCheckBox.setSelected(reportSpec.isGridOn());
		rowCheckBox.setSelected(reportSpec.isRowOn());
		commentsCheckBox.setSelected(reportSpec.isCommentsOn());
		creationDateCheckBox.setSelected(reportSpec.isCreatedDateOn());
		statusCheckBox.setSelected(reportSpec.isStatusOn());
		statusChangeDateCheckBox.setSelected(reportSpec.isStatusChangeDateOn());
	}

	GuidelineReportSpec getGuidelineReportSpec() {
		updateFromFields();
		return reportSpec;
	}

	private void updateFromFields() {
		if (reportSpec == null) {
			reportSpec = new GuidelineReportSpec();
		}
		reportSpec.setLocalFilename(fileChooser.getValue());
		reportSpec.setGridOn(gridCheckBox.isSelected());
		reportSpec.setCommentsOn(commentsCheckBox.isSelected());
		reportSpec.setCreatedDateOn(creationDateCheckBox.isSelected());
		reportSpec.setStatusChangeDateOn(statusChangeDateCheckBox.isSelected());
		reportSpec.setStatusOn(statusCheckBox.isSelected());
		reportSpec.setRowOn(rowCheckBox.isSelected());
		// append .html if it doesn't end
		if (!reportSpec.getLocalFilename().endsWith(".html")) {
			reportSpec.setLocalFilename(reportSpec.getLocalFilename() + ".html");
		}
	}
}
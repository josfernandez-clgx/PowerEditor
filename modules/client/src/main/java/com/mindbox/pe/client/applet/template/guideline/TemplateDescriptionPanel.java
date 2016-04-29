package com.mindbox.pe.client.applet.template.guideline;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.UsageTypeComboBox;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.NumberTextField;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;

/**
 * Template detail description panel.
 * 
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class TemplateDescriptionPanel extends PanelBase {

	private static final long serialVersionUID = -3951228734910107454L;

	private final JTextField nameField;
	private final JTextArea descField;
	private final UsageTypeComboBox usageTypeField;
	private final NumberTextField maxRowField;
	private final TypeEnumValueComboBox statusCombo;
	private final CheckList<GridTemplateColumn> completeColumnsCheckList;
	private final CheckList<GridTemplateColumn> consistColumnsCheckList;
	private final DefaultListModel<GridTemplateColumn> completeColumnsModel;
	private final DefaultListModel<GridTemplateColumn> consistColumnsModel;
	private final JTextArea commentField;
	private final JCheckBox fitToScreenCheckBox;
	private final JTextField versionField;
	private final JTextField templateIDField;

	TemplateDescriptionPanel() throws ServerException {
		nameField = new JTextField();
		descField = new JTextArea(4, 100);
		commentField = new JTextArea();
		// descField.setAutoscrolls(true);
		usageTypeField = UsageTypeComboBox.createInstance();
		usageTypeField.setEditable(false);

		fitToScreenCheckBox = UIFactory.createCheckBox("checkbox.template.fit.screen");

		maxRowField = new NumberTextField(12);
		statusCombo = UIFactory.createStatusComboBox(false);
		statusCombo.setEditable(false);

		versionField = new JTextField();
		templateIDField = new JTextField();
		templateIDField.setEditable(false);

		completeColumnsModel = new DefaultListModel<GridTemplateColumn>();
		consistColumnsModel = new DefaultListModel<GridTemplateColumn>();
		completeColumnsCheckList = new CheckList<GridTemplateColumn>();
		consistColumnsCheckList = new CheckList<GridTemplateColumn>();
		completeColumnsCheckList.setModel(completeColumnsModel);
		consistColumnsCheckList.setModel(consistColumnsModel);

		initPanel();
		setEditable(false);
	}

	void addDocumentListener(TemplateDetailPanel.FieldChangeListener changeListener) {
		nameField.getDocument().addDocumentListener(changeListener);
		descField.getDocument().addDocumentListener(changeListener);
		commentField.getDocument().addDocumentListener(changeListener);
		maxRowField.getDocument().addDocumentListener(changeListener);
		fitToScreenCheckBox.addActionListener(changeListener);
		statusCombo.addActionListener(changeListener);
		usageTypeField.addActionListener(changeListener);
		versionField.getDocument().addDocumentListener(changeListener);
		completeColumnsCheckList.addListSelectionListener(changeListener);
		consistColumnsCheckList.addListSelectionListener(changeListener);
	}

	public final void clearFields() {
		nameField.setText("");
		descField.setText("");
		maxRowField.setValue(0);
		statusCombo.setSelectedIndex(-1);
		usageTypeField.setSelectedIndex(-1);
		versionField.setText("");
		completeColumnsCheckList.clearSelection();
		consistColumnsCheckList.clearSelection();
		commentField.setText("");
		fitToScreenCheckBox.setSelected(false);

		setEditable(false);
	}

	void columnAdded(GridTemplate template) {
		populateCompleteAndConsistencyColumns(template);
	}

	void columnDeleted(GridTemplate template) {
		populateCompleteAndConsistencyColumns(template);
	}

	private void initPanel() {
		completeColumnsCheckList.setSelectionMode(2);
		completeColumnsCheckList.setVisibleRowCount(8);
		consistColumnsCheckList.setSelectionMode(2);
		consistColumnsCheckList.setVisibleRowCount(8);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, new JLabel("Name:"));

		c.weightx = 0.5;
		addComponent(this, bag, c, nameField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.version"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.00;
		addComponent(this, bag, c, versionField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, new JLabel("Usage Type:"));

		c.gridwidth = 1;
		c.weightx = 0.5;
		addComponent(this, bag, c, usageTypeField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, new JLabel("ID:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		addComponent(this, bag, c, templateIDField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.desc"));

		descField.setLineWrap(false);
		descField.setMinimumSize(new Dimension(100, 38));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.5;
		addComponent(this, bag, c, new JScrollPane(descField));

		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		addComponent(this, bag, c, new JLabel("Max Rows:"));

		c.weightx = 0.5;
		addComponent(this, bag, c, maxRowField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, new JLabel(" "));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		addComponent(this, bag, c, fitToScreenCheckBox);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.columns.complete"));

		c.weightx = 0.5;
		c.weighty = 0.5;
		addComponent(this, bag, c, new JScrollPane(completeColumnsCheckList));

		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.columns.consistent"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		c.weighty = 0.5;
		addComponent(this, bag, c, new JScrollPane(consistColumnsCheckList));

		commentField.setLineWrap(true);
		commentField.setColumns(80);
		commentField.setRows(10);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.0;
		addComponent(this, bag, c, new JLabel(" Comments:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, new JScrollPane(commentField));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, Box.createVerticalGlue());
	}

	private void populateCompleteAndConsistencyColumns(GridTemplate template) {
		// set completeness and consistency columns
		completeColumnsModel.clear();
		consistColumnsModel.clear();
		for (int c = 1; c <= template.getNumColumns(); c++) {
			GridTemplateColumn element = template.getColumn(c);
			if (element != null) {
				completeColumnsModel.addElement(element);
				consistColumnsModel.addElement(element);
			}
		}
		completeColumnsCheckList.clearSelection();
		int[] compColumns = template.getCompletenessColumns();
		if (compColumns != null && compColumns.length > 0) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (UtilBase.isMember(element.getColumnNumber(), compColumns)) {
					completeColumnsCheckList.setSelectedValue(element, true);
				}
			}
		}
		consistColumnsCheckList.clearSelection();
		int[] consistColumns = template.getCompletenessColumns();
		if (consistColumns != null && consistColumns.length > 0) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();
				if (UtilBase.isMember(element.getColumnNumber(), consistColumns)) {
					consistColumnsCheckList.setSelectedValue(element, true);
				}
			}
		}
	}

	public final void populateDefaults(TemplateUsageType usageType) {
		versionField.setText(GridTemplate.DEFAULT_VERSION);
		statusCombo.setSelectedIndex(0);
		maxRowField.setValue(99);
		if (usageType == null) {
			usageTypeField.setSelectedIndex(-1);
		}
		else {
			usageTypeField.setSelectedItem(usageType);
		}
		fitToScreenCheckBox.setSelected(true);
	}

	public final void populateFields(GridTemplate template) {
		templateIDField.setText(String.valueOf(template.getID()));
		nameField.setText(template.getName());
		descField.setText(template.getDescription());
		if (template.getDescription() != null) {
			descField.setCaretPosition(0);
		}
		maxRowField.setValue(template.getMaxNumOfRows());
		statusCombo.selectTypeEnumValue(template.getStatus());
		usageTypeField.selectUsage(template.getUsageType());
		versionField.setText(template.getVersion());
		commentField.setText(template.getComment());
		fitToScreenCheckBox.setSelected(template.fitToScreen());

		populateCompleteAndConsistencyColumns(template);

		setEditable(true);
	}

	void refreshColumns(GridTemplate template) {
		populateCompleteAndConsistencyColumns(template);
	}

	final void selectNameField() {
		nameField.selectAll();
		nameField.requestFocusInWindow();
	}

	public final void setEditable(boolean editable) {
		if (editable && !ClientUtil.checkViewOrEditAnyTemplatePermission()) return;
		usageTypeField.setEnabled(editable);
		versionField.setEnabled(editable);
		nameField.setEditable(editable);
		descField.setEditable(editable);
		maxRowField.setEditable(editable);
		usageTypeField.setEnabled(editable);
		statusCombo.setEnabled(editable);
		commentField.setEnabled(editable);
		fitToScreenCheckBox.setEnabled(editable);
		consistColumnsCheckList.setEnabled(editable);
		completeColumnsCheckList.setEnabled(editable);
	}

	public final void updateFromFields(GridTemplate td) throws ValidationException {
		logger.debug(">>> updateFromFields: " + td);

		Util.checkEmpty(nameField, "label.name");
		Util.checkEmpty(usageTypeField, "label.usage.type");
		Util.checkEmpty(versionField, "label.version");
		Util.checkEmpty(statusCombo, "label.status");

		td.setName(nameField.getText());
		td.setDescription(descField.getText());
		td.setMaxNumOfRows((maxRowField.getValue() == null ? 0 : maxRowField.getValue().intValue()));
		td.setStatus(statusCombo.getSelectedEnumValueValue());
		td.setComment(commentField.getText());
		td.setFitToScreen(fitToScreenCheckBox.isSelected());
		td.setVersion(versionField.getText());

		final List<Integer> intList = new ArrayList<Integer>();
		for (GridTemplateColumn column : completeColumnsCheckList.getSelectedValuesList()) {
			intList.add(column.getColumnNumber());
		}
		td.setCompletenessColumns(UtilBase.toIntArray(intList));

		intList.clear();
		for (GridTemplateColumn column : consistColumnsCheckList.getSelectedValuesList()) {
			intList.add(column.getColumnNumber());
		}
		td.setConsistencyColumns(UtilBase.toIntArray(intList));

		// update usage type, if changed
		if (usageTypeField.getSelectedUsage() != td.getUsageType()) {
			td.setUsageType(usageTypeField.getSelectedUsage());
		}
	}
}

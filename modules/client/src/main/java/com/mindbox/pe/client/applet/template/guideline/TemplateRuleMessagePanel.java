package com.mindbox.pe.client.applet.template.guideline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.common.MessageEditDialog;
import com.mindbox.pe.client.applet.template.common.MessageTableModel;
import com.mindbox.pe.client.applet.template.rule.PowerEditPanel;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.event.Action3Adapter;
import com.mindbox.pe.common.validate.WarningConsumer;
import com.mindbox.pe.common.validate.WarningInfo;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.MessageContainer;
import com.mindbox.pe.model.template.RuleMessageContainer;
import com.mindbox.pe.model.template.TemplateMessageDigest;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
class TemplateRuleMessagePanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static final MessageFormat validateMessageFormat = new MessageFormat("* Validataion Failed *" + System.getProperty("line.separator") + System.getProperty("line.separator")
			+ "Error Type   : {0}" + System.getProperty("line.separator") + "Error Message: {1}" + System.getProperty("line.separator") + System.getProperty("line.separator"));

	private class ValidateRuleL implements ActionListener, WarningConsumer {

		final List<String> warningList;
		final JTextArea textArea;

		public ValidateRuleL() {
			warningList = new LinkedList<String>();
			textArea = new JTextArea();
			textArea.setAutoscrolls(true);
			textArea.setEditable(false);
			textArea.setBackground(new Color(255, 162, 162));
			textArea.setOpaque(true);
		}

		public void addWarning(int level, String message) {
			warningList.add(WarningInfo.toString(level) + ": " + message);
		}

		public void addWarning(int level, String message, String resource) {
			warningList.add(WarningInfo.toString(level) + ": " + message + " at " + resource);
		}

		@SuppressWarnings("unused")
		String toErrorMessage(Throwable ex) {
			String errorName = ex.getClass().getName().substring(ex.getClass().getName().lastIndexOf(".") + 1);
			return validateMessageFormat.format(new Object[] { errorName, ex.getMessage() });
		}

		public void actionPerformed(ActionEvent e) {
			// TBD: no need to validate rule syntax
			//      validate column and parameter references
		}
	}

	private class MessageActionL extends Action3Adapter {

		public void deletePerformed(ActionEvent e) {
			if (getSelectedMessage() != null) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.message")) {
					currentScope.removeMessageDigest(getSelectedMessage());
					messageTableModel.removeRow(getSelectedMessage());
				}
			}
		}

		public void editPerformed(ActionEvent e) {
			TemplateMessageDigest digest = getSelectedMessage();
			if (digest != null) {
				TemplateMessageDigest changedDigest = MessageEditDialog.editTemplateMessageDigest(
						JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
						currentScope instanceof GridTemplate,
						template,
						digest,
						ruleForTemplateRadio.isSelected());
				if (changedDigest != null) {
					messageTableModel.updateRow(messageTable.getSelectedRow());
				}
			}
		}

		public void newPerformed(ActionEvent e) {
			TemplateMessageDigest digest = MessageEditDialog.createTemplateMessageDigest(
					JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
					currentScope instanceof GridTemplate,
					template,
					ruleForTemplateRadio.isSelected());
			if (digest != null) {
				currentScope.addMessageDigest(digest);
				messageTableModel.addRow(digest);
			}
		}
	}

	private class RuleForColumnComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			updateAll();

			if (columnCombo.getSelectedItem() != null) {
				setCurrentScope(getSelectedColumn());
			}
			else {
				setCurrentScope(null);
			}
		}
	}

	private class RuleForColumnRadioL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			logger.debug("forColumnRadio: >>> actionPerformed");
			if (ruleForColumnRadio.isSelected()) {
				updateAll();

				logger.debug("forColumnRaiod: setting fields...");
				columnCombo.setEnabled(true);
				setCurrentScope(getSelectedColumn());
			}
			logger.debug("forColumnRadio: <<< actionPerformed");
		}
	}

	private class RuleForTemplateRadioL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (ruleForTemplateRadio.isSelected()) {
				updateAll();

				setCurrentScope(template);
				columnCombo.setEnabled(false);
			}
		}
	}

	private final JRadioButton ruleForTemplateRadio, ruleForColumnRadio;
	private final JComboBox columnCombo;
	private final JButton validateRuleButton;
	private final ButtonPanel messageButtonPanel;

	private boolean editable = false;
	private RuleMessageContainer currentScope = null;
	private GridTemplate template = null;
	private final PowerEditPanel powerEditPanel = new PowerEditPanel(null);
	private final JTable messageTable;
	private final MessageTableModel messageTableModel;
	private final TemplateDetailPanel detailPanel;

	TemplateRuleMessagePanel(TemplateDetailPanel detailPanel) {
		this.detailPanel = detailPanel;
		messageTableModel = new MessageTableModel(ClientUtil.getEntityTypeForMessageContext());
		messageTable = new JTable(messageTableModel);
		messageTable.setColumnSelectionAllowed(false);
		messageTable.setRowSelectionAllowed(true);

		for (int i = 0; i < messageTable.getColumnCount(); i++) {
			TableColumn column = messageTable.getColumnModel().getColumn(i);
			if (i >= messageTable.getColumnCount() - 1) {
				column.setPreferredWidth(400);
			}
			else {
				column.setPreferredWidth(80);
			}
		}

		ruleForTemplateRadio = new JRadioButton("Template");
		ruleForTemplateRadio.setSelected(true);
		ruleForColumnRadio = new JRadioButton("Column: ");
		columnCombo = new JComboBox(new String[] { " 1 - Column Name" });
		columnCombo.setEnabled(false);

		ButtonGroup ruleForGroup = new ButtonGroup();
		ruleForGroup.add(ruleForTemplateRadio);
		ruleForGroup.add(ruleForColumnRadio);

		validateRuleButton = UIFactory.createButton("", "image.btn.small.validate", new ValidateRuleL(), null);
		validateRuleButton.setToolTipText("Validate Rule");

		messageButtonPanel = UIFactory.create3ButtonPanel(new MessageActionL(), true);

		initPanel();

		ruleForTemplateRadio.addActionListener(new RuleForTemplateRadioL());
		ruleForColumnRadio.addActionListener(new RuleForColumnRadioL());
		columnCombo.addActionListener(new RuleForColumnComboL());

		setEditable(false);

		messageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				messageButtonPanel.setEnabledSelectionAwareButtons(messageTable.getSelectedRow() > -1);
			}
		});
	}

	private void addDocumentListener(TemplateDetailPanel.FieldChangeListener changeListener) {
		messageTableModel.addTableModelListener(changeListener);
		powerEditPanel.addRuleChangeListener(changeListener);
	}

	private void removeDocumentListener(TemplateDetailPanel.FieldChangeListener changeListener) {
		messageTableModel.removeTableModelListener(changeListener);
		powerEditPanel.removeRuleChangeListener(changeListener);
	}

	private void initPanel() {
		JPanel rulePanel = new JPanel(new BorderLayout(0, 0));
		rulePanel.setBorder(BorderFactory.createTitledBorder("Deployment Rule"));
		rulePanel.add(powerEditPanel, BorderLayout.CENTER);
		rulePanel.setMinimumSize(new Dimension(200, 150));

		//		GridBagLayout bag = new GridBagLayout();
		JPanel messagePanel = new JPanel(new BorderLayout(0, 0)); //bag);
		messagePanel.setBorder(BorderFactory.createTitledBorder("Messages"));
		messagePanel.add(messageButtonPanel, BorderLayout.NORTH);
		messagePanel.add(new JScrollPane(messageTable), BorderLayout.CENTER);
		messagePanel.setMinimumSize(new Dimension(200, 100));
		messagePanel.setPreferredSize(new Dimension(200, 208));

		JSplitPane ruleMsgSplitPane = UIFactory.createSplitPane(JSplitPane.VERTICAL_SPLIT);
		ruleMsgSplitPane.setResizeWeight(1.0);
		ruleMsgSplitPane.setTopComponent(rulePanel);
		ruleMsgSplitPane.setBottomComponent(messagePanel);

		JPanel ruleMsgNorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ruleMsgNorthPanel.add(new JLabel("Rule and Message Scope: "));
		ruleMsgNorthPanel.add(ruleForTemplateRadio);
		ruleMsgNorthPanel.add(ruleForColumnRadio);
		ruleMsgNorthPanel.add(columnCombo);

		setLayout(new BorderLayout(0, 0));
		add(ruleMsgNorthPanel, BorderLayout.NORTH);
		add(ruleMsgSplitPane, BorderLayout.CENTER);
	}

	private TemplateMessageDigest getSelectedMessage() {
		if (messageTable.getSelectedRow() >= 0) {
			return (TemplateMessageDigest) messageTableModel.getValueAt(messageTable.getSelectedRow(), -1);
		}
		else {
			return null;
		}
	}

	private GridTemplateColumn getSelectedColumn() {
		String str = (String) columnCombo.getSelectedItem();
		if (str == null || str.length() == 0) {
			return null;
		}
		else {
			int colIndex = Integer.parseInt(str.substring(0, str.indexOf(" ")));
			return template.getColumn(colIndex);
		}
	}

	public void clearFields() {
		powerEditPanel.clearFields();
		messageTableModel.removeAllRows();
		setEditable(false);
	}

	/**
	 * template must not be null.
	 * @param template
	 */
	public synchronized void populateFields(GridTemplate template) {
		populateFields_internal(template);
	}

	private void populateFields_internal(GridTemplate template) {
		this.template = template;

		ruleForTemplateRadio.setEnabled(true);
		columnCombo.setEnabled(false);
		ruleForColumnRadio.setEnabled(true);
		ruleForTemplateRadio.setSelected(true);

		currentScope = null;
		columnCombo.removeAllItems();

		boolean hasColumnRule = false;
		List<GridTemplateColumn> columnList = template.getColumns();
		if (columnList.size() > 0) {
			for (Iterator<GridTemplateColumn> iter = columnList.iterator(); iter.hasNext();) {
				GridTemplateColumn column = iter.next();
				columnCombo.addItem(column.getID() + " " + column.getTitle());
				if (!hasColumnRule) {
					hasColumnRule = column.hasMessageDigest();
				}
			}
		}

		setCurrentScope(template);


		if (template.getRuleDefinition() == null && hasColumnRule) {
			// set template rule
			ruleForColumnRadio.setSelected(true);
			columnCombo.setEnabled(true);
			setCurrentScope(getSelectedColumn());
		}
		setEditable(true);
	}

	private void populateMessages(MessageContainer messageContainer) {

		messageTableModel.removeAllRows();
		for (Iterator<TemplateMessageDigest> iter = messageContainer.getAllMessageDigest().iterator(); iter.hasNext();) {
			TemplateMessageDigest element = iter.next();
			messageTableModel.addRow(element);
		}
	}

	void setEditable(boolean editable) {
		this.editable = editable;
		messageButtonPanel.setEnabled(editable);
		if (editable) {
			messageButtonPanel.setEnabledSelectionAwareButtons(messageTable.getSelectedRow() >= 0);
		}
		if (editable && !ClientUtil.checkViewOrEditAnyTemplatePermission()) return;

		powerEditPanel.setEnabled(editable);
		validateRuleButton.setEnabled(editable);
	}

	public synchronized void refreshColumns() {
		populateFields_internal(this.template);
	}

	private synchronized void setCurrentScope(RuleMessageContainer entity) {
		removeDocumentListener(detailPanel.getFieldChangeListener());
		try {
			this.currentScope = entity;
			if (entity != null) {
				setRule(entity.getRuleDefinition());
				populateMessages(entity);
			}
			else {
				powerEditPanel.clearFields();
				clearFields();
			}
		}
		finally {
			addDocumentListener(detailPanel.getFieldChangeListener());
		}

	}

	private final void setRule(RuleDefinition ruleDef) {
		powerEditPanel.setRule(template, ruleDef);
	}

	public void updateFromFields() throws ValidationException {
		updateAll();
	}

	private synchronized void updateAll() {
		logger.debug(">>> updateAll");
		updateRule();
		logger.debug("<<< updateAll");
	}

	private void updateRule() {
		if (this.editable && this.currentScope != null) {
			powerEditPanel.updateRuleFromFields();
			this.currentScope.setRuleDefinition(powerEditPanel.getRule());
		}
	}

}
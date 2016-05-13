package com.mindbox.pe.client.applet.template.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainPanel;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.GenericEntityComboBox;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.DialogFactory;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.validate.WarningConsumer;
import com.mindbox.pe.common.validate.WarningInfo;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.TemplateMessageDigest;


/**
 * @author kim
 * @author MindBox
 * @since PowerEditor
 */
public class MessageEditDialog extends JPanel {
	private class AcceptL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (updateMessage()) {
				dialog.dispose();
			}
		}
	}

	private class BrowseAttributeL implements ActionListener {
		final JTextArea textArea;
		private final boolean forMessage;

		public BrowseAttributeL(JTextArea textArea) {
			this(textArea, false);
		}

		public BrowseAttributeL(JTextArea textArea, boolean forMessage) {
			this.textArea = textArea;
			this.forMessage = forMessage;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			pasteValue(DialogFactory.showAttributeSelector(null));
		}

		void pasteValue(String val) {
			if ((val != null) && (val.length() > 0)) {
				String prevText = textArea.getText();
				int pos = textArea.getCaretPosition();

				if (forMessage) {
					textArea.setText(prevText.substring(0, pos) + "|" + val + "|" + prevText.substring(pos));
					textArea.setCaretPosition(pos + val.length() + 2);
				}
				else {
					textArea.setText(prevText.substring(0, pos) + val + prevText.substring(pos));
					textArea.setCaretPosition(pos + val.length());
				}

				textArea.requestFocus();
			}
		}
	}

	private class BrowseColumnL extends BrowseAttributeL {
		public BrowseColumnL(JTextArea textArea) {
			super(textArea);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] columnValues = new String[template.getNumColumns()];

			for (int i = 1; i <= columnValues.length; i++) {
				columnValues[i - 1] = i + " " + template.getColumn(i).getTitle();
			}

			String value = (String) JOptionPane.showInputDialog(ClientUtil.getApplet(), null, "Select Column", JOptionPane.PLAIN_MESSAGE, null, columnValues, null);

			if (value != null) {
				int colIndex = Integer.parseInt(value.substring(0, value.indexOf(" ")));
				pasteValue("%column " + colIndex + "%");
			}
		}
	}

	private class CancelL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			digest = null;
			dialog.dispose();
		}
	}

	private class InsertColMessagesL extends BrowseAttributeL {
		public InsertColMessagesL(JTextArea textArea) {
			super(textArea);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] columnValues = new String[template.getNumColumns()];
			for (int i = 1; i <= columnValues.length; i++) {
				columnValues[i - 1] = i + " " + template.getColumn(i).getTitle();
			}

			JList<String> jlist = new JList<String>(columnValues);
			JPanel selectionPanel = createListSelectionPanel(jlist);

			StringBuilder buf = new StringBuilder();
			int option = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), selectionPanel, "Select Column", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

			if (option == JOptionPane.OK_OPTION) {
				List<String> selections = jlist.getSelectedValuesList();
				if (!selections.isEmpty()) {
					buf.append("%columnMessages(");

					boolean first = true;
					for (String str : selections) {
						int colIndex = Integer.parseInt(str.substring(0, str.indexOf(" ")));
						buf.append(colIndex);

						if (!first) {
							buf.append(",");
						}
						if (first) {
							first = false;
						}
					}

					buf.append(")%");
					pasteValue(buf.toString());
				}
			}
		}

		private JPanel createListSelectionPanel(JList<String> jlist) {
			JPanel selectionPanel = new JPanel(new BorderLayout(4, 4));
			jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			selectionPanel.add(new JScrollPane(jlist), BorderLayout.CENTER);
			return selectionPanel;
		}
	}

	private class ValidateMessageL extends ValidateRuleL {
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((messageText.getText() != null) && (messageText.getText().length() > 0)) {
				warningList.clear();

				if (Validator.validateMessage(messageText.getText(), !forTemplate, columnSize, this)) {
					JOptionPane.showMessageDialog(ClientUtil.getApplet(), "The message is valid.", "Validation Result", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					StringBuilder buff = new StringBuilder();
					buff.append("There are " + warningList.size() + " error(s).");
					buff.append(System.getProperty("line.separator"));

					for (Iterator<String> iter = warningList.iterator(); iter.hasNext();) {
						buff.append(iter.next());
						buff.append(System.getProperty("line.separator"));
					}

					textArea.setText(buff.toString());
					textArea.setCaretPosition(0);
					JOptionPane.showMessageDialog(ClientUtil.getApplet(), new JScrollPane(textArea), "Validation Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

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

		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void addWarning(int level, String message) {
			warningList.add(WarningInfo.toString(level) + ": " + message);
		}

		@Override
		public void addWarning(int level, String message, String resource) {
			warningList.add(WarningInfo.toString(level) + ": " + message + " at " + resource);
		}

		@SuppressWarnings("unused")
		String toErrorMessage(Throwable ex) {
			String errorName = ex.getClass().getName().substring(ex.getClass().getName().lastIndexOf(".") + 1);

			return validateMessageFormat.format(new Object[] { errorName, ex.getMessage() });
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private static final MessageFormat validateMessageFormat = new MessageFormat(
			"* Validation Failed *" + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error Type   : {0}" + System.getProperty("line.separator")
					+ "Error Message: {1}" + System.getProperty("line.separator") + System.getProperty("line.separator"));

	public static TemplateMessageDigest createTemplateMessageDigest(Frame owner, boolean forTemplate, GridTemplate template, boolean displayConditionalDelims) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("New Message");

		MessageEditDialog panel = new MessageEditDialog(dialog, forTemplate, template, null, displayConditionalDelims);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.digest;
	}

	public static TemplateMessageDigest editTemplateMessageDigest(Frame owner, boolean forTemplate, GridTemplate template, TemplateMessageDigest digest,
			boolean displayConditionalDelims) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Edit Message");

		MessageEditDialog panel = new MessageEditDialog(dialog, forTemplate, template, digest, displayConditionalDelims);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.digest;
	}

	private TemplateMessageDigest digest = null;
	private final GenericEntityComboBox entityCombo;
	private final JTextField condDelimField;
	private final JTextField condFinalDelimField;
	private final JTextArea messageText;
	private final JDialog dialog;
	private final JButton validateMessageButton;
	private final JButton browseMessageElemButton;
	private final JButton browseMessageColButton;
	private final JButton insertColMessagesButton;
	private final JComboBox<String> columnCombo;
	private final int columnSize;
	private final boolean forTemplate;
	private final GridTemplate template;

	private MessageEditDialog(JDialog dialog, boolean forTemplate, GridTemplate template, TemplateMessageDigest digest, boolean displayConditionalDelims) {
		this.dialog = dialog;
		this.digest = digest;
		this.template = template;
		this.columnSize = template.getNumColumns();
		this.forTemplate = forTemplate;
		this.entityCombo = ((ClientUtil.getEntityTypeForMessageContext() == null) ? null : new GenericEntityComboBox(ClientUtil.getEntityTypeForMessageContext(), true, null));
		messageText = new JTextArea();
		messageText.setColumns(80);
		messageText.setRows(4);

		condDelimField = new JTextField();
		condFinalDelimField = new JTextField();

		columnCombo = new JComboBox<String>(new String[] { " 1 - Column Name" });
		columnCombo.setEnabled(false);

		validateMessageButton = UIFactory.createButton("", "image.btn.small.validate", new ValidateMessageL(), null);
		validateMessageButton.setToolTipText("Validate Message");

		browseMessageElemButton = UIFactory.createButton("", "image.btn.find.attribute", new BrowseAttributeL(messageText, true), null);
		browseMessageElemButton.setToolTipText("Paste Attribute Reference for Message...");
		browseMessageColButton = UIFactory.createButton("", "image.btn.find.column", new BrowseColumnL(messageText), null);
		browseMessageColButton.setToolTipText("Paste Column Reference for Rule...");
		insertColMessagesButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.insert.column.messages"), null, new InsertColMessagesL(messageText), null);
		insertColMessagesButton.setToolTipText("Insert Column Messages...");
		insertColMessagesButton.setVisible(displayConditionalDelims);

		initPanel();

		populateFields();

		setSize(440, 320);
	}

	private void initPanel() {
		JPanel messageMsgButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		messageMsgButtonPanel.add(validateMessageButton);
		messageMsgButtonPanel.add(browseMessageElemButton);
		messageMsgButtonPanel.add(browseMessageColButton);
		messageMsgButtonPanel.add(insertColMessagesButton);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		if (entityCombo != null) {
			c.gridwidth = 1;
			c.weightx = 0.0;
			PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel(entityCombo.getGenericEntityType()));

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			PanelBase.addComponent(this, bag, c, entityCombo);
		}

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.delim.cond"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, condDelimField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.delim.cond.final"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, condFinalDelimField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, new JLabel(""));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, messageMsgButtonPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.message.text"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		PanelBase.addComponent(this, bag, c, new JScrollPane(messageText));

		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		PanelBase.addComponent(this, bag, c, new JSeparator());
		c.weighty = 0.0;
		c.insets.top = 16;
		c.insets.bottom = 8;
		PanelBase.addComponent(this, bag, c, buttonPanel);
	}

	private void populateFields() {
		if (digest != null) {
			if (entityCombo != null) {
				entityCombo.selectGenericEntity(digest.getEntityID());
			}

			condDelimField.setText(digest.getConditionalDelimiter());
			condFinalDelimField.setText(digest.getConditionalFinalDelimiter());
			messageText.setText(digest.getText());
		}
		else {
			MessageConfiguration msgConfig = ((MainPanel) ClientUtil.getParent()).getUserSession().getDefaultCondMsg(template.getUsageType());

			if (msgConfig != null) {
				condDelimField.setText(msgConfig.getConditionalDelimiter());
				condFinalDelimField.setText(msgConfig.getConditionalFinalDelimiter());
			}
		}
	}

	private boolean updateMessage() {
		if ((messageText.getText() == null) || (messageText.getText().length() == 0)) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "message text" });
			return false;
		}

		if (digest == null) {
			digest = new TemplateMessageDigest();
		}
		digest.setText(messageText.getText());

		if (entityCombo != null) {
			digest.setEntityID(entityCombo.getSelectedGenericEntityID());
		}
		digest.setConditionalDelimiter(condDelimField.getText());
		digest.setConditionalFinalDelimiter(condFinalDelimField.getText());
		return true;
	}
}

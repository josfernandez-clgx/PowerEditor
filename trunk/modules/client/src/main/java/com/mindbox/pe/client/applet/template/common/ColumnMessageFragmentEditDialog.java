/*
 * Created on 2004. 8. 9.
 */
package com.mindbox.pe.client.applet.template.common;

import java.awt.CardLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.DialogFactory;
import com.mindbox.pe.common.validate.WarningConsumer;
import com.mindbox.pe.common.validate.WarningInfo;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfigType;
import com.mindbox.pe.xsd.config.RangeStyleType;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public class ColumnMessageFragmentEditDialog extends JPanel {
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
			if (val != null && val.length() > 0) {
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

	private class ValidateMessageL extends ValidateRuleL {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (messageText.getText() != null && messageText.getText().length() > 0) {
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

	private static final long serialVersionUID = -3951228734910107454L;

	private static final MessageFormat validateMessageFormat = new MessageFormat(
			"* Validataion Failed *" + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error Type   : {0}" + System.getProperty("line.separator")
					+ "Error Message: {1}" + System.getProperty("line.separator") + System.getProperty("line.separator"));


	public static ColumnMessageFragmentDigest createColumnMessageFragmentDigest(Frame owner, GridTemplate template, int columnNo) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("New Column Message");
		ColumnMessageFragmentEditDialog panel = new ColumnMessageFragmentEditDialog(dialog, template, columnNo, null);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.digest;
	}

	public static ColumnMessageFragmentDigest editColumnMessageFragmentDigest(Frame owner, GridTemplate template, int columnNo, ColumnMessageFragmentDigest digest) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Edit Column Message");
		ColumnMessageFragmentEditDialog panel = new ColumnMessageFragmentEditDialog(dialog, template, columnNo, digest);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.digest;
	}

	private ColumnMessageFragmentDigest digest = null;
	private final JComboBox<String> typeCombo;
	private final JComboBox<String> rangeStyleCombo;
	private final JComboBox<String> cellSelectionCombo;
	private final JTextField enumDelimField;
	private final JTextField enumFinalDelimField;
	private final JTextField enumPrefixField;
	private final JTextArea messageText;
	private final JDialog dialog;
	private final JButton validateMessageButton;
	private final JButton browseMessageElemButton;
	private final JButton browseMessageColButton;
	private final JComboBox<String> columnCombo;
	private final int columnSize;
	private final boolean forTemplate;
	private final GridTemplate template;
	private final CardLayout card;
	private final JPanel detailPanel;
	private final int columnNo;

	private ColumnMessageFragmentEditDialog(JDialog dialog, GridTemplate template, int columnNo, ColumnMessageFragmentDigest digest) {
		this.dialog = dialog;
		this.digest = digest;
		this.template = template;
		this.columnSize = template.getNumColumns();
		this.forTemplate = true;
		this.columnNo = columnNo;

		this.typeCombo = new JComboBox<String>(new String[] { MessageConfigType.ANY.value(), MessageConfigType.ENUM.value(), MessageConfigType.RANGE.value() });
		this.rangeStyleCombo = new JComboBox<String>(new String[] { RangeStyleType.SYMBOLIC.value(), RangeStyleType.VERBOSE.value(), RangeStyleType.BRACKETED.value() });
		this.cellSelectionCombo = new JComboBox<String>(
				new String[] {
						CellSelectionType.DEFAULT.value(),
						CellSelectionType.ENUM_INCLUDE_MULTIPLE.value(),
						CellSelectionType.ENUM_EXCLUDE_SINGLE.value(),
						CellSelectionType.ENUM_EXCLUDE_MULTIPLE.value() });

		messageText = new JTextArea();
		messageText.setColumns(80);
		messageText.setRows(4);

		enumDelimField = new JTextField();
		enumFinalDelimField = new JTextField();
		enumPrefixField = new JTextField();

		columnCombo = new JComboBox<String>(new String[] { " 1 - Column Name" });
		columnCombo.setEnabled(false);

		validateMessageButton = UIFactory.createButton("", "image.btn.small.validate", new ValidateMessageL(), null);
		validateMessageButton.setToolTipText("Validate Message");

		browseMessageElemButton = UIFactory.createButton("", "image.btn.find.attribute", new BrowseAttributeL(messageText, true), null);
		browseMessageElemButton.setToolTipText("Paste Attribute Reference for Message...");
		browseMessageColButton = UIFactory.createButton("", "image.btn.find.column", new BrowseColumnL(messageText), null);
		browseMessageColButton.setToolTipText("Paste Column Reference for Rule...");

		card = new CardLayout(0, 0);
		detailPanel = UIFactory.createJPanel(card);
		detailPanel.add(new JLabel(""), MessageConfigType.ANY.value());

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		GridBagLayout bag = new GridBagLayout();
		JPanel panel = UIFactory.createJPanel(bag);
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(panel, bag, c, UIFactory.createFormLabel("label.range.style"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(panel, bag, c, rangeStyleCombo);
		detailPanel.add(panel, MessageConfigType.RANGE.value());

		bag = new GridBagLayout();
		panel = UIFactory.createJPanel(bag);
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(panel, bag, c, UIFactory.createFormLabel("label.cell.selection"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(panel, bag, c, cellSelectionCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(panel, bag, c, UIFactory.createFormLabel("label.delim.enum"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(panel, bag, c, enumDelimField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(panel, bag, c, UIFactory.createFormLabel("label.delim.enum.final"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(panel, bag, c, enumFinalDelimField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(panel, bag, c, UIFactory.createFormLabel("label.prefix.enum"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(panel, bag, c, enumPrefixField);

		detailPanel.add(panel, MessageConfigType.ENUM.value());

		initPanel();

		setSize(480, 420);

		typeCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (typeCombo.getSelectedItem() != null) {
					card.show(detailPanel, (String) typeCombo.getSelectedItem());
				}
			}
		});

		populateFields();
	}

	private void initPanel() {
		JPanel messageMsgButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		messageMsgButtonPanel.add(validateMessageButton);
		messageMsgButtonPanel.add(browseMessageElemButton);
		messageMsgButtonPanel.add(browseMessageColButton);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.template"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, new JLabel(template.getName()));

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.column"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, new JLabel(template.getColumn(columnNo).getTitle()));

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, UIFactory.createFormLabel("label.type"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, typeCombo);

		PanelBase.addComponent(this, bag, c, detailPanel);

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
			typeCombo.setSelectedItem(digest.getType());
			rangeStyleCombo.setSelectedItem(digest.getRangeStyle());
			String cellSelection = digest.getCellSelection() == null ? null : digest.getCellSelection().value();
			if (cellSelection == null) {
				cellSelection = CellSelectionType.DEFAULT.value();
			}
			cellSelectionCombo.setSelectedItem(cellSelection);
			enumDelimField.setText(digest.getEnumDelimiter());
			enumFinalDelimField.setText(digest.getEnumFinalDelimiter());
			enumPrefixField.setText(digest.getEnumPrefix());
			messageText.setText(digest.getText());
		}
	}

	private boolean updateMessage() {
		if (messageText.getText() == null || messageText.getText().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "message text" });
			return false;
		}

		if (digest == null) {
			digest = new ColumnMessageFragmentDigest();
		}
		digest.setText(messageText.getText());
		final String typeValue = String.class.cast(typeCombo.getSelectedItem());
		digest.setType(MessageConfigType.fromValue(typeValue));
		if (digest.getType() == MessageConfigType.RANGE) {
			digest.setRangeStyle(RangeStyleType.fromValue(String.class.cast(rangeStyleCombo.getSelectedItem())));
			digest.setCellSelection(null);
			digest.setEnumDelimiter(null);
			digest.setEnumFinalDelimiter(null);
			digest.setEnumPrefix(null);
		}
		else if (digest.getType() == MessageConfigType.ENUM) {
			digest.setCellSelection(CellSelectionType.fromValue(String.class.cast(cellSelectionCombo.getSelectedItem())));
			digest.setEnumDelimiter(enumDelimField.getText());
			digest.setEnumFinalDelimiter(enumFinalDelimField.getText());
			digest.setEnumPrefix(enumPrefixField.getText());
			digest.setRangeStyle(null);
		}
		else {
			digest.setRangeStyle(null);
			digest.setCellSelection(null);
			digest.setEnumDelimiter(null);
			digest.setEnumFinalDelimiter(null);
			digest.setEnumPrefix(null);
		}
		return true;
	}
}
package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.common.validate.TextWarningConsumer;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.domain.DomainTranslation;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

class DynamicStringCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	private final class FindAttrL extends AbstractThreadedActionAdapter {

		private String[] items = null;

		private String[] getItems() {
			if (items == null) {
				items = attributeItemList.toArray(new String[0]);
				Arrays.sort(items);
			}
			return items;
		}

		public void performAction(ActionEvent e) {
			findAttributeButton.setEnabled(false);
			try {
				String value = (String) JOptionPane.showInputDialog(
						ClientUtil.getApplet(),
						null,
						ClientUtil.getInstance().getLabel("d.title.select.item"),
						JOptionPane.PLAIN_MESSAGE,
						null,
						getItems(),
						null);

				if (value != null && value.length() > 0) {
					pasteValue("$" + value + "$");
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				findAttributeButton.setEnabled(true);
			}
		}
	}

	private final class FindColL extends AbstractThreadedActionAdapter {

		private String[] columnNames = null;

		private String[] getColumnNames() {
			if (columnNames == null) {
				columnNames = new String[templateColumnNames.length];
				for (int i = 1; i <= columnNames.length; i++) {
					columnNames[i - 1] = i + " " + templateColumnNames[i - 1];
				}
			}
			return columnNames;
		}

		public void performAction(ActionEvent e) {
			findColumnButton.setEnabled(false);
			try {
				String value = (String) JOptionPane.showInputDialog(ClientUtil.getApplet(), null, "Select Column", JOptionPane.PLAIN_MESSAGE, null, getColumnNames(), null);
				if (value != null) {
					int colIndex = Integer.parseInt(value.substring(0, value.indexOf(" ")));
					pasteValue("%column " + colIndex + "%");
				}
			}
			finally {
				findColumnButton.setEnabled(true);
			}
		}
	}

	private final class ValidateL extends AbstractThreadedActionAdapter {

		private final TextWarningConsumer consumer = new TextWarningConsumer();
		private final TextWarningConsumer infoConsumer = new TextWarningConsumer();

		public void performAction(ActionEvent e) {
			if (textArea.getText() != null || textArea.getText().length() > 0) {
				validateButton.setEnabled(false);
				try {
					DomainModel.initInstance();

					Validator.validateDynamicString(attributeItemList, textArea.getText(), templateColumnNames.length, consumer, infoConsumer);
					if (consumer.hasWarnings()) {//errors coming from column references
						ClientUtil.getInstance().showWarning("msg.warning.validation.message", new Object[] { consumer.toString() });
					}
					else if (infoConsumer.hasWarnings()) {//info about correct DisplayName in dynamic strings
						ClientUtil.getInstance().showInformation("msg.info.validation.message", new Object[] { infoConsumer.toString() });
					}
					else {//all good!! all column references were correct and there were no DisplayNames to be checked
						ClientUtil.getInstance().showInformation("msg.info.valid.text");
					}
				}
				catch (Exception ex) {
					ClientUtil.getInstance().showWarning("msg.error.failure.validate", new Object[] { "message", ex.getMessage() });
				}
				finally {
					consumer.clear();
					validateButton.setEnabled(true);
				}
			}
		}
	}

	private JLabel label = new JLabel();
	private JButton button = new JButton("");
	private final boolean viewOnly;
	private final List<String> attributeItemList;
	private final String title;
	private final JPanel panel;
	private final JTextArea textArea;
	private final JButton validateButton;
	private final JButton findAttributeButton;
	private final JButton findColumnButton;
	private String[] templateColumnNames;

	DynamicStringCellEditor(String[] templateColumnNames, String columnName, ColumnDataSpecDigest columnDataSpecDigest, boolean viewOnly) {
		this.templateColumnNames = templateColumnNames;
		if (this.templateColumnNames == null) {
			this.templateColumnNames = new String[0];
		}
		this.viewOnly = viewOnly;
		this.title = columnName.toUpperCase() + " Editor";
		this.textArea = new JTextArea();
		textArea.setColumns(80);
		textArea.setRows(3);

		this.panel = new JPanel();
		validateButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.validate"), "image.btn.validate.message", new ValidateL(), null);
		attributeItemList = new ArrayList<String>();
		if (columnDataSpecDigest.getAllAttributeItems() == null || columnDataSpecDigest.getAllAttributeItems().isEmpty()) {
			findAttributeButton = null;
		}
		else {
			findAttributeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.insert.attribute"), "image.btn.find.attribute", new FindAttrL(), null);
			initAttributeItemList(columnDataSpecDigest);
		}
		findColumnButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.insert.column"), "image.btn.find.column", new FindColL(), null);

		init();
		setClickCountToStart(2);
	}

	private JPanel createEditorPanel() {
		JPanel selectionPanel = UIFactory.createBorderLayoutPanel(4, 4);

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(validateButton);
		if (findAttributeButton != null) {
			buttonPanel.add(findAttributeButton);
		}
		if (findColumnButton != null) {
			buttonPanel.add(findColumnButton);
		}

		selectionPanel.add(buttonPanel, BorderLayout.NORTH);
		selectionPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		return selectionPanel;
	}

	private void editValueSelection() {
		JPanel selectionPanel = createEditorPanel();

		populateValue(button.getText());

		int option = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), selectionPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION) {
			button.setText(extractValue());
			fireEditingStopped();
		}
	}

	private String extractValue() {
		StringBuilder buff = new StringBuilder();
		buff.append(textArea.getText());
		return buff.toString();
	}

	protected void fireEditingCanceled() {
		super.fireEditingCanceled();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}

	public Object getCellEditorValue() {
		return DynamicStringValue.parseValue(button.getText());
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (ExcelAdapter.isNullEmptyString(value)) {
			button.setText("");
		}
		else {
			button.setText(value.toString());
		}
		button.setEnabled(!this.viewOnly);
		return button;
	}

	//// for cell-editor ////////////////////////////////

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (ExcelAdapter.isNullEmptyString(value)) {
			label.setText("");
		}
		else {
			label.setText(value.toString());
		}
		return label;
	}

	private void init() {
		button.addMouseListener(new EditingStoppingSingleMouseClickListener(this));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		panel.setLayout(new BorderLayout(1, 1));
		panel.add(button, BorderLayout.CENTER);
	}

	private void initAttributeItemList(ColumnDataSpecDigest columnDataSpecDigest) {
		for (Iterator<ColumnAttributeItemDigest> iter = columnDataSpecDigest.getAllAttributeItems().iterator(); iter.hasNext();) {
			ColumnAttributeItemDigest element = iter.next();
			if (element.getDisplayValue() != null) {
				attributeItemList.add(element.getDisplayValue());
			}
			else {
				// check DomainTranslation first
				int index = element.getName().indexOf(".");
				if (index > -1) {
					DomainClass dc = DomainModel.getInstance().getDomainClass(element.getName().substring(0, index));
					if (dc != null) {
						DomainTranslation dt = dc.getDomainTranslation(element.getName().substring(index + 1));
						if (dt != null) {
							attributeItemList.add(dt.getContextlessLabel());
						}
						DomainAttribute da = dc.getDomainAttribute(element.getName().substring(index + 1));
						if (da != null) {
							attributeItemList.add(da.getContextlessLabel());
						}
					}
				}
			}
		}
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	private void pasteValue(String val) {
		if (val != null && val.length() > 0) {
			String prevText = textArea.getText();
			int pos = textArea.getCaretPosition();
			textArea.setText(prevText.substring(0, pos) + val + prevText.substring(pos));
			textArea.setCaretPosition(pos + val.length());
			textArea.requestFocus();
		}
	}

	private void populateValue(String valueStr) {
		textArea.setText(valueStr);
	}

	public void setCellEditorValue(Object value) {
		setValue_internal(value);
	}

	protected void setValue(Object value) {
		setValue_internal(value);
	}

	private void setValue_internal(Object value) {
		if (value instanceof DynamicStringValue) {
			this.button.setText(value.toString());
		}
		else {
			this.button.setText(value.toString());
		}
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				editValueSelection();
				flag = true;
			}
			else {
				return true;
			}
		}
		return flag;
	}
}
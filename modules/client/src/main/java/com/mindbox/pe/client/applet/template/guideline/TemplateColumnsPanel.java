package com.mindbox.pe.client.applet.template.guideline;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.template.common.ColumnMessageFragmentEditDialog;
import com.mindbox.pe.client.applet.template.common.ColumnTableModel;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.ExternalEnumSourceDetailCellRenderer;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.GenericEntityTypeComboBox;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TemplateColumnCellRenderer;
import com.mindbox.pe.client.common.event.Action3Adapter;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.common.ui.NumberTextField;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.ExternalEnumSourceDetail;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.template.ColumnAttributeItemDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.server.parser.jtb.message.ParseException;

/**
 * Panel for managing template column details.
 * @author kim
 * @author MindBox
 * @since PowerEditor
 */
class TemplateColumnsPanel extends PanelBase {

	private class AttrItemActionL extends Action3Adapter {
		@Override
		public void deletePerformed(ActionEvent e) {
			ColumnAttributeItemDigest attrItem = getSelectedAttributeItem();
			if (attrItem == null) {
				return;
			}
			if (ClientUtil.getInstance().showConfirmation("msg.question.delete.column.attr.item")) {
				((JButton) e.getSource()).setEnabled(false);
				try {
					attributeItemTableModel.removeRow(attrItem);
				}
				finally {
					((JButton) e.getSource()).setEnabled(true);
				}
			}
		}

		@Override
		public void editPerformed(ActionEvent e) {
			ColumnAttributeItemDigest attrItem = getSelectedAttributeItem();
			if (attrItem == null) {
				return;
			}
			attrItem = ColumnAttributeItemDialog.editColumnAttributeItem(attrItem);
			if (attrItem != null) {
				attributeItemTableModel.updateRow(attributeItemTable.getSelectedRow());
			}
		}

		@Override
		public void newPerformed(ActionEvent e) {
			ColumnAttributeItemDigest attrItem = ColumnAttributeItemDialog.newColumnAttributeItem();
			if (attrItem != null) {
				attributeItemTableModel.addRow(attrItem);
			}
		}
	}

	private class ColumnActionL extends Action3Adapter {
		@Override
		public void deletePerformed(ActionEvent e) {
			if (columnTable.getSelectedRow() != -1) {
				GridTemplateColumn column = columnTableModel.getColumnAt(columnTable.getSelectedRow());
				int option = JOptionPane.showConfirmDialog(
						ClientUtil.getApplet(),
						"Are you sure you want to delete Column " + column.getID() + " '" + column.getTitle() + "'?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.YES_OPTION) {
					columnTable.getSelectionModel().removeListSelectionListener(columnSelectionL);
					deleteColumn();
					clearColumnFields();
					currentColumn = null;
					columnTable.getSelectionModel().addListSelectionListener(columnSelectionL);
				}
			}
		}

		@Override
		public void newPerformed(ActionEvent e) {
			try {
				updateCurrentColumn();

				createColumn();
				setEditableColumnFields(true);
			}
			catch (ValidationException e1) {
				e1.showAsWarning();
			}
		}
	}

	private class ColumnMessageActionL extends Action3Adapter {
		@Override
		public void deletePerformed(ActionEvent e) {
			ColumnMessageFragmentDigest digest = getSelectedColumnMessageFragment();

			if (digest != null) {
				if (ClientUtil.getInstance().showConfirmation("msg.question.delete.message.column")) {
					columnMessageTableModel.removeRow(digest);
					currentColumn.removeMessageFragmentDigest(digest);
				}
			}
		}

		@Override
		public void editPerformed(ActionEvent e) {
			ColumnMessageFragmentDigest digest = getSelectedColumnMessageFragment();

			if (digest != null) {
				String prevText = digest.getText();
				digest = ColumnMessageFragmentEditDialog.editColumnMessageFragmentDigest(
						JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
						template,
						currentColumn.getColumnNumber(),
						digest);

				if (digest != null) {
					try {
						currentColumn.updateColumnMessageFragmentText(digest);
						columnMessageTableModel.updateRow(columnMessageTable.getSelectedRow());
					}
					catch (ParseException e1) {
						e1.printStackTrace();
						ClientUtil.getInstance().showWarning("msg.warning.failure.update.column.message", new Object[] { e1.getMessage() });
						digest.setText(prevText);
					}
				}
			}
		}

		@Override
		public void newPerformed(ActionEvent e) {
			ColumnMessageFragmentDigest digest = ColumnMessageFragmentEditDialog.createColumnMessageFragmentDigest(
					JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
					template,
					currentColumn.getColumnNumber());

			if (digest != null) {
				try {
					currentColumn.addColumnMessageFragment(digest);
					columnMessageTableModel.addRow(digest);
				}
				catch (ParseException e1) {
					e1.printStackTrace();
					ClientUtil.getInstance().showWarning("msg.warning.failure.add.column.message", new Object[] { e1.getMessage() });
				}
			}
		}
	}

	private class ColumnSelectionL implements ListSelectionListener {
		private void cancelEvent() {
			columnTable.getSelectionModel().removeListSelectionListener(this);
			try {
				columnTable.getSelectionModel().setSelectionInterval(currentColumn.getColumnNumber() - 1, currentColumn.getColumnNumber() - 1);
			}
			finally {
				columnTable.getSelectionModel().addListSelectionListener(this);
			}
		}

		@Override
		public synchronized void valueChanged(ListSelectionEvent arg0) {
			if (arg0.getValueIsAdjusting()) {
				removeDocumentListener(detailPanel.getFieldChangeListener());
				try {
					updateCurrentColumn();

					if (columnTable.getSelectedRow() != -1) {
						currentColumn = columnTableModel.getColumnAt(columnTable.getSelectedRow());
						populateColumnFields(currentColumn);

						if (isEnabled()) {
							setEditableColumnFields(true);
							titleField.requestFocus();
							setEnabledColumnSelectionAwareButtons(true);
						}
					}
					else {
						currentColumn = null;
						clearColumnFields();
						setEditableColumnFields(false);
						setEnabledColumnSelectionAwareButtons(false);
					}
				}
				catch (ValidationException e1) {
					e1.showAsWarning();
					cancelEvent();
				}
				finally {
					addDocumentListener(detailPanel.getFieldChangeListener());
				}
			}
		}
	}

	private final class DataTypeComboL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String selection = (String) dataTypeCombo.getSelectedItem();

			if (selection != null) {
				if (selection.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
					columnDTDetailCard.show(columnDTDetailPanel, "ENUM");
				}
				else if (selection.equals(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING)) {
					columnDTDetailCard.show(columnDTDetailPanel, "DYNAMICSTRING");
				}
				else if (selection.equals(ColumnDataSpecDigest.TYPE_INTEGER) || selection.equals(ColumnDataSpecDigest.TYPE_INTEGER_RANGE)) {
					columnDTDetailCard.show(columnDTDetailPanel, "RANGE");
				}
				else if (selection.equals(ColumnDataSpecDigest.TYPE_CURRENCY) || selection.equals(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE)
						|| selection.equals(ColumnDataSpecDigest.TYPE_FLOAT) || selection.equals(ColumnDataSpecDigest.TYPE_FLOAT_RANGE)) {
					columnDTDetailCard.show(columnDTDetailPanel, "FLOAT");
				}
				else if (selection.equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
					columnDTDetailCard.show(columnDTDetailPanel, "ENTITY");
				}
				else {
					columnDTDetailCard.show(columnDTDetailPanel, "EMPTY");
				}
				allowNullCheckBox.setVisible(!selection.equals(ColumnDataSpecDigest.TYPE_RULE_ID));
				multiSelectCheckBox.setVisible(selection.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) || selection.equals(ColumnDataSpecDigest.TYPE_ENTITY));
				sortEnumCheckBox.setVisible(selection.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) || selection.equals(ColumnDataSpecDigest.TYPE_ENTITY));
				showLHSAttrCheckBox.setVisible(selection.equals(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING));
				if (sortEnumCheckBox.isVisible()) {
					sortEnumCheckBox.setText(
							ClientUtil.getInstance().getLabel(selection.equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) ? "checkbox.sort.enum" : "checkbox.sort.entity"));
				}
			}
			else {
				columnDTDetailCard.show(columnDTDetailPanel, "EMPTY");
			}
		}
	}

	private class DownColumnL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent e) {
			if (columnTable.getSelectedRow() != -1) {
				try {
					updateCurrentColumn();

					GridTemplateColumn column = columnTableModel.getColumnAt(columnTable.getSelectedRow());
					columnTable.clearSelection();
					moveColumnDown(column.getID());
				}
				catch (ValidationException e1) {
					e1.showAsWarning();
				}
			}
		}
	}

	private class DownEnumL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			final int index = enumValueList.getSelectedIndex();
			if ((index >= 0) && (index < (enumValueListModel.getSize() - 1))) {
				String selection = enumValueListModel.remove(index);
				enumValueListModel.insertElementAt(selection, index + 1);
				enumValueList.setSelectedIndex(index + 1);
			}
		}
	}

	private class EntityTypeActionL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			GenericEntityType entityType = entityTypeCombo.getSelectedEntityType();
			resetEntityContentCombo(entityType != null && entityType.hasCategory(), null);
		}
	}

	private class EnumSourceDetailComboL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (enumSourceDetailCombo.getSelectedIndex() >= 0) {
				selectorColumnCombo.setEnabled(((ExternalEnumSourceDetail) enumSourceDetailCombo.getSelectedItem()).isSupportsSelector());
			}
		}
	}

	private class EnumValueActionL extends Action3Adapter {
		@Override
		public void deletePerformed(ActionEvent e) {
			String prevValue = enumValueList.getSelectedValue();

			if (prevValue != null) {
				int option = JOptionPane.showConfirmDialog(
						ClientUtil.getApplet(),
						"Are you sure you want to delete the selected enum value '" + prevValue + "'?",
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.YES_OPTION) {
					enumValueListModel.removeElement(prevValue);
				}
			}
		}

		@Override
		public void editPerformed(ActionEvent e) {
			String prevValue = enumValueList.getSelectedValue();
			if (prevValue != null) {
				final Object value = JOptionPane.showInputDialog(
						ClientUtil.getApplet(),
						"Edit Enumeration Value for " + currentColumn.getTitle(),
						"Edit Enum Value",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						prevValue);

				if ((value != null) && !prevValue.equals(value)) {
					enumValueListModel.removeElement(prevValue);
					enumValueListModel.addElement(String.class.cast(value));
				}
			}
		}

		@Override
		public void newPerformed(ActionEvent e) {
			final String value = JOptionPane.showInputDialog(
					ClientUtil.getApplet(),
					"New Enumeration Value for " + currentColumn.getTitle(),
					"New Enum Value",
					JOptionPane.PLAIN_MESSAGE);

			if (value != null) {
				enumValueListModel.addElement(value);
			}
		}
	}

	private class EnumValueSelectionL implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			int index = enumValueList.getSelectedIndex();
			setEnabledEnumValueSelectionAwareButtons(index > -1);

			if (index == 0) {
				upEnumButton.setEnabled(false);
			}

			if (index == (enumValueListModel.size() - 1)) {
				downEnumButton.setEnabled(false);
			}
		}
	}

	private class SortAscL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			sortEnumValues(true);
		}
	}

	private class SortDescL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			sortEnumValues(false);
		}
	}

	private class UpColumnL extends AbstractThreadedActionAdapter {
		@Override
		public void performAction(ActionEvent e) {
			if (columnTable.getSelectedRow() != -1) {
				try {
					updateCurrentColumn();

					GridTemplateColumn column = columnTableModel.getColumnAt(columnTable.getSelectedRow());
					columnTable.clearSelection();
					moveColumnUp(column.getID());
				}
				catch (ValidationException e1) {
					e1.showAsWarning();
				}
			}
		}
	}

	private class UpEnumL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = enumValueList.getSelectedIndex();

			if (index > 0) {
				String selection = enumValueListModel.remove(index);
				enumValueListModel.insertElementAt(selection, index - 1);
				enumValueList.setSelectedIndex(index - 1);
			}
		}
	}

	private static final long serialVersionUID = -3951228734910107454L;
	private static final int MAX_MIN_PRECISION = 15;
	/**
	 * Whenever you modify this array, be sure to update indices in {@link #updateDataTypeDetails(int)} method.
	 */
	private static final String[] DATA_TYPES = new String[] {
			ColumnDataSpecDigest.TYPE_BOOLEAN,
			ColumnDataSpecDigest.TYPE_CURRENCY,
			ColumnDataSpecDigest.TYPE_CURRENCY_RANGE,
			ColumnDataSpecDigest.TYPE_DATE,
			ColumnDataSpecDigest.TYPE_DATE_RANGE,
			ColumnDataSpecDigest.TYPE_DYNAMIC_STRING,
			ColumnDataSpecDigest.TYPE_ENTITY,
			ColumnDataSpecDigest.TYPE_ENUM_LIST,
			ColumnDataSpecDigest.TYPE_FLOAT,
			ColumnDataSpecDigest.TYPE_FLOAT_RANGE,
			ColumnDataSpecDigest.TYPE_INTEGER,
			ColumnDataSpecDigest.TYPE_INTEGER_RANGE,
			ColumnDataSpecDigest.TYPE_RULE_ID,
			ColumnDataSpecDigest.TYPE_STRING,
			ColumnDataSpecDigest.TYPE_SYMBOL,
			ColumnDataSpecDigest.TYPE_TIME_RANGE };
	private final ButtonPanel columnButtonPanel;
	private final JButton upColumnButton;
	private final JButton downColumnButton;
	private final ColumnTableModel columnTableModel;
	private final JTable columnTable;
	private final JComboBox<Integer> idField;
	private final JTextField titleField = new JTextField();
	private final JCheckBox allowNullCheckBox = UIFactory.createCheckBox("checkbox.allow.null");
	private final JCheckBox multiSelectCheckBox = UIFactory.createCheckBox("checkbox.select.multiple");
	private final JCheckBox sortEnumCheckBox = UIFactory.createCheckBox("checkbox.sort.enum");
	private final JCheckBox showLHSAttrCheckBox = UIFactory.createCheckBox("checkbox.show.lhs.attribute");
	private final JTextField fontField = new JTextField();
	private final JTextField colorField = new JTextField();
	private final NumberTextField widthField = new NumberTextField(10);
	private final JPanel columnDTDetailPanel;
	private final CardLayout columnDTDetailCard = new CardLayout();
	private final NumberTextField minField = new NumberTextField(MAX_MIN_PRECISION);
	private final NumberTextField maxField = new NumberTextField(MAX_MIN_PRECISION);
	private final FloatTextField minFloatField = new FloatTextField(MAX_MIN_PRECISION, false);
	private final FloatTextField maxFloatField = new FloatTextField(MAX_MIN_PRECISION, false);
	private final NumberTextField precisionField = new NumberTextField(4, 0, -1, false);
	private final ButtonPanel enumValueButtonPanel;
	private final DefaultListModel<String> enumValueListModel;
	private final JList<String> enumValueList;
	private final JTextField colNameField = new JTextField();
	private final JTextField colDescField = new JTextField();
	private final AttributeReferenceSelectField attrMapField = new AttributeReferenceSelectField();
	private final JComboBox<String> dataTypeCombo;
	private final ColumnMessageFragmentTableModel columnMessageTableModel;
	private final JTable columnMessageTable;
	private final ButtonPanel messageFragmentButtonPanel;
	private final JButton upEnumButton;
	private final JButton downEnumButton;
	private final JButton sortAscButton;
	private final JButton sortDescButton;
	private final ButtonPanel attrItemButtonPanel;
	private final AttributeItemTableModel attributeItemTableModel;
	private final JTable attributeItemTable;
	protected GridTemplateColumn currentColumn = null;
	protected GridTemplate template = null;
	private final TemplateDetailPanel detailPanel;
	private ColumnSelectionL columnSelectionL;
	private final GenericEntityTypeComboBox entityTypeCombo;
	private final JComboBox<String> entityContentCombo;
	private final ActionListener entityTypeListener;
	private final CardLayout enumDetailCardLayout;
	private final JPanel enumDetailCenterPanel;
	// Enumeration source fields
	private final JComboBox<EnumSourceType> enumSourceTypeCombo;
	private final JComboBox<ExternalEnumSourceDetail> enumSourceDetailCombo;
	private final JComboBox<GridTemplateColumn> selectorColumnCombo;
	private final DefaultComboBoxModel<GridTemplateColumn> selectorColumnComboBoxModel;
	private final EnumSourceDetailComboL enumSourceDetailComboL;

	TemplateColumnsPanel(TemplateDetailPanel detailPanel) {
		this.detailPanel = detailPanel;

		enumSourceDetailComboL = new EnumSourceDetailComboL();
		entityTypeCombo = new GenericEntityTypeComboBox(false, true, false);
		entityContentCombo = UIFactory.createComboBox();
		resetEntityContentCombo(true);

		idField = new JComboBox<Integer>();
		// modify here to allow more than 20 columns, set it to 99
		for (int i = 1; i <= 99; i++) {
			idField.addItem(i);
		}

		columnMessageTableModel = new ColumnMessageFragmentTableModel();
		columnMessageTable = new JTable(columnMessageTableModel);
		columnMessageTable.setColumnSelectionAllowed(false);
		columnMessageTable.setRowSelectionAllowed(true);

		messageFragmentButtonPanel = UIFactory.create3ButtonPanel(new ColumnMessageActionL(), true);

		TableColumn column = columnMessageTable.getColumnModel().getColumn(6);
		column.setPreferredWidth(100);

		columnTableModel = new ColumnTableModel();
		columnTable = new JTable(columnTableModel);
		columnTable.setColumnSelectionAllowed(false);
		columnTable.setRowSelectionAllowed(true);

		column = columnTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(20);
		column = columnTable.getColumnModel().getColumn(1);
		column.setPreferredWidth(200);

		columnButtonPanel = UIFactory.create3ButtonPanel(new ColumnActionL(), false);
		downColumnButton = UIFactory.createButton("", "image.btn.small.down", new DownColumnL(), "button.tooltip.choice.move.down");
		upColumnButton = UIFactory.createButton("", "image.btn.small.up", new UpColumnL(), "button.tooltip.choice.move.up");

		dataTypeCombo = new JComboBox<String>(DATA_TYPES);

		columnDTDetailPanel = new JPanel(columnDTDetailCard);
		columnDTDetailPanel.setPreferredSize(new Dimension(200, 120));

		downEnumButton = UIFactory.createButton("", "image.btn.small.down", new DownEnumL(), "button.tooltip.choice.move.down");
		upEnumButton = UIFactory.createButton("", "image.btn.small.up", new UpEnumL(), "button.tooltip.choice.move.up");
		sortAscButton = UIFactory.createButton("", "image.btn.small.sort.asc", new SortAscL(), "button.tooltip.sort.asc");
		sortDescButton = UIFactory.createButton("", "image.btn.small.sort.desc", new SortDescL(), "button.tooltip.sort.desc");
		enumValueButtonPanel = UIFactory.create3ButtonPanel(new EnumValueActionL(), true);
		enumValueListModel = new DefaultListModel<String>();
		enumValueList = new JList<String>(enumValueListModel);
		enumValueList.addListSelectionListener(new EnumValueSelectionL());

		attrItemButtonPanel = UIFactory.create3ButtonPanel(new AttrItemActionL(), true);
		attributeItemTableModel = new AttributeItemTableModel();
		attributeItemTable = new JTable(attributeItemTableModel);
		attributeItemTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		colNameField.setEditable(false);

		enumDetailCardLayout = new CardLayout();
		enumDetailCenterPanel = UIFactory.createJPanel(enumDetailCardLayout);
		enumSourceTypeCombo = UIFactory.createComboBox();
		enumSourceDetailCombo = UIFactory.createComboBox();
		enumSourceDetailCombo.setRenderer(new ExternalEnumSourceDetailCellRenderer());
		selectorColumnComboBoxModel = new DefaultComboBoxModel<GridTemplateColumn>();
		selectorColumnCombo = UIFactory.createComboBox();
		selectorColumnCombo.setModel(selectorColumnComboBoxModel);
		selectorColumnCombo.setRenderer(new TemplateColumnCellRenderer());

		// populate enumSourceNameCombo
		List<ExternalEnumSourceDetail> enumSourceDetails;
		try {
			enumSourceDetails = ClientUtil.getCommunicator().getEnumerationSourceDetails();
			if (enumSourceDetails != null) {
				for (ExternalEnumSourceDetail detail : enumSourceDetails) {
					enumSourceDetailCombo.addItem(detail);
				}
			}
		}
		catch (ServerException e) {
			ClientUtil.handleRuntimeException(e);
		}

		for (ColumnDataSpecDigest.EnumSourceType sourceType : ColumnDataSpecDigest.EnumSourceType.values()) {
			// Don't add external if no source is available
			if (sourceType != EnumSourceType.EXTERNAL || enumSourceDetailCombo.getItemCount() > 0) {
				enumSourceTypeCombo.addItem(sourceType);
			}
		}

		initPanel();

		// hide colum name field
		this.colNameField.setVisible(false);
		this.columnSelectionL = new ColumnSelectionL();
		columnTable.getSelectionModel().addListSelectionListener(columnSelectionL);

		setEnabledEnumValueSelectionAwareButtons(false);
		setEnabledColumnMessageSelectionAwareButtons(false);
		setEnabledAttributeItemSelectionAwareButtons(false);
		setEditable(false);

		idField.setEditable(false);
		idField.setEnabled(false);

		columnMessageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				setEnabledColumnMessageSelectionAwareButtons(columnMessageTable.getSelectedRow() >= 0);
			}
		});

		attributeItemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				setEnabledAttributeItemSelectionAwareButtons(attributeItemTable.getSelectedRow() >= 0);
			}
		});
		entityTypeListener = new EntityTypeActionL();
	}

	private synchronized void addDocumentListener(TemplateDetailPanel.FieldChangeListener changeListener) {
		attrMapField.addChangeListener(changeListener);
		titleField.getDocument().addDocumentListener(changeListener);
		colDescField.getDocument().addDocumentListener(changeListener);
		fontField.getDocument().addDocumentListener(changeListener);
		widthField.getDocument().addDocumentListener(changeListener);
		colorField.getDocument().addDocumentListener(changeListener);
		allowNullCheckBox.addActionListener(changeListener);
		multiSelectCheckBox.addActionListener(changeListener);
		sortEnumCheckBox.addActionListener(changeListener);
		showLHSAttrCheckBox.addActionListener(changeListener);
		dataTypeCombo.addActionListener(changeListener);
		minField.getDocument().addDocumentListener(changeListener);
		maxField.getDocument().addDocumentListener(changeListener);
		minFloatField.getDocument().addDocumentListener(changeListener);
		maxFloatField.getDocument().addDocumentListener(changeListener);
		precisionField.getDocument().addDocumentListener(changeListener);
		columnTableModel.addTableModelListener(changeListener);
		columnMessageTableModel.addTableModelListener(changeListener);
		attributeItemTableModel.addTableModelListener(changeListener);
		enumValueListModel.addListDataListener(changeListener);
		entityTypeCombo.addActionListener(changeListener);
		entityContentCombo.addActionListener(changeListener);
		entityTypeCombo.addActionListener(entityTypeListener);
		enumSourceTypeCombo.addActionListener(changeListener);
		enumSourceDetailCombo.addActionListener(changeListener);
		selectorColumnCombo.addActionListener(changeListener);
	}

	private void clearColumnFields() {
		idField.setSelectedIndex(0);
		colNameField.setText("");
		titleField.setText("");
		allowNullCheckBox.setSelected(false);
		multiSelectCheckBox.setSelected(false);
		showLHSAttrCheckBox.setSelected(false);
		sortEnumCheckBox.setSelected(false);
		fontField.setText("");
		widthField.setValue(0);
		colorField.setText("");
		minField.setValue(0);
		maxField.setText("");
		maxFloatField.setText("");
		minFloatField.setValue(0);
		precisionField.setValue(FloatFormatter.DEFAULT_PRECISION);
		colDescField.setText("");
		attrMapField.clearValue();
		dataTypeCombo.setSelectedIndex(-1);
		entityTypeCombo.setSelectedIndex(-1);
		selectorColumnComboBoxModel.removeAllElements();
		enumSourceTypeCombo.setSelectedIndex(-1);
		enumSourceDetailCombo.setSelectedIndex(-1);
		enumValueListModel.removeAllElements();
		resetEntityContentCombo(true);
		attributeItemTableModel.removeAllRows();
		columnMessageTableModel.removeAllRows();
	}

	public final void clearFields() {
		this.template = null;
		this.currentColumn = null;
		columnTableModel.removeAllRows();
		clearColumnFields();
		setEditable(false);
		setEnabledEnumValueSelectionAwareButtons(false);
	}

	private void createColumn() {
		int columnID = getNextColumnID();
		GridTemplateColumn column = new GridTemplateColumn(columnID, getNextColumnName(columnID), "", 100, template.getUsageType());
		column.setDataSpecDigest(new ColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(DeployType.STRING.toString());
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(false);
		column.setColor("automatic");
		column.setFont("arial");
		column.setColumnWidth(100);
		column.setTitle(column.getTitle());
		template.addGridTemplateColumn(column);
		columnTableModel.addRow(column);

		int index = columnTableModel.indexOf(column);

		if (index > -1) {
			columnTable.setRowSelectionInterval(index, index);
		}

		this.currentColumn = column;
		populateColumnFields(currentColumn);

		detailPanel.columnAdded();
		detailPanel.fireDetailChanged();

		postCreateColumnAction();
	}

	private void deleteColumn() {
		if (template != null) {
			template.removeTemplateColumn(currentColumn.getColumnNumber());
			columnTableModel.removeRow(currentColumn);
			detailPanel.columnDeleted();
			postDeleteColumnAction();
		}
	}

	private int getIndexOfExternalEnumSource(String sourceName) {
		for (int i = 0; i < enumSourceDetailCombo.getItemCount(); i++) {
			ExternalEnumSourceDetail detail = enumSourceDetailCombo.getItemAt(i);
			if (detail != null && detail.getName().equals(sourceName)) {
				return i;
			}
		}
		return -1;
	}

	private int getNextColumnID() {
		logger.debug(">>> getNextColumnID");
		int nextID = 0;

		if (template.getNumColumns() > 0) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();

				if (element.getID() > nextID) {
					nextID = element.getID();
				}
			}
		}

		logger.debug("<<< getNextColumnID with " + (nextID + 1));

		return nextID + 1;
	}

	private String getNextColumnName(int baseNo) {
		String nameToCheck = "Column " + baseNo;

		if (template.getNumColumns() > 0) {
			for (Iterator<GridTemplateColumn> iter = template.getColumns().iterator(); iter.hasNext();) {
				GridTemplateColumn element = iter.next();

				if (element.getName().equals(nameToCheck)) {
					return getNextColumnName(baseNo + 1);
				}
			}
		}

		return nameToCheck;
	}

	private ColumnAttributeItemDigest getSelectedAttributeItem() {
		int index = attributeItemTable.getSelectedRow();

		return ((index >= 0) ? (ColumnAttributeItemDigest) attributeItemTableModel.getValueAt(index, -1) : null);
	}

	private ColumnMessageFragmentDigest getSelectedColumnMessageFragment() {
		int index = columnMessageTable.getSelectedRow();

		if (index >= 0) {
			return (ColumnMessageFragmentDigest) columnMessageTableModel.getValueAt(index, -1);
		}
		else {
			return null;
		}
	}

	private ExternalEnumSourceDetail getSelectedExternalEnumSourceDetail() {
		return (ExternalEnumSourceDetail) enumSourceDetailCombo.getSelectedItem();
	}

	private void initPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.weighty = 0.0;
		c.gridheight = 1;

		JPanel columnBP = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		columnBP.add(columnButtonPanel);
		columnBP.add(new JSeparator());
		columnBP.add(upColumnButton);
		columnBP.add(downColumnButton);

		JPanel columnListPanel = new JPanel(new BorderLayout(2, 2));
		columnListPanel.setBorder(BorderFactory.createTitledBorder("Columns"));
		columnListPanel.add(columnBP, BorderLayout.NORTH);
		columnListPanel.add(new JScrollPane(columnTable), BorderLayout.CENTER);

		GridBagLayout bag = null;
		bag = new GridBagLayout();

		JPanel columnPresPanel = new JPanel(bag);
		columnPresPanel.setBorder(BorderFactory.createTitledBorder("Presentation Details"));

		c.gridheight = 1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnPresPanel, bag, c, new JLabel("Font:"));

		c.weightx = 0.4;
		addComponent(columnPresPanel, bag, c, fontField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnPresPanel, bag, c, new JLabel(" Width:"));

		c.weightx = 0.3;
		addComponent(columnPresPanel, bag, c, widthField);

		c.weightx = 0.0;
		addComponent(columnPresPanel, bag, c, new JLabel(" Color:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.3;
		addComponent(columnPresPanel, bag, c, colorField);

		bag = new GridBagLayout();

		JPanel columnDataTypePanel = new JPanel(bag);
		columnDataTypePanel.setBorder(BorderFactory.createTitledBorder("Data Type Details"));

		JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		checkBoxPanel.add(allowNullCheckBox);
		checkBoxPanel.add(multiSelectCheckBox);
		checkBoxPanel.add(showLHSAttrCheckBox);
		checkBoxPanel.add(sortEnumCheckBox);
		multiSelectCheckBox.setVisible(false);
		addComponent(columnDataTypePanel, bag, c, checkBoxPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnDataTypePanel, bag, c, new JLabel("Type:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(columnDataTypePanel, bag, c, dataTypeCombo);

		JPanel columnDetailPanelWrapper = new JPanel(new BorderLayout(0, 0));
		columnDetailPanelWrapper.add(columnDTDetailPanel, BorderLayout.CENTER);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(columnDataTypePanel, bag, c, columnDetailPanelWrapper);

		bag = new GridBagLayout();

		JPanel rangeDetailPanel = new JPanel(bag);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(rangeDetailPanel, bag, c, new JLabel("Min:"));

		c.weightx = 0.5;
		addComponent(rangeDetailPanel, bag, c, minField);

		c.weightx = 0.0;
		addComponent(rangeDetailPanel, bag, c, new JLabel(" Max:"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.5;
		addComponent(rangeDetailPanel, bag, c, maxField);

		bag = new GridBagLayout();

		JPanel floatRangeDetailPanel = new JPanel(bag);

		c.gridheight = 1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(floatRangeDetailPanel, bag, c, UIFactory.createFormLabel("label.min"));

		c.weightx = 0.5;
		addComponent(floatRangeDetailPanel, bag, c, minFloatField);

		c.weightx = 0.0;
		addComponent(floatRangeDetailPanel, bag, c, UIFactory.createFormLabel("label.max"));

		c.weightx = 0.5;
		addComponent(floatRangeDetailPanel, bag, c, maxFloatField);

		c.weightx = 0.0;
		addComponent(floatRangeDetailPanel, bag, c, UIFactory.createFormLabel("label.precision.decimal"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.0;
		addComponent(floatRangeDetailPanel, bag, c, precisionField);

		// EnumList panel ---------------------------------------------------
		JPanel enumDetailButtonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		enumDetailButtonPanel.add(enumValueButtonPanel);
		enumDetailButtonPanel.add(upEnumButton);
		enumDetailButtonPanel.add(downEnumButton);
		enumDetailButtonPanel.add(sortAscButton);
		enumDetailButtonPanel.add(sortDescButton);

		JPanel enumColumnTypeDetailPanel = UIFactory.createJPanel(new BorderLayout(2, 2));
		enumColumnTypeDetailPanel.setBorder(BorderFactory.createTitledBorder("Enumeration Values"));
		enumColumnTypeDetailPanel.add(enumDetailButtonPanel, BorderLayout.NORTH);
		enumColumnTypeDetailPanel.add(new JScrollPane(enumValueList), BorderLayout.CENTER);

		JPanel enumAttributeTypeDetailPanel = UIFactory.createBorderLayoutPanel(3, 3);
		enumAttributeTypeDetailPanel.setBorder(BorderFactory.createTitledBorder("Domain Attribute"));
		enumAttributeTypeDetailPanel.add(attrMapField, BorderLayout.NORTH);

		enumDetailCenterPanel.add(enumColumnTypeDetailPanel, "COLUMN");
		enumDetailCenterPanel.add(enumAttributeTypeDetailPanel, "ATTRIBUTE");
		enumDetailCenterPanel.add(new JPanel(), "EMPTY");

		// only add external detail panel if necessary
		if (enumSourceDetailCombo.getItemCount() > 0) {
			GridBagLayout externalBag = new GridBagLayout();
			JPanel enumExternalTypeDetailPanel = UIFactory.createJPanel(externalBag);
			enumExternalTypeDetailPanel.setBorder(BorderFactory.createTitledBorder("External Enumeration Source"));
			c.gridheight = 1;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(enumExternalTypeDetailPanel, externalBag, c, UIFactory.createFormLabel("label.enum.source.name"));
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(enumExternalTypeDetailPanel, externalBag, c, enumSourceDetailCombo);
			c.gridwidth = 1;
			c.weightx = 0.0;
			addComponent(enumExternalTypeDetailPanel, externalBag, c, UIFactory.createFormLabel("label.enum.selector.col"));
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			addComponent(enumExternalTypeDetailPanel, externalBag, c, selectorColumnCombo);
			c.weighty = 1.0;
			addComponent(enumExternalTypeDetailPanel, externalBag, c, Box.createVerticalGlue());

			enumDetailCenterPanel.add(enumExternalTypeDetailPanel, "EXTERNAL");

			// setup listener for enumSourceDetailCombo
			enumSourceDetailCombo.addActionListener(enumSourceDetailComboL);
		}

		JPanel enumDetailNorthPanel = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		enumDetailNorthPanel.add(UIFactory.createFormLabel("label.enum.source.type"));
		enumDetailNorthPanel.add(enumSourceTypeCombo);
		enumSourceTypeCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColumnDataSpecDigest.EnumSourceType sourceType = (ColumnDataSpecDigest.EnumSourceType) enumSourceTypeCombo.getSelectedItem();
				String panelName;
				if (sourceType == null) {
					panelName = "EMPTY";
				}
				else if (sourceType == ColumnDataSpecDigest.EnumSourceType.DOMAIN_ATTRIBUTE) {
					panelName = "ATTRIBUTE";
				}
				else if (sourceType == ColumnDataSpecDigest.EnumSourceType.COLUMN) {
					panelName = "COLUMN";
				}
				else if (sourceType == ColumnDataSpecDigest.EnumSourceType.EXTERNAL) {
					panelName = "EXTERNAL";
				}
				else {
					panelName = "EMPTY";
				}
				enumDetailCardLayout.show(enumDetailCenterPanel, panelName);
			}
		});

		JPanel enumDetailPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		enumDetailPanel.add(enumDetailNorthPanel, BorderLayout.NORTH);
		enumDetailPanel.add(enumDetailCenterPanel, BorderLayout.CENTER);

		// Dynamic string panel ---------------------------------------------
		JPanel dynamicStringDetailPanel = new JPanel(new BorderLayout(2, 2));
		dynamicStringDetailPanel.add(attrItemButtonPanel, BorderLayout.NORTH);
		dynamicStringDetailPanel.add(new JScrollPane(attributeItemTable), BorderLayout.CENTER);
		dynamicStringDetailPanel.add(UIFactory.createLabel("label.text.col.attr.items.sorted"), BorderLayout.SOUTH);
		dynamicStringDetailPanel.setBorder(UIFactory.createTitledBorder(ClientUtil.getInstance().getLabel("label.column.attr.items")));

		JPanel entityColumnWestPanel = UIFactory.createJPanel(new GridLayout(2, 1, 2, 2));
		entityColumnWestPanel.add(UIFactory.createFormLabel("label.entity"));
		entityColumnWestPanel.add(UIFactory.createFormLabel("label.column.content"));

		JPanel entityColumnEastPanel = UIFactory.createJPanel(new GridLayout(2, 1, 2, 2));
		entityColumnEastPanel.add(entityTypeCombo);
		entityColumnEastPanel.add(entityContentCombo);

		JPanel entityColumnNPanel = UIFactory.createBorderLayoutPanel(0, 0);
		entityColumnNPanel.add(entityColumnWestPanel, BorderLayout.WEST);
		entityColumnNPanel.add(entityColumnEastPanel, BorderLayout.CENTER);

		JPanel entityColumnPanel = UIFactory.createBorderLayoutPanel(0, 0);
		entityColumnPanel.add(entityColumnNPanel, BorderLayout.NORTH);

		JPanel emptyDetailPanel = new JPanel();

		columnDTDetailPanel.add(emptyDetailPanel, "EMPTY");
		columnDTDetailPanel.add(rangeDetailPanel, "RANGE");
		columnDTDetailPanel.add(enumDetailPanel, "ENUM");
		columnDTDetailPanel.add(floatRangeDetailPanel, "FLOAT");
		columnDTDetailPanel.add(dynamicStringDetailPanel, "DYNAMICSTRING");
		columnDTDetailPanel.add(entityColumnPanel, "ENTITY");

		dataTypeCombo.addActionListener(new DataTypeComboL());

		bag = new GridBagLayout();

		JPanel columnDetailPanel = new JPanel(bag);
		columnDetailPanel.setBorder(UIFactory.createTitledBorder("Column Details"));

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnDetailPanel, bag, c, UIFactory.createFormLabel("label.title"));

		c.gridwidth = 1;
		c.weightx = 1.0;
		addComponent(columnDetailPanel, bag, c, titleField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnDetailPanel, bag, c, UIFactory.createFormLabel("label.id"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.1;
		addComponent(columnDetailPanel, bag, c, idField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(columnDetailPanel, bag, c, UIFactory.createFormLabel("label.desc"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(columnDetailPanel, bag, c, colDescField);

		addComponent(columnDetailPanel, bag, c, columnPresPanel);

		c.gridheight = 1;
		c.weighty = 1.0;
		addComponent(columnDetailPanel, bag, c, columnDataTypePanel);

		JPanel columnMessagePanel = UIFactory.createBorderLayoutPanel(2, 2);
		columnMessagePanel.add(messageFragmentButtonPanel, BorderLayout.NORTH);
		columnMessagePanel.add(new JScrollPane(columnMessageTable), BorderLayout.CENTER);
		columnMessagePanel.setBorder(BorderFactory.createTitledBorder("Column Messages"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = 1;
		c.weighty = 1.0;
		addComponent(columnDetailPanel, bag, c, columnMessagePanel);

		JSplitPane columnPane = UIFactory.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, columnListPanel, columnDetailPanel);
		columnPane.setDividerLocation(200);

		setLayout(new BorderLayout(0, 0));
		add(columnPane, BorderLayout.CENTER);
	}

	private void moveColumnDown(int columnNo) {
		try {
			updateCurrentColumn();
		}
		catch (ValidationException e) {
			e.showAsWarning();
			return;
		}
		template.swapTemplateColumns(columnNo, columnNo + 1);
		currentColumn = template.getColumn(columnNo + 1);
		populateColumnFields(currentColumn);
		columnTableModel.moveRow(columnNo, false);
		detailPanel.columnsSwapped();
	}

	private void moveColumnUp(int columnNo) {
		try {
			updateCurrentColumn();
		}
		catch (ValidationException e) {
			e.showAsWarning();
			return;
		}
		template.swapTemplateColumns(columnNo, columnNo - 1);
		currentColumn = template.getColumn(columnNo - 1);
		populateColumnFields(currentColumn);
		columnTableModel.moveRow(columnNo, true);
		detailPanel.columnsSwapped();
	}

	private void populateColumnFields(GridTemplateColumn column) {
		idField.setSelectedIndex(column.getID() - 1);
		colNameField.setText(column.getName());
		titleField.setText(column.getTitle());
		allowNullCheckBox.setSelected(column.getColumnDataSpecDigest().isBlankAllowed());
		multiSelectCheckBox.setSelected(column.getColumnDataSpecDigest().isMultiSelectAllowed());
		sortEnumCheckBox.setSelected(column.getColumnDataSpecDigest().isEnumValueNeedSorted());
		showLHSAttrCheckBox.setSelected(column.getColumnDataSpecDigest().isLHSAttributeVisible());
		fontField.setText(column.getFont());
		widthField.setValue(column.getColumnWidth());
		colorField.setText(column.getColor());

		if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY)) {
			GenericEntityType entityType = GenericEntityType.forName(column.getColumnDataSpecDigest().getEntityType());
			setEntityTypeSelection(entityType);

			String contentStr = null;
			if (column.getColumnDataSpecDigest().isEntityAllowed() && column.getColumnDataSpecDigest().isCategoryAllowed()) {
				contentStr = ClientUtil.getInstance().getLabel("label.category.and.entity");
			}
			else if (column.getColumnDataSpecDigest().isCategoryAllowed()) {
				contentStr = ClientUtil.getInstance().getLabel("label.category");
			}
			else {
				contentStr = ClientUtil.getInstance().getLabel("label.entity");
			}
			resetEntityContentCombo(entityType != null && entityType.hasCategory(), contentStr);
		}

		if (column.getColumnDataSpecDigest().hasMinValue()) {
			minField.setValue(column.getColumnDataSpecDigest().getMinAsLong());
			minFloatField.setValue(column.getColumnDataSpecDigest().getMinAsFloat());
		}
		else {
			minField.setValue(0L);
			minFloatField.setValue(0.0);
		}

		if (column.getColumnDataSpecDigest().hasMaxValue()) {
			maxField.setValue((column.getColumnDataSpecDigest().getMaxAsLong()));
			maxFloatField.setValue(column.getColumnDataSpecDigest().getMaxAsFloat());
		}
		else {
			maxField.setText("");
			maxFloatField.setText("");
		}

		// TT 1879: clear field if no precision is set
		if (column.getColumnDataSpecDigest().isPrecisionSet()) {
			precisionField.setValue(column.getColumnDataSpecDigest().getPrecision());
		}
		else {
			precisionField.clearValue();
		}
		colDescField.setText(column.getDescription());
		attrMapField.setValue(column.getMAClassName(), column.getMAAttributeName());
		dataTypeCombo.setSelectedItem(column.getColumnDataSpecDigest().getType());
		// populate selector column combo
		refreshSelectorColumnComboItems(column);

		if (column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
			enumSourceTypeCombo.setSelectedItem(column.getColumnDataSpecDigest().getEnumSourceType());
			enumValueListModel.clear();
			attrMapField.clearValue();
			if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.COLUMN) {
				for (String valueStr : column.getColumnDataSpecDigest().getAllColumnEnumValues()) {
					enumValueListModel.addElement(valueStr);
				}
				enumDetailCardLayout.show(enumDetailCenterPanel, "COLUMN");
			}
			else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE) {
				attrMapField.setValue(column.getMAClassName(), column.getMAAttributeName());
				enumDetailCardLayout.show(enumDetailCenterPanel, "ATTRIBUTE");
			}
			else if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.EXTERNAL) {
				enumSourceDetailCombo.removeActionListener(enumSourceDetailComboL);
				try {
					selectExternalEnumSource(column.getColumnDataSpecDigest().getEnumSourceName());
					if (enumSourceDetailCombo.getItemCount() > 0) {
						ExternalEnumSourceDetail externalEnumSourceDetail = getSelectedExternalEnumSourceDetail();
						if (externalEnumSourceDetail.isSupportsSelector()) {
							if (!UtilBase.isEmptyAfterTrim(column.getColumnDataSpecDigest().getEnumSelectorColumnName())) {
								GridTemplateColumn columnToSelect = template.getColumn(column.getColumnDataSpecDigest().getEnumSelectorColumnName().trim());
								if (columnToSelect != null) {
									selectorColumnCombo.setSelectedItem(columnToSelect);
								}
								else {
									selectorColumnCombo.setSelectedIndex(0);
								}
							}
						}
						setEnableSelectorColumnCombo(true);
					}
					else {
						selectorColumnCombo.setEnabled(false);
					}
				}
				finally {
					enumSourceDetailCombo.addActionListener(enumSourceDetailComboL);
				}
				enumDetailCardLayout.show(enumDetailCenterPanel, "EXTERNAL");
			}
		}

		// populate column message fragments
		columnMessageTableModel.removeAllRows();
		for (Iterator<ColumnMessageFragmentDigest> iter = column.getAllMessageFragmentDigests().iterator(); iter.hasNext();) {
			columnMessageTableModel.addRow(iter.next());
		}

		// populate attribute items if any
		attributeItemTableModel.removeAllRows();
		if (column.getColumnDataSpecDigest().hasAttributeItem()) {
			for (Iterator<ColumnAttributeItemDigest> iter = column.getColumnDataSpecDigest().getAllAttributeItems().iterator(); iter.hasNext();) {
				attributeItemTableModel.addRow(iter.next());
			}
		}
	}

	public final void populateFields(GridTemplate template, boolean dataChanged) {
		this.template = template;
		columnTableModel.setData(template.getColumns(), dataChanged);
		clearColumnFields();
		currentColumn = null;
	}

	protected void postCreateColumnAction() {
	}

	protected void postDeleteColumnAction() {
	}

	private void refreshSelectorColumnComboItems(GridTemplateColumn currentColumn) {
		selectorColumnComboBoxModel.removeAllElements();
		if (currentColumn != null && enumSourceDetailCombo.getItemCount() > 0) {
			// populate selectorColumnCombo
			selectorColumnComboBoxModel.addElement(null);
			for (int i = 1; i <= template.getColumnCount(); i++) {
				if (template.getColumn(i).getColumnDataSpecDigest().canBeSelector() && !template.getColumn(i).getName().equals(currentColumn.getName())) {
					selectorColumnComboBoxModel.addElement(template.getColumn(i));
				}
			}
		}
	}

	private synchronized void removeDocumentListener(TemplateDetailPanel.FieldChangeListener changeListener) {
		attrMapField.removeChangeListener(changeListener);
		titleField.getDocument().removeDocumentListener(changeListener);
		colDescField.getDocument().removeDocumentListener(changeListener);
		fontField.getDocument().removeDocumentListener(changeListener);
		widthField.getDocument().removeDocumentListener(changeListener);
		colorField.getDocument().removeDocumentListener(changeListener);
		allowNullCheckBox.removeActionListener(changeListener);
		multiSelectCheckBox.removeActionListener(changeListener);
		sortEnumCheckBox.removeActionListener(changeListener);
		showLHSAttrCheckBox.removeActionListener(changeListener);
		dataTypeCombo.removeActionListener(changeListener);
		minField.getDocument().removeDocumentListener(changeListener);
		maxField.getDocument().removeDocumentListener(changeListener);
		minFloatField.getDocument().removeDocumentListener(changeListener);
		maxFloatField.getDocument().removeDocumentListener(changeListener);
		precisionField.getDocument().removeDocumentListener(changeListener);
		columnTableModel.removeTableModelListener(changeListener);
		columnMessageTableModel.removeTableModelListener(changeListener);
		attributeItemTableModel.removeTableModelListener(changeListener);
		enumValueListModel.removeListDataListener(changeListener);
		entityTypeCombo.removeActionListener(changeListener);
		entityContentCombo.removeActionListener(changeListener);
		entityTypeCombo.removeActionListener(entityTypeListener);
		enumSourceTypeCombo.removeActionListener(changeListener);
		enumSourceDetailCombo.removeActionListener(changeListener);
		selectorColumnCombo.removeActionListener(changeListener);
	}

	private void resetEntityContentCombo(boolean allowCatgory) {
		resetEntityContentCombo(allowCatgory, ClientUtil.getInstance().getLabel("label.entity"));
	}

	private void resetEntityContentCombo(boolean allowCatgory, String valueToSelect) {
		entityContentCombo.removeAllItems();
		if (allowCatgory) {
			entityContentCombo.addItem(ClientUtil.getInstance().getLabel("label.category"));
		}
		entityContentCombo.addItem(ClientUtil.getInstance().getLabel("label.entity"));
		if (allowCatgory) {
			entityContentCombo.addItem(ClientUtil.getInstance().getLabel("label.category.and.entity"));
		}
		if (valueToSelect != null) {
			entityContentCombo.setSelectedItem(valueToSelect);
		}
	}

	private void selectExternalEnumSource(String sourceName) {
		enumSourceDetailCombo.setSelectedIndex(getIndexOfExternalEnumSource(sourceName));
	}

	private void setEditable(boolean editable) {
		messageFragmentButtonPanel.setEnabledSelectionAwareButtons(editable && (columnTable.getSelectedRow() > -1));
		setEditableColumnFields(editable && (columnTable.getSelectedRow() > -1));

		if (editable && !ClientUtil.checkViewOrEditAnyTemplatePermission()) {
			return;
		}

		columnButtonPanel.setEnabledAll(editable);
		setEnabledColumnSelectionAwareButtons(editable);
	}

	private void setEditableColumnFields(boolean editable) {
		logger.debug(">>> setEditableColumnFields: " + editable);
		messageFragmentButtonPanel.setEnabled(editable);

		if (editable && !ClientUtil.checkViewOrEditAnyTemplatePermission()) {
			return;
		}
		titleField.setEditable(editable);
		fontField.setEditable(editable);
		widthField.setEditable(editable);
		colorField.setEditable(editable);
		minField.setEditable(editable);
		maxField.setEditable(editable);
		maxFloatField.setEditable(editable);
		minFloatField.setEditable(editable);
		precisionField.setEditable(editable);
		colDescField.setEditable(editable);
		attrMapField.setEnabled(editable);
		dataTypeCombo.setEnabled(editable);
		allowNullCheckBox.setEnabled(editable);
		sortEnumCheckBox.setEnabled(editable);
		showLHSAttrCheckBox.setEnabled(editable);
		multiSelectCheckBox.setEnabled(editable);
		attributeItemTable.setEnabled(editable);
		enumValueButtonPanel.setEnabled(editable);
		attrItemButtonPanel.setEnabled(editable);
		entityTypeCombo.setEnabled(editable);
		entityContentCombo.setEnabled(editable);
		if (editable) {
			enumValueButtonPanel.setEnabledSelectionAwareButtons(enumValueList.getSelectedIndex() > -1);
			attrItemButtonPanel.setEnabledSelectionAwareButtons(attributeItemTable.getSelectedRow() > -1);
		}
		enumValueList.setEnabled(editable);
		sortAscButton.setEnabled(editable);
		sortDescButton.setEnabled(editable);
		enumSourceDetailCombo.setEnabled(editable);
		enumSourceTypeCombo.setEnabled(editable);
		setEnableSelectorColumnCombo(editable);
	}

	@Override
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);
		setEditable(enable);
	}

	private void setEnabledAttributeItemSelectionAwareButtons(boolean enabled) {
		attrItemButtonPanel.setEnabledSelectionAwareButtons(enabled);
	}

	private void setEnabledColumnMessageSelectionAwareButtons(boolean enabled) {
		messageFragmentButtonPanel.setEnabledSelectionAwareButtons(enabled);
	}

	private void setEnabledColumnSelectionAwareButtons(boolean enabled) {
		if (enabled && !ClientUtil.checkViewOrEditAnyTemplatePermission()) {
			return;
		}

		columnButtonPanel.setEnabledSelectionAwareButtons(enabled && (columnTable.getSelectedRow() > -1));
		upColumnButton.setEnabled(enabled && (columnTable.getSelectedRow() >= 1));
		downColumnButton.setEnabled(enabled && (columnTable.getSelectedRow() < (template.getNumColumns() - 1)) && (columnTable.getSelectedRow() >= 0));
	}

	private void setEnabledEnumValueSelectionAwareButtons(boolean enabled) {
		enumValueButtonPanel.setEnabledSelectionAwareButtons(enabled);
		upEnumButton.setEnabled(enabled);
		downEnumButton.setEnabled(enabled);
	}

	private void setEnableSelectorColumnCombo(boolean editable) {
		if (editable) {
			ExternalEnumSourceDetail externalEnumSourceDetail = getSelectedExternalEnumSourceDetail();
			selectorColumnCombo.setEnabled(super.isEnabled() && externalEnumSourceDetail != null && externalEnumSourceDetail.isSupportsSelector());
		}
		else {
			selectorColumnCombo.setEnabled(false);
		}
	}

	private void setEntityTypeSelection(GenericEntityType newType) {
		entityTypeCombo.removeActionListener(entityTypeListener);
		try {
			entityTypeCombo.selectGenericEntityType(newType);
		}
		finally {
			entityTypeCombo.addActionListener(entityTypeListener);
		}
	}

	private void sortEnumValues(boolean ascending) {
		String[] values = new String[enumValueListModel.getSize()];

		for (int i = 0; i < values.length; i++) {
			values[i] = enumValueListModel.getElementAt(i);
		}

		if (ascending) {
			Arrays.sort(values);
		}
		else {
			Arrays.sort(values, Collections.reverseOrder());
		}

		enumValueListModel.clear();

		for (int i = 0; i < values.length; i++) {
			enumValueListModel.addElement(values[i]);
		}
	}

	private synchronized void updateCurrentColumn() throws ValidationException {
		if (currentColumn != null) {
			Util.checkEmpty(dataTypeCombo, "label.data.type");
			Util.checkEmpty(colNameField, "label.name");
			Util.checkEmpty(titleField, "label.title");

			// TT 1892 - Ensure that the title has not been used.
			String currColTitle = asValue(titleField.getText());
			if (currColTitle != null) {
				currColTitle = currColTitle.trim();
			}
			int currColID = currentColumn.getId();
			int numCols = columnTableModel.getRowCount(); // [rows of columns] - confusing, but getColumnCount() returns 2
			for (int colIndex = 0; colIndex < numCols; colIndex++) {
				GridTemplateColumn gtcol = columnTableModel.getColumnAt(colIndex);
				if (gtcol != null) {
					if (gtcol.getID() != currColID) {
						String colTitle = gtcol.getTitle();
						if (currColTitle.equalsIgnoreCase(colTitle)) {
							throw new ValidationException("msg.warning.not.unique", "Title");
						}
					}
				}
			}

			String dataType = (String) dataTypeCombo.getSelectedItem();

			boolean dataTypeChanged = !identical(dataType, currentColumn.getColumnDataSpecDigest().getType());

			currentColumn.setID(idField.getSelectedIndex() + 1);
			currentColumn.setName(asValue(colNameField.getText()));
			currentColumn.setTitle(currColTitle);
			currentColumn.setDescription(asValue(colDescField.getText()));
			currentColumn.getColumnDataSpecDigest().setIsBlankAllowed(allowNullCheckBox.isSelected());
			currentColumn.setFont(fontField.getText());
			currentColumn.setColor(colorField.getText());
			currentColumn.setColumnWidth(widthField.getIntValue());

			updateDataTypeDetails(dataTypeCombo.getSelectedIndex());

			if (dataTypeChanged) {
				currentColumn.getColumnDataSpecDigest().setType(dataType);
			}

			// refresh column list table
			int index = columnTableModel.indexOf(currentColumn);

			if (index > -1) {
				columnTableModel.fireTableRowsUpdated(index, index);
			}
		}
	}

	private void updateDataTypeDetails(int index) throws ValidationException {
		switch (index) {
		case 0: // boolean
		case 3: // date
		case 4: // daterange
		case 13: // rule id
		case 14: // string
		case 15: // symbol
		case 16: // timerange
			break;
		case 1: // currency
		case 2: // currency range
		case 8: // float
		case 9: // float range
			currentColumn.getColumnDataSpecDigest().setMaxValue(maxFloatField.hasValue() ? String.valueOf(maxFloatField.getValue()) : null);
			currentColumn.getColumnDataSpecDigest().setMinValue(minFloatField.hasValue() ? String.valueOf(minFloatField.getValue()) : null);
			// TT 1879 if precision field has no value, set precision to no value
			currentColumn.getColumnDataSpecDigest().setPrecision((precisionField.hasValue() ? precisionField.getValue().intValue() : FloatFormatter.NO_PRECISION));
			break;

		case 5: // dynamic string
			currentColumn.getColumnDataSpecDigest().setIsLHSAttributeVisible(showLHSAttrCheckBox.isSelected());
			currentColumn.getColumnDataSpecDigest().clearAttributeItems();

			for (Iterator<ColumnAttributeItemDigest> iter = attributeItemTableModel.getData().iterator(); iter.hasNext();) {
				currentColumn.getColumnDataSpecDigest().addAttributeItem(iter.next());
			}
			break;

		case 6: // entity
			if (!entityTypeCombo.hasSelection()) throw new ValidationException("msg.warning.select.entity.type", new Object[0]);
			currentColumn.getColumnDataSpecDigest().setEntityType(entityTypeCombo.getSelectedEntityType().toString());
			currentColumn.getColumnDataSpecDigest().setIsMultiSelectAllowed(multiSelectCheckBox.isSelected());
			String contentStr = (String) entityContentCombo.getSelectedItem();
			if (contentStr.equals(ClientUtil.getInstance().getLabel("label.category.and.entity"))) {
				currentColumn.getColumnDataSpecDigest().setIsCategoryAllowed(true);
				currentColumn.getColumnDataSpecDigest().setIsEntityAllowed(true);
			}
			else if (contentStr.equals(ClientUtil.getInstance().getLabel("label.category"))) {
				currentColumn.getColumnDataSpecDigest().setIsCategoryAllowed(true);
				currentColumn.getColumnDataSpecDigest().setIsEntityAllowed(false);
			}
			else {
				currentColumn.getColumnDataSpecDigest().setIsCategoryAllowed(false);
				currentColumn.getColumnDataSpecDigest().setIsEntityAllowed(true);
			}
			currentColumn.getColumnDataSpecDigest().setIsEnumValueNeedSorted(sortEnumCheckBox.isSelected());
			break;

		case 7: // enumlist
			if (enumSourceTypeCombo.getSelectedIndex() < 0) throw new ValidationException("msg.warning.enum.column.select.type", new Object[0]);
			ColumnDataSpecDigest columnDataSpecDigest = currentColumn.getColumnDataSpecDigest();
			ColumnDataSpecDigest.EnumSourceType sourceType = (ColumnDataSpecDigest.EnumSourceType) enumSourceTypeCombo.getSelectedItem();

			columnDataSpecDigest.setEnumSourceType(sourceType);
			columnDataSpecDigest.setIsMultiSelectAllowed(multiSelectCheckBox.isSelected());
			columnDataSpecDigest.setIsEnumValueNeedSorted(sortEnumCheckBox.isSelected());

			columnDataSpecDigest.clearEnumValues();
			if (sourceType == EnumSourceType.COLUMN) {
				for (Enumeration<?> enumeration = enumValueListModel.elements(); enumeration.hasMoreElements();) {
					String element = (String) enumeration.nextElement();
					columnDataSpecDigest.addColumnEnumValue(element);
				}
			}
			else if (sourceType == EnumSourceType.DOMAIN_ATTRIBUTE) {
				if (!attrMapField.hasValue()) throw new ValidationException("msg.warning.enum.column.select.attribute", new Object[0]);
				columnDataSpecDigest.setAttributeMap(attrMapField.getValue());
			}
			else {
				if (enumSourceDetailCombo.getSelectedIndex() < 0) throw new ValidationException("msg.warning.enum.column.select.source", new Object[0]);
				columnDataSpecDigest.setEnumSourceName(((ExternalEnumSourceDetail) enumSourceDetailCombo.getSelectedItem()).getName());
				if (selectorColumnCombo.getSelectedItem() != null && selectorColumnCombo.getSelectedItem() instanceof GridTemplateColumn) {
					columnDataSpecDigest.setEnumSelectorColumnName(((GridTemplateColumn) selectorColumnCombo.getSelectedItem()).getName());
				}
				else {
					columnDataSpecDigest.setEnumSelectorColumnName(null);
				}
			}
			break;

		case 10: // integer
		case 11: // integer range
			currentColumn.getColumnDataSpecDigest().setMaxValue(maxField.hasValue() ? String.valueOf(maxField.getValue()) : null);
			currentColumn.getColumnDataSpecDigest().setMinValue(minField.hasValue() ? String.valueOf(minField.getValue()) : null);
			break;
		}
	}

	public final void updateFromFields() throws ValidationException {
		if (currentColumn != null) {
			updateCurrentColumn();
		}
	}

	/**
	 * Validate attribute map text.
	 * Returns <code>null</code> on success.
	 * @param text
	 * @return message resource key if validation failed; <code>null</code>, otherwise
	 */
	protected String validateAttributeMap() {
		return null;
	}
}

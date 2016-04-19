package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;

public class MultiSelectEnumCellEditor extends AbstractCellEditor {

	private final JCheckBox notCheckbox = new JCheckBox(ClientUtil.getInstance().getLabel("checkbox.exclude.enum"));
	private JButton button = new JButton("");
	private final boolean viewOnly;
	private final ColumnDataSpecDigest columnDataSpecDigest;
	private final DefaultListModel listModel;
	private final String title, instruction;
	private EnumValues<EnumValue> enumValues = null; // the currently selected value(s), we should allow the super class to keep track of this
	private final AbstractGridTableModel<?> tableModel; // work around described in TT 1494, remove instance variable when fixed

	public MultiSelectEnumCellEditor(String columnName, ColumnDataSpecDigest columnDataSpecDigest, boolean viewOnly, AbstractGridTableModel<?> tableModel) {
		this.listModel = new DefaultListModel();
		this.columnDataSpecDigest = columnDataSpecDigest;
		this.viewOnly = viewOnly;
		this.title = columnName.toUpperCase() + ' ' + ClientUtil.getInstance().getLabel("label.title.enum.chooser");
		this.instruction = "Select Values for " + columnName + ":";
		this.tableModel = tableModel; // work around described in TT 1494, remove from parameter list when fixed
		init();
		setClickCountToStart(2);
	}

	private void init() {
		button.addMouseListener(new EditingStoppingSingleMouseClickListener(this));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);

		List<EnumValue> list = EnumCellEditor.fetchSortedEnumValueListIfConfigured(columnDataSpecDigest);
		resetListModel(list);
	}

	private final void resetListModel(List<EnumValue> list) {
		listModel.clear();
		if (list != null && list.size() > 0) {
			for (EnumValue ev : list) {
				if (ev.isActive()) {
					listModel.addElement(ev);
				}
			}
		}
	}

	private JPanel createListSelectionPanel(JList jlist) {
		JPanel selectionPanel = new JPanel(new BorderLayout(4, 4));

		jlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlist.setModel(listModel);
		jlist.setCellRenderer(new EnumValueCellRenderer());

		selectionPanel.add(new JLabel(instruction), BorderLayout.NORTH);
		selectionPanel.add(new JScrollPane(jlist), BorderLayout.CENTER);
		selectionPanel.add(notCheckbox, BorderLayout.SOUTH);

		return selectionPanel;
	}

	private void resetListModelForSelectionIfNeeded(int row) {
		// reset listModel if columnDataSpecDigest has a selector
		if (columnDataSpecDigest.getEnumSourceType() == EnumSourceType.EXTERNAL && columnDataSpecDigest.isEnumSelectorColumnSet()) {
			Object cellValue = tableModel.getCellValueAt(row, columnDataSpecDigest.getEnumSelectorColumnName());
			String selectorValue = (cellValue == null ? null : (cellValue instanceof EnumValue ? ((EnumValue) cellValue).getDeployValue() : cellValue.toString()));

			List<EnumValue> list = EnumCellEditor.fetchSortedApplicableEnumValueListIfConfigured(columnDataSpecDigest, selectorValue);
			resetListModel(list);
		}
	}

	private void editValueSelection() {
		ClientUtil.getLogger().debug("---> editValueSelection");
		JList jlist = new JList();
		JPanel selectionPanel = createListSelectionPanel(jlist);

		populateSelectedValues(jlist);
		ClientUtil.getLogger().debug("Selected values populated; opening editor dialog...");

		int option = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), selectionPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION) {
			ClientUtil.getLogger().debug("Updating enum value from list...");
			updateEnumValueFromList(jlist);
		}
		ClientUtil.getLogger().debug("<--- editValueSelection");
	}

	private void updateEnumValueFromList(JList jlist) {
		ClientUtil.getLogger().debug("---> updateEnumValueFromList");
		String previousValues = enumValues == null ? "" : enumValues.toString(); // work around described in TT 1494

		ClientUtil.getLogger().debug("Previous Values = " + previousValues);
		ClientUtil.getLogger().debug("EnumValues = " + enumValues);

		if (enumValues == null) {
			enumValues = new EnumValues<EnumValue>();
		}
		else {
			enumValues.clear();
		}
		Object[] objects = jlist.getSelectedValues();
		if (objects == null || objects.length == 0) {
			enumValues.setSelectionExclusion(false);
		}
		else {
			enumValues.setSelectionExclusion(notCheckbox.isSelected());
			for (int i = 0; i < objects.length; i++) {
				enumValues.add((EnumValue) objects[i]);
			}
		}
		refreshInternals();

		// work around described in TT 1494
		if (!previousValues.equals(enumValues.toString())) {
			tableModel.setDirty(true);
		}
		ClientUtil.getLogger().debug("<--- updateEnumValueFromList");
	}

	private void populateSelectedValues(JList jlist) {
		if (enumValues == null) {
			notCheckbox.setSelected(false);
			jlist.clearSelection();
		}
		else {
			List<Integer> indexList = new LinkedList<Integer>();

			notCheckbox.setSelected(enumValues.isSelectionExclusion());
			for (Iterator<EnumValue> enumValIter = enumValues.iterator(); enumValIter.hasNext();) {
				indexList.add(new Integer(listModel.indexOf(enumValIter.next())));

			}
			jlist.setSelectedIndices(UtilBase.toIntArray(indexList));
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		setValue_internal(value);
		button.setEnabled(!this.viewOnly);
		resetListModelForSelectionIfNeeded(row);
		return button;
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() >= getClickCountToStart()) {
				// check if list model is empty
				if (listModel.isEmpty() && columnDataSpecDigest.isEnumSelectorColumnSet()) {
					String columnName = this.tableModel.getColumnName(tableModel.getColumnIndex(columnDataSpecDigest.getEnumSelectorColumnName()));
					ClientUtil.getInstance().showWarning("msg.warning.select.column.selector", new Object[] { columnName });
				}
				else {
					editValueSelection();
				}
			}
			return true;
		}
		return super.shouldSelectCell(eventobject);
	}

	public Object getCellEditorValue() {
		return enumValues;
	}

	public void setCellEditorValue(Object value) {
		setValue_internal(value);
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setValue_internal(Object value) {
		if (value instanceof EnumValues) {
			this.enumValues = (EnumValues) value;
		}
		// This occurs if the column is changed to multi-select from single select
		else if (value instanceof EnumValue) {
			this.enumValues = new EnumValues();
			this.enumValues.add((EnumValue) value);
		}
		else {
			this.enumValues = null;
		}
		refreshInternals();
	}

	private final void refreshInternals() {
		ClientUtil.getLogger().debug("---> refreshInternals");

		ClientUtil.getLogger().debug("EnumValues = " + enumValues);

		notCheckbox.setSelected(enumValues != null && enumValues.isSelectionExclusion());
		button.setText(MultiSelectEnumCellRenderer.toDisplayString(enumValues));

		ClientUtil.getLogger().debug("<--- refreshInternals");
	}

}
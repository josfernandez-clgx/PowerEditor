package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.GenericEntityCheckList;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

class MultiSelectEntityCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private JButton editorComponent;
	private JPanel editPanel;
	private JPanel actionsPanel;
	private final JCheckBox notCheckbox = new JCheckBox(ClientUtil.getInstance().getLabel("checkbox.exclude.enum"));
	private JButton selectAllButton = UIFactory.createButton("", "image.btn.checkall", null, "button.tooltip.select.all.entities", false);
	private JButton clearAllButton = UIFactory.createButton("", "image.btn.small.clear", null, "button.tooltip.clearall", false);
	private final GenericEntityCheckList checkList;
	private final GenericEntityType entityType;
	private final String columnTitle;
	private AbstractGridTableModel<?> tableModel;

	public MultiSelectEntityCellEditor(String columnTitle, GenericEntityType entityType, boolean viewOnly, AbstractGridTableModel<?> tableModel) {
		this.columnTitle = columnTitle;
		this.entityType = entityType;
		this.viewOnly = viewOnly;
		this.checkList = new GenericEntityCheckList(entityType);
		this.tableModel = tableModel;
		initEditor();
		setClickCountToStart(2);
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				if (JOptionPane.showConfirmDialog(
						ClientUtil.getApplet(),
						editPanel,
						ClientUtil.getInstance().getLabel("d.title.edit.generic", new Object[] { columnTitle }),
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE) == 0) {
					if (isDirty()) {
						tableModel.setDirty(true);
					}
					setCategoryOrEntityValuesFromGUI();
					refreshEditorComponentText();
				}
			}
			else {
				return true;
			}
		}
		return flag;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		CategoryOrEntityValues values = null;
		if (value instanceof CategoryOrEntityValues) {
			values = (CategoryOrEntityValues) value;
		}
		else {
			values = CategoryOrEntityValues.parseCategoryOrEntityValues((String) value, entityType.toString(), true, false);
		}
		setInternalValue(values);
		return editorComponent;
	}

	private void setInternalValue(CategoryOrEntityValues values) {
		setCellEditorValue(values);
		refreshEditorComponentText();
		// update GUI elements
		checkList.clearSelection();
		if (values == null || values.isEmpty()) {
			notCheckbox.setSelected(false);
		}
		else {
			notCheckbox.setSelected(values.isSelectionExclusion());
			int[] selectedIndices = new int[values.size()];
			Map<Integer, Integer> entitiesMap = EntityModelCacheFactory.getInstance().getGenericEntityListModelMap(entityType);
			int n = 0;
			for (Iterator<CategoryOrEntityValue> i = values.iterator(); i.hasNext();) {
				Integer index = entitiesMap.get(new Integer(i.next().getId()));
				selectedIndices[n] = (index == null ? -1 : index.intValue());
				n++;
			}
			checkList.setSelectedIndices(selectedIndices);
		}
	}

	private void refreshEditorComponentText() {
		CategoryOrEntityValues values = (CategoryOrEntityValues) getCellEditorValue();
		editorComponent.setText((values == null || values.size() == 0 ? null : CategoryEntityMultiSelectCellRenderer.getDisplayValue(values, true, false)));
	}

	private boolean isDirty() {
		CategoryOrEntityValues values = (CategoryOrEntityValues) getCellEditorValue();
		if (values == null) {
			return checkList.getSelectedGenericEntities().length != 0;
		}
		else if (values.isSelectionExclusion() != notCheckbox.isSelected()) {
			return true;
		}
		else {
			GenericEntity[] entities = checkList.getSelectedGenericEntities();
			if (entities.length != values.size()) {
				return true;
			}
			else {
				for (int i = 0; i < entities.length; i++) {
					if (!values.contains(new CategoryOrEntityValue(entities[i]))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private void setCategoryOrEntityValuesFromGUI() {
		CategoryOrEntityValues values = (CategoryOrEntityValues) getCellEditorValue();
		if (values == null) {
			values = new CategoryOrEntityValues();
			setCellEditorValue(values);
		}
		else {
			values.clear();
		}
		values.setSelectionExclusion(notCheckbox.isSelected());
		GenericEntity[] entities = checkList.getSelectedGenericEntities();
		if (entities != null) {
			for (int i = 0; i < entities.length; i++) {
				values.add(new CategoryOrEntityValue(entities[i]));
			}
		}
	}

	private void initEditor() {
		if (editorComponent == null) {
			editorComponent = new JButton();
			editorComponent.addMouseListener(new EditingStoppingSingleMouseClickListener(this));
		}
		if (editPanel == null) {
			editPanel = UIFactory.createBorderLayoutPanel(2, 2);
			actionsPanel = UIFactory.createFlowLayoutPanelLeftAlignment(1, 2);
			actionsPanel.add(notCheckbox);
			actionsPanel.add(selectAllButton);
			actionsPanel.add(clearAllButton);
			editPanel.add(actionsPanel, BorderLayout.NORTH);
			editPanel.add(new JScrollPane(checkList), BorderLayout.CENTER);
			selectAllButton.setBackground(null);
			clearAllButton.setBackground(null);
			selectAllButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					checkList.selectAll(true);
				}
			});
			clearAllButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					checkList.selectAll(false);
				}
			});

		}
	}
}
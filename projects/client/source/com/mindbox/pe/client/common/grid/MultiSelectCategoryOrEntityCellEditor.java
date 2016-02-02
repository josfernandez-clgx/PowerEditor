package com.mindbox.pe.client.common.grid;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.tree.GenericCategoryTreeWithCheckBox;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * Table cell editor for multi-select category or entity column.
 * @author Geneho Kim
 *
 */
public class MultiSelectCategoryOrEntityCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private boolean allowEntity;
	private JButton editorComponent;
	private JPanel editPanel;
	private JPanel actionsPanel;
	private final GenericEntityType entityType;
	private final GenericCategoryTreeWithCheckBox selectionTree;
	private final String columnTitle;
	private AbstractGridTableModel tableModel;
	private final JCheckBox notCheckbox = new JCheckBox(ClientUtil.getInstance().getLabel("checkbox.exclude.enum"));
	private JButton selectAllEntitiesButton = UIFactory.createButton(
			"",
			"image.btn.checkall",
			null,
			"button.tooltip.select.all.entities",
			false);
	private JButton clearAllButton = UIFactory.createButton("", "image.btn.small.clear", null, "button.tooltip.clearall", false);

	public MultiSelectCategoryOrEntityCellEditor(String columnTitle, GenericEntityType entityType, boolean allowEntity, boolean viewOnly,
			AbstractGridTableModel tableModel, boolean sort) {
		this.columnTitle = columnTitle;
		this.entityType = entityType;
		this.viewOnly = viewOnly;
		this.allowEntity = allowEntity;
		this.tableModel = tableModel;
		if (entityType.hasCategory()) {
			this.selectionTree = new GenericCategoryTreeWithCheckBox(entityType, allowEntity, sort);
		}
		else {
			ClientUtil.getLogger().warn(entityType + " does not support categories!!!");
			this.selectionTree = null;
		}
		initEditor();
		setClickCountToStart(2);
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public boolean shouldSelectCell(EventObject eventobject) {
		boolean flag = super.shouldSelectCell(eventobject);
		if (selectionTree != null && eventobject instanceof MouseEvent) {
			MouseEvent mouseevent = (MouseEvent) eventobject;
			if (mouseevent.getClickCount() > 1) {
				final MainApplication rootFrame = ClientUtil.getParent();
				rootFrame.setCursor(UIFactory.getWaitCursor());
				try {
					this.editorComponent.setCursor(UIFactory.getWaitCursor());
					if (JOptionPane.showConfirmDialog(ClientUtil.getApplet(), editPanel, ClientUtil.getInstance().getLabel(
							"d.title.edit.generic",
							new Object[] { columnTitle }), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
						setCategoryOrEntityValuesFromGUI();
						refreshEditorComponentText();
						if (selectionTree.isDirty()) {
							tableModel.setDirty(true);
						}
					}
				}
				finally {
					this.editorComponent.setCursor(UIFactory.getWaitCursor());
					rootFrame.setCursor(UIFactory.getDefaultCursor());
				}
			}
			else {
				return true;
			}
		}
		return flag;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		CategoryOrEntityValues categoryOrEntityValues = null;
		if (value instanceof CategoryOrEntityValues) {
			categoryOrEntityValues = (CategoryOrEntityValues) value;
		}
		else if (value instanceof String) {
			categoryOrEntityValues = CategoryOrEntityValues.parseCategoryOrEntityValues((String) value, entityType.toString(), true, true);
		}
		setInternalValue(categoryOrEntityValues);
		return editorComponent;
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
		List<Integer> idList = selectionTree.getSelectedGenericCategoryIDs();
		addCategoryOrEntityValue(values, false, idList);
		idList = selectionTree.getSelectedGenericEntityIDs();
		addCategoryOrEntityValue(values, true, idList);
	}

	private void addCategoryOrEntityValue(CategoryOrEntityValues values, boolean forEntity, List<Integer> idList) {
		if (idList != null && !idList.isEmpty()) {
			for (Iterator<Integer> iter = idList.iterator(); iter.hasNext();) {
				Integer element = iter.next();
				if (element != null) {
					values.add(new CategoryOrEntityValue(entityType, forEntity, element.intValue()));
				}
			}
		}
	}

	private void setInternalValue(CategoryOrEntityValues categoryOrEntityValues) {
		setCellEditorValue(categoryOrEntityValues);
		refreshEditorComponentText();
		List<Integer> categories = new ArrayList<Integer>();
		List<Integer> entities = new ArrayList<Integer>();
		if (categoryOrEntityValues != null) {
			notCheckbox.setSelected(categoryOrEntityValues.isSelectionExclusion());
			for (Iterator<CategoryOrEntityValue> iter = categoryOrEntityValues.iterator(); iter.hasNext();) {
				CategoryOrEntityValue element = iter.next();
				if (!element.isForEntity()) {
					categories.add(new Integer(element.getId()));
				}
			}
			for (Iterator<CategoryOrEntityValue> iter = categoryOrEntityValues.iterator(); iter.hasNext();) {
				CategoryOrEntityValue element = iter.next();
				if (element.isForEntity()) {
					entities.add(new Integer(element.getId()));
				}
			}
		}
		else {
			notCheckbox.setSelected(false);
		}
		selectionTree.setSelectedCategoriesAndEntities(categories, entities);
	}

	private void refreshEditorComponentText() {
		CategoryOrEntityValues categoryOrEntityValues = (CategoryOrEntityValues) super.getCellEditorValue();
		editorComponent.setText((categoryOrEntityValues == null ? null : CategoryEntityMultiSelectCellRenderer.getDisplayValue(
				categoryOrEntityValues,
				true,
				false)));
	}

	private void initEditor() {
		if (editorComponent == null) {
			editorComponent = new JButton();
			editorComponent.addMouseListener(new EditingStoppingSingleMouseClickListener(this));
		}

		if (editPanel == null) {
			editPanel = UIFactory.createBorderLayoutPanel(2, 2);

			clearAllButton.setBackground(null);
			selectAllEntitiesButton.setBackground(null);
			actionsPanel = UIFactory.createFlowLayoutPanelLeftAlignment(1, 2);
			actionsPanel.add(notCheckbox);
			if (allowEntity) {
				actionsPanel.add(selectAllEntitiesButton);
			}
			actionsPanel.add(clearAllButton);

			editPanel.add(actionsPanel, BorderLayout.NORTH);
			if (selectionTree == null) {
				JLabel label = new JLabel(ClientUtil.getInstance().getLabel(
						"msg.warning.invalid.entity.no.category",
						new Object[] { entityType.toString() }));
				editPanel.add(new JScrollPane(label), BorderLayout.NORTH);
			}
			else {
				editPanel.add(selectionTree.getJComponent(), BorderLayout.CENTER);
			}
			notCheckbox.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					selectionTree.setDirty(true);
				}

			});
		}
		selectAllEntitiesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent actionevent) {
				selectionTree.selectAllEntities();
			}
		});

		clearAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent actionevent) {
				selectionTree.clearAll();
			}
		});

	}

}
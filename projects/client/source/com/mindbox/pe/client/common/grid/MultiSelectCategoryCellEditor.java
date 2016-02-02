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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.client.common.tree.GenericCategoryTreeWithCheckBox;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;

/**
 * Table cell editor for multi-select category only columns.
 * @author Geneho Kim
 *
 */
public class MultiSelectCategoryCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private JButton editorComponent;
	private JPanel editPanel;
	private final GenericEntityType entityType;
	private final GenericCategoryTreeWithCheckBox selectionTree;
	private final String columnTitle;
	private final JCheckBox notCheckbox = new JCheckBox(ClientUtil.getInstance().getLabel("checkbox.exclude.enum"));
	private JButton expandAllButton = UIFactory.createButton("", "image.btn.expandall", null, "button.tooltip.expandall", false);
	private JButton collapseAllButton = UIFactory.createButton("", "image.btn.collapseall", null, "button.tooltip.collapeseall", false);

	public MultiSelectCategoryCellEditor(String columnTitle, GenericEntityType entityType, boolean viewOnly, boolean sort) {
		this.columnTitle = columnTitle;
		this.entityType = entityType;
		this.viewOnly = viewOnly;
		this.selectionTree = new GenericCategoryTreeWithCheckBox(entityType, false, sort);
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
				final MainApplication rootFrame = ClientUtil.getParent();
				rootFrame.setCursor(UIFactory.getWaitCursor());
				try {
					if (JOptionPane.showConfirmDialog(ClientUtil.getApplet(), editPanel, ClientUtil.getInstance().getLabel(
							"d.title.edit.generic",
							new Object[] { columnTitle }), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
						setCategoryOrEntityValuesFromGUI();
						refreshEditorComponentText();
					}
				}
				finally {
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
			categoryOrEntityValues = CategoryOrEntityValues.parseCategoryOrEntityValues((String) value, entityType.toString(), false, true);
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
		if (idList != null) {
			for (Iterator<Integer> iter = idList.iterator(); iter.hasNext();) {
				Integer element = iter.next();
				if (element != null) {
					values.add(new CategoryOrEntityValue(entityType, false, element.intValue()));
				}
			}
		}
	}

	private void setInternalValue(CategoryOrEntityValues categoryOrEntityValues) {
		setCellEditorValue(categoryOrEntityValues);
		refreshEditorComponentText();
		// select category
		List<Integer> idList = new ArrayList<Integer>();
		if (categoryOrEntityValues != null) {
			for (Iterator<CategoryOrEntityValue> iter = categoryOrEntityValues.iterator(); iter.hasNext();) {
				CategoryOrEntityValue element = iter.next();
				if (!element.isForEntity()) {
					idList.add(new Integer(element.getId()));
				}
			}
			notCheckbox.setSelected(categoryOrEntityValues.isSelectionExclusion());
		}
		else {
			notCheckbox.setSelected(false);
		}
		selectionTree.setSelectedCategoriesAndEntities(idList, null);
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
			editPanel.add(notCheckbox, BorderLayout.NORTH);
			editPanel.add(selectionTree.getJComponent(), BorderLayout.CENTER);
			expandAllButton.setBackground(null);
			collapseAllButton.setBackground(null);
			expandAllButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					try {
						editPanel.setCursor(UIFactory.getWaitCursor());
						selectionTree.expandAll(true);
					}
					finally {
						editPanel.setCursor(UIFactory.getDefaultCursor());
					}
				}
			});

			collapseAllButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent actionevent) {
					try {
						editPanel.setCursor(UIFactory.getWaitCursor());
						selectionTree.expandAll(false);
					}
					finally {
						editPanel.setCursor(UIFactory.getDefaultCursor());
					}
				}
			});
		}
	}

}
package com.mindbox.pe.client.common.grid;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractCellEditor;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

public abstract class AbstractSingleSelectCategoryOrEntityCellEditor extends AbstractCellEditor {

	private boolean viewOnly;
	private JButton editorComponent;
	protected JPanel editPanel;
	protected final GenericEntityType entityType;
	protected final boolean allowEntity;
	protected final String columnTitle;
	protected final boolean sort;

	public AbstractSingleSelectCategoryOrEntityCellEditor(String columnTitle, GenericEntityType entityType, boolean allowEntity, boolean viewOnly, boolean sort) {
		this.columnTitle = columnTitle;
		this.allowEntity = allowEntity;
		this.entityType = entityType;
		this.viewOnly = viewOnly;
		this.sort = sort;
		initEditor();
		setClickCountToStart(2);
	}

	protected abstract void initEditPanel();
    
    protected abstract void valueChanged();    

	protected abstract CategoryOrEntityValue getCategoryOrEntityValueFromGUI();

	protected abstract void setInternals(CategoryOrEntityValue categoryOrEntityValue);

	public boolean isCellEditable(EventObject eventobject) {
		return !viewOnly;
	}

	public final boolean shouldSelectCell(EventObject eventobject) {
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
						if (!(getCellEditorValue() == null && getCategoryOrEntityValueFromGUI() == null)
								&& (getCellEditorValue() == null && getCategoryOrEntityValueFromGUI() != null
										|| (getCellEditorValue() != null && getCategoryOrEntityValueFromGUI() == null) || !getCellEditorValue().equals(
										getCategoryOrEntityValueFromGUI()))) {
							valueChanged();
						}
						CategoryOrEntityValue value = getCategoryOrEntityValueFromGUI();
						setInternalValue(value);
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

	public final Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		CategoryOrEntityValue categoryOrEntityValue = null;
		if (value instanceof CategoryOrEntityValue) {
			categoryOrEntityValue = (CategoryOrEntityValue) value;
		}
		else if (value instanceof String) {
			categoryOrEntityValue = CategoryOrEntityValue.valueOf((String) value, entityType.toString(), true, true);
		}
		setInternalValue(categoryOrEntityValue);
		return editorComponent;
	}

	private void setInternalValue(CategoryOrEntityValue categoryOrEntityValue) {
		setCellEditorValue(categoryOrEntityValue);
		refreshEditorComponentText();
		setInternals(categoryOrEntityValue);
	}

	private void refreshEditorComponentText() {
		CategoryOrEntityValue categoryOrEntityValue = (CategoryOrEntityValue) super.getCellEditorValue();
		editorComponent.setText((categoryOrEntityValue == null ? null : CategoryEntitySingleSelectCellRenderer.getDisplayValue(categoryOrEntityValue)));
	}

	private void initEditor() {
		initEditPanel();
		if (editorComponent == null) {
			editorComponent = new JButton();
			editorComponent.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent actionevent) {
					fireEditingStopped();
				}

			});
		}
	}

}
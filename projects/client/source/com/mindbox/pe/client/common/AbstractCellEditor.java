package com.mindbox.pe.client.common;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;

public abstract class AbstractCellEditor implements TableCellEditor {

	public boolean shouldSelectCell(EventObject eventobject) {
		return true;
	}

	public void removeCellEditorListener(CellEditorListener celleditorlistener) {
		listenerList.remove(javax.swing.event.CellEditorListener.class, celleditorlistener);
	}

	protected void fireEditingStopped() {
		Object aobj[] = listenerList.getListenerList();
		for (int i = aobj.length - 2; i >= 0; i -= 2)
			if (aobj[i] == (javax.swing.event.CellEditorListener.class)) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener) aobj[i + 1]).editingStopped(changeEvent);
			}

	}

	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	public abstract Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int i, int j);

	public AbstractCellEditor() {
		listenerList = new EventListenerList();
		changeEvent = null;
		clickCountToStart = 1;
	}

	public boolean isCellEditable(EventObject eventobject) {
		return !(eventobject instanceof MouseEvent) || ((MouseEvent) eventobject).getClickCount() >= clickCountToStart;
	}

	public void setClickCountToStart(int i) {
		clickCountToStart = i;
	}

	public void addCellEditorListener(CellEditorListener celleditorlistener) {
		listenerList.add(javax.swing.event.CellEditorListener.class, celleditorlistener);
	}

	public int getClickCountToStart() {
		return clickCountToStart;
	}

	public Object getCellEditorValue() {
		return value;
	}

	public void setCellEditorValue(Object obj) {
		value = obj;
	}

	protected void fireEditingCanceled() {
		Object aobj[] = listenerList.getListenerList();
		for (int i = aobj.length - 2; i >= 0; i -= 2)
			if (aobj[i] == (javax.swing.event.CellEditorListener.class)) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener) aobj[i + 1]).editingCanceled(changeEvent);
			}

	}

	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	protected EventListenerList listenerList;
	protected Object value;
	protected ChangeEvent changeEvent;
	protected int clickCountToStart;
}

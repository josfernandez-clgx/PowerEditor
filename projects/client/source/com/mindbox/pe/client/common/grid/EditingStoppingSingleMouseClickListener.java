package com.mindbox.pe.client.common.grid;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.mindbox.pe.client.common.AbstractCellEditor;

/**
 * Mouse listener that stoppes table cell editing on single click.
 * 
 * DAB: there is an issue with the ActionListener on the grid button
 * firing the action event during a double click.  Using the below MouseListener
 * alleviates the problem.  Old behavior in newer JREs was that the action cleared
 * out the enumValues before the double-click handler took effect.
 *  
 *  @since 5.8.0
 */
public class EditingStoppingSingleMouseClickListener extends MouseAdapter {

	AbstractCellEditor cellEditor;

	public EditingStoppingSingleMouseClickListener(AbstractCellEditor cellEditor) {
		this.cellEditor = cellEditor;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getClickCount() == 1) {
			cellEditor.stopCellEditing();
		}
	}

}

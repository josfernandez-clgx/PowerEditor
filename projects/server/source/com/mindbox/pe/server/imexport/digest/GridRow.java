/*
 * Created on 2004. 3. 22.
 *
 */
package com.mindbox.pe.server.imexport.digest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class GridRow {

	private final List<String> cellValueList = new LinkedList<String>();

	public void addCellValue(String name) {
		cellValueList.add(name);
	}
	
	public void setCellValue(String value) {
		addCellValue(value);
	}
	
	public String[] getCellValues() {
		return cellValueList.toArray(new String[0]);
	}
	
	public String getCellValueAt(int column) {
		if (column > 0 && column <= cellValueList.size()) {
			return cellValueList.get(column-1);
		}
		else {
			return null;
		}
	}
	
	public boolean isEmpty() {
		if (cellValueList.isEmpty()) { 
			return true;
		}
		else {
			for (Iterator<String> iter = cellValueList.iterator(); iter.hasNext();) {
				String element = iter.next();
				if (element != null && element.length() > 0) {
					return false;
				}
			}
			return true;
		}
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("ROW[");
		for (Iterator<String> iter = cellValueList.iterator(); iter.hasNext();) {
			String element = iter.next();
			buff.append(element);
			if (iter.hasNext()) {
				buff.append(',');
			}
		}
		return buff.toString();
	}
	
}

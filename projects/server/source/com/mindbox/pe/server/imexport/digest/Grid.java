package com.mindbox.pe.server.imexport.digest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.config.AbstractDigestedObjectHolder;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Grid extends AbstractDigestedObjectHolder {

	private static final long serialVersionUID = 2007071700001L;

	public static final String GRID_TYPE_GUIDELINE = "guideline";
	public static final String GRID_TYPE_PARAMETER = "parameter";
	
	private int templateID = 0;
	private String type = null;
	private final List<String> columnNameList = new LinkedList<String>();

	public List<EntityIdentity> getGridContext() {
		return super.getObjects(EntityIdentity.class);
	}

	public List<GridActivation> getActivations() {
		return super.getObjects(GridActivation.class);
	}
	
	public void setColumnName(String name) {
		addColumnName(name);
	}
	
	public void addColumnName(String name) {
		columnNameList.add((name == null ? "" : name));
	}
	
	public String[] getColumnNames() {
		return columnNameList.toArray(new String[0]);
	}

	public int findColumnNumberFor(String columnName) {
		if (columnName == null) {
			return -1;
		}
		for (int i = 0; i < columnNameList.size(); i++) {
			if (columnName.equals(columnNameList.get(i))) {
				return i+1;
			}
		}
		return -1;
	}
	
	public int getTemplateID() {
		return templateID;
	}

	public String getType() {
		return type;
	}

	public void setTemplateID(int i) {
		templateID = i;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Grid[type=");
		buff.append(type);
		buff.append(',');
		buff.append("template=");
		buff.append(templateID);
		buff.append(',');
		buff.append("columns=");
		for (Iterator<String> iter = columnNameList.iterator(); iter.hasNext();) {
			String element = iter.next();
			buff.append(element);
			if (iter.hasNext()) {
				buff.append(',');
			}
		}
		buff.append(System.getProperty("line.separator"));
		buff.append("-- context: ");
		buff.append(System.getProperty("line.separator"));
		for (Iterator<EntityIdentity> iter = getGridContext().iterator(); iter.hasNext();) {
			EntityIdentity element = (EntityIdentity) iter.next();
			buff.append(element);			
			if (iter.hasNext()) {
				buff.append(',');
			}
		}
		buff.append(System.getProperty("line.separator"));
		buff.append("-- activations: ");
		buff.append(System.getProperty("line.separator"));
		for (Iterator<GridActivation> iter = getActivations().iterator(); iter.hasNext();) {
			GridActivation element = iter.next();
			buff.append(element);			
			if (iter.hasNext()) {
				buff.append(System.getProperty("line.separator"));
			}
		}
		buff.append("]");
		return buff.toString();
	}
	
}

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
public class GridActivation {

	private int id = -1;
	private int parentID = -1;
	private String status = null;
	private String comment = null;
	private final List<Object> rowList = new LinkedList<Object>();
	private ActivationDates activationDates = null;
	private String statusChangedOn = null;
	private String createdOn = null;
	private int activationLabelID;

	
	/**
	 * @return Returns the activationLabelID.
	 */
	public int getActivationLabelID() {
		return activationLabelID;
	}
	/**
	 * @param activationLabelID The activationLabel to set.
	 */
	public void setActivationLabelID(int activationLabelID) {
		this.activationLabelID = activationLabelID;
	}
	
	public int getId() {
		return id;
	}

	public ActivationDates getActivationDates() {
		return activationDates;
	}

	public void setActivationDates(ActivationDates dates) {
		//System.out.println(">>> setActivationDates: " + dates);
		activationDates = dates;
	}

	public void addRow(Object obj) {
		if (obj instanceof GridRow) {
			if (!((GridRow) obj).isEmpty()) {
				rowList.add(obj);
			}
		}
	}

	public int countRows() {
		return (rowList == null ? 0 : rowList.size());
	}

	public GridRow[] getRows() {
		return rowList.toArray(new GridRow[0]);
	}

	public int getParentID() {
		return parentID;
	}

	public String getStatus() {
		return status;
	}

	public void setId(int i) {
		id = i;
	}

	public void setParentID(int i) {
		parentID = (i == 0 ? -1 : i);
	}

	public void setStatus(String string) {
		status = string;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String string) {
		comment = string;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Activation[");
		buff.append(id);
		buff.append(",parent=");
		buff.append(parentID);
		buff.append(",status=");
		buff.append(status);
		buff.append(",comment=");
		buff.append(comment);
		buff.append(",act=");
		buff.append(activationDates);
		buff.append(",row-data:");
		for (Iterator<Object> iter = rowList.iterator(); iter.hasNext();) {
			GridRow element = (GridRow) iter.next();
			buff.append(System.getProperty("line.separator"));
			buff.append("   ");
			buff.append(element);
		}
		return buff.toString();
	}

	public String getStatusChangedOn() {
		return statusChangedOn;
	}

	public void setStatusChangedOn(String string) {
		statusChangedOn = string;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String string) {
		createdOn = string;
	}

}

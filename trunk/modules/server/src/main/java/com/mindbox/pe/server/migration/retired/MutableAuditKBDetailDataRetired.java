package com.mindbox.pe.server.migration.retired;


/**
 * Encapsulates audit detail data.
 *
 */
@Deprecated
public final class MutableAuditKBDetailDataRetired implements AuditKBDetailDataRetired {

	private int kbAuditDetailID;
	private int elementTypeID;
	private String elementValue;

	public int getElementTypeID() {
		return elementTypeID;
	}

	public void setElementTypeID(int elementTypeID) {
		this.elementTypeID = elementTypeID;
	}

	public String getElementValue() {
		return elementValue;
	}

	public void setElementValue(String elementValue) {
		this.elementValue = elementValue;
	}

	public int getKbAuditDetailID() {
		return kbAuditDetailID;
	}

	public void setKbAuditDetailID(int kbAuditDetailID) {
		this.kbAuditDetailID = kbAuditDetailID;
	}
}

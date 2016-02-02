package com.mindbox.pe.server.spi.audit;


/**
 * Encapsulates audit detail data.
 *
 */
public final class MutableAuditKBDetailData implements AuditKBDetailData {

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

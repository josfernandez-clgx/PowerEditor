package com.mindbox.pe.server.spi.audit;


/**
 * Encapsulates audit detail data.
 *
 */
public final class DefaultAuditKBDetailData implements AuditKBDetailData {

	private int kbAuditDetailID;
	private int elementTypeID;
	private String elementValue;

	public DefaultAuditKBDetailData(int kbAuditDetailID, int elementTypeID, String elementValue) {
		this.kbAuditDetailID = kbAuditDetailID;
		this.elementTypeID = elementTypeID;
		this.elementValue = elementValue;
	}

	public int getElementTypeID() {
		return elementTypeID;
	}

	public String getElementValue() {
		return elementValue;
	}

	public int getKbAuditDetailID() {
		return kbAuditDetailID;
	}
}

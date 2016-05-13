package com.mindbox.pe.server.migration.retired;


/**
 * Encapsulates audit detail data.
 *
 */
@Deprecated
public final class DefaultAuditKBDetailDataRetired implements AuditKBDetailDataRetired {

	private int kbAuditDetailID;
	private int elementTypeID;
	private String elementValue;

	public DefaultAuditKBDetailDataRetired(int kbAuditDetailID, int elementTypeID, String elementValue) {
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

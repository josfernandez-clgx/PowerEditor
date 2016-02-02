package com.mindbox.pe.model;

public interface Auditable {

	Auditable deepCopy();

	int getID();

	/**
	 * Used in the ChangedElement@name attribute.
	 * @return
	 */
	String getAuditName();

	String getAuditDescription();
}

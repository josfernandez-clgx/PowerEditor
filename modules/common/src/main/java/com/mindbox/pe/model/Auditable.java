package com.mindbox.pe.model;

public interface Auditable {

	Auditable deepCopy();

	String getAuditDescription();

	/**
	 * Used in the ChangedElement@name attribute.
	 * @return name
	 */
	String getAuditName();

	int getID();
}

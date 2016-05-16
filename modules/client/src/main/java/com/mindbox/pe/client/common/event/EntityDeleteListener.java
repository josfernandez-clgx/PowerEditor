package com.mindbox.pe.client.common.event;

public interface EntityDeleteListener {

	/**
	 * Notifies that a generic entity is deleted.
	 * @param e event
	 */
	void entityDeleted(EntityDeleteEvent e);
}

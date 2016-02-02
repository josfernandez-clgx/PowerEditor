package com.mindbox.pe.communication;

import com.mindbox.pe.model.EntityType;

/**
 * Request to check if the specified date synonym is in use.
 * @since PowerEditor 5.0.1
 */
public class CheckForUniqueNameRequest extends SessionRequest<BooleanResponse> {


	private static final long serialVersionUID = 2007072510001L;

	private final EntityType entityType;
	private final String name;

	public CheckForUniqueNameRequest(String userID, String sessionID, EntityType entityType, String name) {
		super(userID, sessionID);
		this.entityType = entityType;
		this.name = name;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "CheckForUniqueNameRequest[type=" + entityType + ",name=" + name + ']';
	}
}

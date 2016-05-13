package com.mindbox.pe.communication;

import com.mindbox.pe.model.GenericEntityType;

/**
 * Request to check if the specified compatibility already exists.
 * @since PowerEditor 5.0.1
 */
public class CheckExistingCompatibilityRequest extends SessionRequest<BooleanResponse> {

	private static final long serialVersionUID = 2007072510001L;

	private final GenericEntityType entityType1;
	private final GenericEntityType entityType2;
	private final int id1, id2;

	public CheckExistingCompatibilityRequest(String userID, String sessionID, GenericEntityType type1, int id1,
			GenericEntityType type2, int id2) {
		super(userID, sessionID);
		this.entityType1 = type1;
		this.entityType2 = type2;
		this.id1 = id1;
		this.id2 = id2;
	}

	public GenericEntityType getEntityType1() {
		return entityType1;
	}

	public GenericEntityType getEntityType2() {
		return entityType2;
	}

	public int getId1() {
		return id1;
	}

	public int getId2() {
		return id2;
	}

}

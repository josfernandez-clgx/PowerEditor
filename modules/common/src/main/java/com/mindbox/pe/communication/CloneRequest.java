package com.mindbox.pe.communication;

import com.mindbox.pe.model.GenericEntity;

public class CloneRequest extends SaveRequest {
	
	private static final long serialVersionUID = 200607271000L;

	private boolean copyPolicies;

	public CloneRequest(String userID, String sessionID, GenericEntity object, boolean lockEntity, boolean forClone, boolean copyPolicies) {
		super(userID, sessionID, object, lockEntity, forClone);
		this.copyPolicies = copyPolicies;
	}

	public boolean shouldCopyPolicies() {
		return copyPolicies;
	}

}

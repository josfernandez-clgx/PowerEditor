package com.mindbox.pe.communication;

import com.mindbox.pe.model.admin.UserData;

/**
 * Request to reload user data from data source.
 * This retrieves user data from the data source
 * and repopulates cache. 
 * PE returns {@link com.mindbox.pe.communication.ListResponse}.
 */
public class ReloadUserDataRequest extends RequestComm<ListResponse<UserData>> {

	private static final long serialVersionUID = 2005040400001L;

	public String toString() {
		return "ReloadUserDataRequest[]";
	}

}

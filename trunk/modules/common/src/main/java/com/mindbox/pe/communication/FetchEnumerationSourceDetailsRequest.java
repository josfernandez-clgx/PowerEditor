package com.mindbox.pe.communication;

import com.mindbox.pe.model.ExternalEnumSourceDetail;

/**
 * Request to fetch a list of enumeration source names.
 *
 */
public class FetchEnumerationSourceDetailsRequest extends SessionRequest<ListResponse<ExternalEnumSourceDetail>> {

	private static final long serialVersionUID = 2008052710000001L;

	public FetchEnumerationSourceDetailsRequest(String userID, String sessionID) {
		super(userID, sessionID);
	}

}

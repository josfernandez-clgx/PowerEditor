package com.mindbox.pe.communication;

import com.mindbox.pe.model.EnumValue;

/**
 * Request to fetch a list of enumeration values of the source.
 *
 */
public class FetchAllEnumerationValuesRequest extends SessionRequest<ListResponse<EnumValue>> {

	private static final long serialVersionUID = 2008052810000001L;
	
	private final String sourceName;

	public FetchAllEnumerationValuesRequest(String sourceName, String userID, String sessionID) {
		super(userID, sessionID);
		this.sourceName = sourceName;
	}

	public String getSourceName() {
		return sourceName;
	}

}

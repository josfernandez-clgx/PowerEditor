package com.mindbox.pe.communication;

import com.mindbox.pe.model.DateSynonym;

/**
 * Request to check if the specified date synonym is in use.
 * @since PowerEditor 5.0.1
 */
public class DateSynonymInUseRequest extends SessionRequest<BooleanResponse> {

	private static final long serialVersionUID = 2006112810000L;

	private final DateSynonym dateSynonym;

	public DateSynonymInUseRequest(String userID, String sessionID, DateSynonym dateSynonym) {
		super(userID, sessionID);
		this.dateSynonym = dateSynonym;
	}

	public DateSynonym getDateSynonym() {
		return dateSynonym;
	}

	public String toString() {
		return "DateSynonymInUseRequest[dateSynonym=" + dateSynonym + ']';
	}
}

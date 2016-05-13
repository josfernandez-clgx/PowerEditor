package com.mindbox.pe.communication;

import java.util.Date;


/**
 * Request to check if the new date for a date synonym is valid.
 * This is for TT-75.
 * 
 * @author Geneho Kim
 * @since PowerEditor 5.9.1
 */
public class ValidateDateSynonymDateChangeRequest extends SessionRequest<ValidateDateSynonymDateChangeResponse> {

	private static final long serialVersionUID = 2005092340000002L;

	private final int dateSynonymId;
	private final Date newDate;

	public ValidateDateSynonymDateChangeRequest(String userID, String sessionID, int dateSynonymId, Date newDate) {
		super(userID, sessionID);
		this.dateSynonymId = dateSynonymId;
		this.newDate = newDate;
	}

	public int getDateSynonymId() {
		return dateSynonymId;
	}

	public Date getNewDate() {
		return newDate;
	}
}

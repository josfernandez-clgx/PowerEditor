package com.mindbox.pe.communication;

import java.util.Arrays;

import com.mindbox.pe.model.DateSynonym;

/**
 * @author MindBox, Inc
 * @since PowerEditor 4.4.2
 */
public class ReplaceDateSynonymsRequest extends SessionRequest<AbstractSimpleResponse> {

	private static final long serialVersionUID = 2003061611404000L;

	private final DateSynonym[] toBeReplaced;
	private final DateSynonym replacement;
	
	public ReplaceDateSynonymsRequest(String userID, String sessionID, DateSynonym[] toBeReplaced, DateSynonym replacement) {
		super(userID, sessionID);
		this.toBeReplaced = toBeReplaced;
		this.replacement = replacement;
	}

	public DateSynonym getReplacement() {
		return replacement;
	}

	public DateSynonym[] getToBeReplaced() {
		return toBeReplaced;
	}
	
	public String toString() {
		return "ReplaceDateSynonymRequest: replacing " + Arrays.asList(toBeReplaced) + " with " + replacement;
	}
}

package com.mindbox.pe.communication;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class FetchDomainDefinitionXMLRequest extends SessionRequest<ByteArrayResponse> {

	private static final long serialVersionUID = -8135012497668410521L;

	/**
	 * @param userID userID
	 * @param sessionID sessionID
	 */
	public FetchDomainDefinitionXMLRequest(String userID, String sessionID) {
		super(userID, sessionID);
	}
}

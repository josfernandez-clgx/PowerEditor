package com.mindbox.pe.communication;


/**
 * To retrieve custom report details.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.4.0
 */
public class FetchCustomReportsRequest extends SessionRequest<ListResponse<String>> {

	private static final long serialVersionUID = 20060126800000L;


	public FetchCustomReportsRequest(String userID, String sessionID) {
		super(userID, sessionID);
	}

	public String toString() {
		return "FetchCustomReportsRequest[" + "]";
	}

}

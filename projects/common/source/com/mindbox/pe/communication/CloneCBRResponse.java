package com.mindbox.pe.communication;

/**
 * Clone CBR Response
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 *
 */
public class CloneCBRResponse extends ResponseComm {
	
	private static final long serialVersionUID = 2004110219296013L;
	
	public CloneCBRResponse(int i) {
		persistentId = i;
	}

	public int getPersistentID() {
		return persistentId;
	}

	private final int persistentId;
}

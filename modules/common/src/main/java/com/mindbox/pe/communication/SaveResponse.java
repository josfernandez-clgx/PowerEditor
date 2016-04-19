package com.mindbox.pe.communication;


// Referenced classes of package com.mindbox.communication:
//            ResponseComm

public class SaveResponse extends ResponseComm {
	
	private static final long serialVersionUID = 2003052312006013L;
	
	public SaveResponse(int i) {
		persistentId = i;
	}

	public int getPersistentID() {
		return persistentId;
	}

	private final int persistentId;
}

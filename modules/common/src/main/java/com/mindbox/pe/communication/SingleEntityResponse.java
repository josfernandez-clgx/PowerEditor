/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.communication;

import com.mindbox.pe.model.Persistent;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class SingleEntityResponse extends ResponseComm {

	private static final long serialVersionUID = 20030616120020000L;

	private final Persistent object;

	public SingleEntityResponse(Persistent object) {
		super();
		this.object = object;
	}

	public Persistent getPersistent() {
		return object;
	}
}

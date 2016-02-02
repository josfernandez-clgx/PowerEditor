package com.mindbox.pe.client;

import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;

/**
 * An extension of {@link com.mindbox.pe.client.applet.PowerEditorLoggedApplet} that does not communicate with server.
 * This is for unit testing.
 * @author Geneho Kim
 * @see com.mindbox.pe.client.AbstractClientTestBase
 */
public class PELoggedAppletWithNoComm extends PowerEditorLoggedApplet {

	public void init() {
		// do nothing
	}
	
	public void start() {
		// do nothing
	}
	
	public void stop() {
		super.stop();
	}
	
	public void destroy() {
		super.destroy();
	}
}

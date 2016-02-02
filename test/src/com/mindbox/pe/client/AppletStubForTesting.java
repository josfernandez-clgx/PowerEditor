package com.mindbox.pe.client;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A concrete implementation of {@link java.applet.AppletStub} for unit testing.
 * 
 * @author Geneho Kim
 * @see com.mindbox.pe.client.PELoggerAppletImplForTest
 * @see com.mindbox.pe.client.AbstractClientTestBase
 */
public class AppletStubForTesting implements AppletStub {

	private static class AppletContextImpl implements AppletContext {

		public AudioClip getAudioClip(URL arg0) {
			return null;
		}

		public Image getImage(URL arg0) {
			return null;
		}

		public Applet getApplet(String arg0) {
			return null;
		}

		public Enumeration<Applet> getApplets() {
			return null;
		}

		public void showDocument(URL arg0) {
		}

		public void showDocument(URL arg0, String arg1) {
		}

		public void showStatus(String arg0) {
		}

		public void setStream(String arg0, InputStream arg1) throws IOException {
		}

		public InputStream getStream(String arg0) {
			return null;
		}

		public Iterator<String> getStreamKeys() {
			return null;
		}

	}

	private final Map<String,String> paramMap = Collections.synchronizedMap(new HashMap<String,String>());
	private final AppletContextImpl appletContextImpl = new AppletContextImpl();

	public boolean isActive() {
		return true;
	}

	public URL getDocumentBase() {
		try {
			return new URL("file://src/doc");
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public URL getCodeBase() {
		try {
			return new URL("file://src/doc");
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getParameter(String arg0) {
		return paramMap.get(arg0);
	}

	public void setParameter(String key, String value) {
		paramMap.put(key, value);
	}

	public AppletContext getAppletContext() {
		return appletContextImpl;
	}

	public void appletResize(int arg0, int arg1) {
	}

}

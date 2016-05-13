/*
 * Created on May 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client;


import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;
/**
 * Runs PowerEditor in stand-alone mode.
 * 
 * @since PowerEditor 1.0
 */
public class PowerEditorRunner {

	private static class AppletStubImpl implements AppletStub {
		private String serverURL = null;
		private URL rootURL = null;
		private String sessionID = null;
		
		public AppletStubImpl(String rootDir, String serverURL) throws MalformedURLException {
			this.serverURL = serverURL;
			rootURL = new URL("file:///" + rootDir);
		}
		
		public boolean isActive() {
			return true;
		}

		public void appletResize(int arg0, int arg1) {
		}

		public AppletContext getAppletContext() {
			return null;
		}

		public URL getCodeBase() {
			return rootURL;
		}

		public URL getDocumentBase() {
			return rootURL;
		}

		public String getParameter(String arg0) {
			if (arg0 == null) return null;
			if (arg0.equals("server")) {
				return serverURL;
			}
			else if (arg0.equals("ssid")) {
				return sessionID;
			}
			return arg0;
		}
		
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("java com.mindbox.pe.client.PowerEditorRunner <rootDir> <serverURL>");
		System.out.println();
	}
	
	public static void main(String[] args) throws MalformedURLException {
		System.out.println("Running PowerEditor Applet Runner:");
		for (int i = 0; i < args.length; i++) {
			System.out.println("  arg["+(i+1)+"] = " + args[i]);
		}
		
		if (args.length < 2) {
			printUsage();
			System.exit(0);
		}
		
		PowerEditorLoggedApplet applet = new PowerEditorLoggedApplet();

		JFrame frame = new JFrame("PowerEditor Runner");
		frame.getContentPane().setLayout(new GridLayout(1,1));
		frame.getContentPane().add(applet);
		frame.setSize(800,600);		
		
		applet.setStub(new AppletStubImpl(args[0], args[1]));
		applet.init();
		
		frame.setVisible(true);

		applet.start();
	}
}

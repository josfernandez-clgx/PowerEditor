package com.mindbox.pe.client;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;

import com.mindbox.pe.client.applet.PearApplet;

public class PearRunner {

	private static class AppletStubImpl implements AppletStub {

		private Properties parameters = new Properties();
		private String rootServer = null;
		private URL rootURL = null;

		public AppletStubImpl(String server) throws MalformedURLException {
			this.rootServer = server;
			this.rootURL = new URL(server);
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

		public String getParameter(String name) {
			if (null == name)
				return null;
			return parameters.getProperty(name);
		}

		public Properties getParameters() {
			return parameters;
		}

		public void setParameter(String name, String value) {
			parameters.setProperty(name, value);
		}

	}

	static final String BORDER = "*******";

	public static void main(String[] args) throws Exception {
		System.out.println("Running Pear:");
		if (args.length != 4) {
			System.out.println("Usage:");
			System.out.println("java com.mindbox.pe.client.PearRunner [serverURL] [loginURL] [username] [password]");
			System.out.println();
			System.exit(0);
		}

		System.out.println("args[0] serverURL: " + args[0]);
		System.out.println("args[1]  loginURL: " + args[1]);
		System.out.println("args[2]  username: " + args[2]);
		System.out.println("args[4]  password: " + args[3]);

		URL loginURL = new URL(args[1]);
		HttpURLConnection connection = (HttpURLConnection) loginURL.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", "PEAR");
		connection.setDoOutput(true);

		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes("userID=");
		output.writeBytes(args[2]);
		output.writeBytes("&password=");
		output.writeBytes(args[3]);
		output.flush();
		output.close();

		AppletStubImpl stub = new AppletStubImpl(args[0]);

		System.out.println(BORDER);
		System.out.println("Headers:");
		System.out.println(BORDER);

		Map<String, List<String>> headers = connection.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			System.out.format("Header name: %s%n", key);
			List<String> values = entry.getValue();
			for (String value : values) {
				System.out.format("	 value: %s%n", value);
			}
			if ((null != key) && key.equals("Set-Cookie")) {
				for (String value : values) {
					String[] cookie = value.split("=", 2);
					if ((null != cookie) && (2 == cookie.length)) {
						stub.setParameter(cookie[0], cookie[1]);
					}
				}
			}
		}

		System.out.println(BORDER);
		System.out.println("Cookies");
		System.out.println(BORDER);

		Properties parameters = stub.getParameters();
		Set<String> keys = parameters.stringPropertyNames();
		for (String key : keys) {
			String value = parameters.getProperty(key);
			System.out.format("Name=\"%s\", value=\"%s\"%n", key, value);
		}

		System.out.println(BORDER);
		System.out.println("Body");
		System.out.println(BORDER);

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = null;
		while (null != (line = reader.readLine()))
			System.out.println(line);

		reader.close();

		PearApplet applet = new PearApplet();

		JFrame frame = new JFrame("Pear Runner");
		frame.getContentPane().setLayout(new GridLayout(1,1));
		frame.getContentPane().add(applet);
		frame.setSize(800,600);

		applet.setStub(stub);
		applet.init();

		frame.setVisible(true);
		applet.start();
	}
}

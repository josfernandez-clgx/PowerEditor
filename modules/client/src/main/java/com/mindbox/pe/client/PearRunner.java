package com.mindbox.pe.client;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.DataOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

	private static String removeTrailingSlashes(String input) {
		StringBuilder output = new StringBuilder(input);
		for (int i = output.length() - 1; i >= 0; i--) {
			if ('\\' != output.charAt(i)) {
				break;
			}
			output.deleteCharAt(i);
		}
		return output.toString();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Arguments expected: PowerEditorURL username password");
			System.exit(0);
		}

		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);

		final String peURL = removeTrailingSlashes(args[0]);
		final String userid = args[1];
		final String password = args[2];

		URL loginURL = new URL(peURL + "/login_pear.jsp");
		HttpURLConnection connection = (HttpURLConnection) loginURL.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", "PEAR");
		connection.setDoOutput(true);

		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes("userID=");
		output.writeBytes(userid);
		output.writeBytes("&password=");
		output.writeBytes(password);
		output.flush();
		output.close();

		int response = connection.getResponseCode();
		if (HttpURLConnection.HTTP_OK != response) {
			System.err.format("HTTP response code %s%n", response);
			System.exit(-1);
		}

		connection.getContent();

		AppletStubImpl stub = new AppletStubImpl(peURL);

		CookieStore cookieStore = cookieManager.getCookieStore();
		List<HttpCookie> cookieList = cookieStore.getCookies();
		for (HttpCookie cookie : cookieList) {
			stub.setParameter(cookie.getName(), cookie.getValue());
		}

		PearApplet applet = new PearApplet();

		GraphicsEnvironment graphics_environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphics_devices = graphics_environment.getScreenDevices();
		GraphicsConfiguration graphics_configuration = graphics_devices[0].getDefaultConfiguration();
		Rectangle graphics_rectangle = graphics_configuration.getBounds();

		JFrame frame = new JFrame("Pear Runner");
		frame.setSize(graphics_rectangle.width - 6, graphics_rectangle.height - 58);
		frame.getContentPane().setLayout(new GridLayout(1, 1));
		frame.getContentPane().add(applet);

		applet.setStub(stub);
		applet.init();

		frame.setVisible(true);
		applet.start();
	}
}

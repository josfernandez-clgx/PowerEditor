package com.mindbox.ftest.pe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.mindbox.pe.TestConfig;

public class ESPTestUtil {

	private static final byte[] END_OF_TEXT = String.valueOf('\u0003').getBytes();
	private static final String SHUTDOWN_REQUEST = "ESPControlRequest~demo|shutdown~esp";
	private static final String PING_REQUEST = "ESPControlRequest~demo|ping";

	private TestConfig testConfig;
	private InetAddress aeHost;
	private int aePort;
	private int timeout = 15;

	public ESPTestUtil(TestConfig testConfig) throws Exception {
		this.testConfig = testConfig;
		setUp();
	}

	protected void setUp() throws Exception {
		aeHost = InetAddress.getByName(testConfig.getRequiredStringProperty("mindbox.test.ae.host"));
		aePort = Integer.parseInt(testConfig.getRequiredStringProperty("mindbox.test.ae.port"));
	}

	public final String sendRequest(File requestFile) throws Exception {
		return sendRequest(new FileReader(requestFile));
	}

	public final String sendRequest(String requestContent) throws Exception {
		return sendRequest(new StringReader(requestContent));
	}

	protected final String sendRequest(Reader input) throws Exception {
		Socket aeSocket = null;
		try {
			aeSocket = new Socket(aeHost, aePort);
			writeRequest(input, aeSocket);
			BufferedReader reader = getResponse(aeSocket);
			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				out.println(line);
			}
			out.flush();
			out.close();
			out = null;
			return sw.toString().trim();
		}
		finally {
			if (aeSocket != null) {
				aeSocket.close();
				aeSocket = null;
			}
		}
	}

	private BufferedReader getResponse(Socket source) throws IOException {
		source.setSoTimeout(1000 * timeout); // block a max of 10 seconds
		return new BufferedReader(new InputStreamReader(source.getInputStream()));
	}

	private void writeRequest(Reader reader, Socket target) throws Exception {
		BufferedReader in = new BufferedReader(reader);
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			target.getOutputStream().write(line.getBytes());
		}
		in.close();
		in = null;
		target.getOutputStream().write(END_OF_TEXT);
		target.getOutputStream().flush();
	}

	public final void shutDownESP() throws Throwable {
		sendRequest(SHUTDOWN_REQUEST);
	}

	public final boolean verifyESPIsRunning(long timeout) throws Exception {
		long startTime = System.currentTimeMillis();
		boolean isESPRunning = false;
		while (!isESPRunning && (System.currentTimeMillis() - startTime) < timeout) {
			try {
				Thread.sleep(500L);
				isESPRunning = isESPRunning();
			}
			catch (InterruptedException e) {
			}
		}
		return isESPRunning;
	}
	
	public final boolean isESPRunning() throws Exception {
		try {
			String response = sendRequest(PING_REQUEST);
			return response != null && response.startsWith("ESP");
		}
		catch (ConnectException ex) {
			Logger.getLogger(getClass()).info("ConnectException while trying to connect to ESP. It's probably down: " + ex.getMessage());
			return false;
		}
		catch (IOException ex) {
			Logger.getLogger(getClass()).info("IO error while trying to connect to ESP. ESP is probably down!", ex);
			return false;
		}
	}
}

package com.mindbox.pe.server.webservices;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;

public class WebServicesTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("ExportServiceTest Tests");
		suite.addTestSuite(WebServicesTest.class);
		return suite;
	}

	public WebServicesTest(String name) {
		super(name);
	}

	public void testPublish() throws Exception {
//		PowerEditorAPIInterface server = new PowerEditorAPIInterface();
//		server.importData("<?xml version='1.0'?>", false);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

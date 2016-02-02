package com.mindbox.pe.server.audit;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.server.spi.audit.AuditEvent;

public class DefaultAuditStorageTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("DefaultAuditStorageTest Tests");
		suite.addTestSuite(DefaultAuditStorageTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private DefaultAuditStorage defaultAuditStorage;

	public DefaultAuditStorageTest(String name) {
		super(name);
	}

	public void testLogAuditEventWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(defaultAuditStorage, "log", new Class[] { AuditEvent.class });
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.defaultAuditStorage = new DefaultAuditStorage();
	}

}

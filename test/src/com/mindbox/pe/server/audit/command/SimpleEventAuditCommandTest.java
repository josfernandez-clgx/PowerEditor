package com.mindbox.pe.server.audit.command;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.spi.audit.AuditEvent;

public class SimpleEventAuditCommandTest extends AbstractAuditCommandTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("SimpleEventAuditCommandTest Tests");
		suite.addTestSuite(SimpleEventAuditCommandTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public SimpleEventAuditCommandTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		useTestAuditStorage();
	}

	public void testExecuteHappyCase() throws Exception {
		AuditEvent auditEvent = ObjectMother.createAuditDataBuilderForKBMod();
		SimpleEventAuditCommand simpleEventAuditCommand = new SimpleEventAuditCommand(
				auditEvent.getAuditType(),
				auditEvent.getDate(),
				auditEvent.getUserName());

		simpleEventAuditCommand.execute(testAuditStorage);

		assertNotNull(testAuditStorage.getAuditEvent());
		assertNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(auditEvent.getAuditType(), testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(auditEvent.getDate(), testAuditStorage.getAuditEvent().getDate());
		assertEquals(auditEvent.getUserName(), testAuditStorage.getAuditEvent().getUserName());
	}

}

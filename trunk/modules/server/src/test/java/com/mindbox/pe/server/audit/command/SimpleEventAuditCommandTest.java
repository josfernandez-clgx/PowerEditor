package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.server.ServerTestObjectMother.createAuditDataBuilderForKBMod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mindbox.pe.server.spi.audit.AuditEvent;

public class SimpleEventAuditCommandTest extends AbstractAuditCommandTestBase {

	public void setUp() throws Exception {
		super.setUp();
		useTestAuditStorage();
	}

	@Test
	public void testExecuteHappyCase() throws Exception {
		AuditEvent auditEvent = createAuditDataBuilderForKBMod();
		SimpleEventAuditCommand simpleEventAuditCommand = new SimpleEventAuditCommand(auditEvent.getAuditType(), auditEvent.getDate(), auditEvent.getUserName(), "");

		simpleEventAuditCommand.execute(testAuditStorage);

		assertNotNull(testAuditStorage.getAuditEvent());
		assertNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(auditEvent.getAuditType(), testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(auditEvent.getDate(), testAuditStorage.getAuditEvent().getDate());
		assertEquals(auditEvent.getUserName(), testAuditStorage.getAuditEvent().getUserName());
	}

}

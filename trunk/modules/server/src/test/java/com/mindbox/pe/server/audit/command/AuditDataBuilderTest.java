package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AuditDataBuilderTest extends AbstractTestBase {

	@Test
	public void testAsAuditEvent() throws Exception {
		int i1 = createInt();
		String user = createString();
		Date date = new Date();
		AuditEvent auditEvent = AuditDataBuilder.asAuditEvent(i1, AuditEventType.KB_MOD, date, user, "");
		assertNotNull(auditEvent);
		assertEquals(i1, auditEvent.getAuditID());
		assertEquals(AuditEventType.KB_MOD, auditEvent.getAuditType());
		assertEquals(user, auditEvent.getUserName());
		assertEquals(date, auditEvent.getDate());
	}

	@Test
	public void testSetAuditEventLogHappyCase() throws Exception {
		int i1 = createInt();
		String user = null;
		Date date = new Date();

		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(i1, AuditEventType.KB_MOD, date, user, "");
		auditDataBuilder.freeze();
		AuditEvent auditEvent = auditDataBuilder.getAuditEvent();
		assertNotNull(auditEvent);
		assertEquals(i1, auditEvent.getAuditID());
		assertEquals(AuditEventType.KB_MOD, auditEvent.getAuditType());
		assertEquals(user, auditEvent.getUserName());
		assertEquals(date, auditEvent.getDate());
	}

	@Test
	public void testSetAuditMasterLogHappyCase() throws Exception {
		int i1 = createInt();
		int i2 = createInt();
		int i3 = createInt();
		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(createInt(), AuditEventType.KB_MOD, new Date(), null, "");
		auditDataBuilder.insertAuditMasterLog(i1, i2, i3);
		auditDataBuilder.freeze();
		AuditKBMaster auditMaster = auditDataBuilder.getAuditEvent().getKBMaster(0);
		assertNotNull(auditMaster);
		assertEquals(i1, auditMaster.getKbAuditID());
		assertEquals(i2, auditMaster.getKbChangedTypeID());
		assertEquals(i3, auditMaster.getElementID());
	}

}

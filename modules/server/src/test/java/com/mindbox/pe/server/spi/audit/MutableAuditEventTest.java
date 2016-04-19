package com.mindbox.pe.server.spi.audit;

import static com.mindbox.pe.server.ServerTestObjectMother.createAuditDataBuilderForKBMod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class MutableAuditEventTest extends AbstractTestBase {

	@Test
	public void testEqualsUsesAuditID() throws Exception {
		MutableAuditEvent auditEvent1 = createAuditDataBuilderForKBMod();
		MutableAuditEvent auditEvent2 = createAuditDataBuilderForKBMod();
		assertFalse(auditEvent1.equals(auditEvent2));
		assertFalse(auditEvent2.equals(auditEvent1));

		auditEvent2.setAuditID(auditEvent1.getAuditID());
		assertTrue(auditEvent1.equals(auditEvent2));
		assertTrue(auditEvent2.equals(auditEvent1));

		auditEvent2.setAuditID(auditEvent1.getAuditID() + 1);
		auditEvent2.setDate(auditEvent1.getDate());
		auditEvent2.setUserName(auditEvent1.getUserName());
		assertFalse(auditEvent1.equals(auditEvent2));
		assertFalse(auditEvent2.equals(auditEvent1));
	}

	@Test
	public void testHashCodeReturnsAuditID() throws Exception {
		MutableAuditEvent auditEvent = createAuditDataBuilderForKBMod();
		assertEquals(auditEvent.getAuditID(), auditEvent.hashCode());
	}
}

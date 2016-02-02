package com.mindbox.pe.server.spi.audit;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;

public class MutableAuditEventTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("MutableAuditEventTest Tests");
		suite.addTestSuite(MutableAuditEventTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public MutableAuditEventTest(String name) {
		super(name);
	}

	public void testHashCodeReturnsAuditID() throws Exception {
		MutableAuditEvent auditEvent = ObjectMother.createAuditDataBuilderForKBMod();
		assertEquals(auditEvent.getAuditID(), auditEvent.hashCode());
	}

	public void testEqualsUsesAuditID() throws Exception {
		MutableAuditEvent auditEvent1 = ObjectMother.createAuditDataBuilderForKBMod();
		MutableAuditEvent auditEvent2 = ObjectMother.createAuditDataBuilderForKBMod();
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
}

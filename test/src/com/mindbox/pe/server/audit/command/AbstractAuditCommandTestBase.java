package com.mindbox.pe.server.audit.command;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;

public abstract class AbstractAuditCommandTestBase extends AbstractTestWithTestConfig {

	protected static class TestAuditStorage implements AuditStorage {

		private AuditEvent auditEvent;

		private TestAuditStorage() {
		}

		public AuditEvent getAuditEvent() {
			return auditEvent;
		}

		public AuditKBMaster getFirstAuditMaster() {
			return (auditEvent.kbMasterCount() > 0 ? auditEvent.getKBMaster(0) : null);
		}

		public int getNextAuditID() throws AuditException {
			return ObjectMother.createInt();
		}

		public void log(AuditEvent auditEvent) throws ServiceException {
			this.auditEvent = auditEvent;
		}

	}

	protected MockControl auditStorageMockControl;
	protected AuditStorage mockAuditStorage;
	protected TestAuditStorage testAuditStorage;

	protected AbstractAuditCommandTestBase(String name) {
		super(name);
	}

	protected final void useAuditStorageMock() {
		auditStorageMockControl = MockControl.createControl(AuditStorage.class);
		mockAuditStorage = (AuditStorage) auditStorageMockControl.getMock();
	}

	protected final void useTestAuditStorage() {
		testAuditStorage = new TestAuditStorage();
	}

	protected final void replayAll() {
		auditStorageMockControl.replay();
	}

	protected final void verifyAll() {
		auditStorageMockControl.verify();
	}
}

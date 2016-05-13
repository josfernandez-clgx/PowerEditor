package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.mindbox.pe.server.AbstractTestWithTestConfig;
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
			return createInt();
		}

		public void log(AuditEvent auditEvent) throws ServiceException {
			this.auditEvent = auditEvent;
		}

	}

	protected AuditStorage mockAuditStorage;
	protected TestAuditStorage testAuditStorage;

	protected final void replayAll() {
		replay(mockAuditStorage);
	}

	protected final void useAuditStorageMock() {
		mockAuditStorage = createMock(AuditStorage.class);
	}

	protected final void useTestAuditStorage() {
		testAuditStorage = new TestAuditStorage();
	}

	protected final void verifyAll() {
		verify(mockAuditStorage);
	}
}

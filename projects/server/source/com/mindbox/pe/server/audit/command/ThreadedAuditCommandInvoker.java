package com.mindbox.pe.server.audit.command;

import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.audit.event.AuditFailedEventSupport;

public final class ThreadedAuditCommandInvoker implements AuditCommandInvoker {

	private boolean suppressAll;

	public ThreadedAuditCommandInvoker(boolean suppressAll) {
		this.suppressAll = suppressAll;
	}

	public void execute(final AuditCommand auditCommand, final AuditStorage auditStorage, final AuditFailedEventSupport eventSupport) {
		if (suppressAll) return;
		new Thread() {
			public void run() {
				try {
					auditCommand.execute(auditStorage);
				}
				catch (Exception ex) {
					eventSupport.fireAuditFailed(AuditConstants.FAILURE_MESSAGE_PREFIX + auditCommand.getDescription(), ex);
				}
			}
		}.run();
	}
}

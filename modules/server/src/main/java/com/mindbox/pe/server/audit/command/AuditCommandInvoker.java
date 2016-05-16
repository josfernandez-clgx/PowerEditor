package com.mindbox.pe.server.audit.command;

import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.audit.event.AuditFailedEventSupport;

/**
 * Invoker of {@link AuditCommand}.
 * @author kim
 *
 */
public interface AuditCommandInvoker {

	/**
	 * Executes the specified audit command with the specified event support.
	 * 
	 * This should NOT throw any exception. That is, 
	 * an implementation of this should catch all exceptions and call the
	 * {@link AuditFailedEventSupport#fireAuditFailed(String, Exception)} method on
	 * <code>eventSupport</code>.
	 * 
	 * @param auditCommand auditCommand
	 * @param auditStorage auditStorage
	 * @param eventSupport eventSupport
	 */
	void execute(AuditCommand auditCommand, AuditStorage auditStorage, AuditFailedEventSupport eventSupport);
}

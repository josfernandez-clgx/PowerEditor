package com.mindbox.pe.server.audit.command;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

/**
 * Encapsulates logging of an audit event.
 * 
 * @author kim
 * @see AuditCommandInvoker
 */
public interface AuditCommand {

	/**
	 * Executes the command.
	 * This may throw undeclared runtime exceptions.
	 * @param auditStorage the audit storage
	 * @throws AuditException
	 * @throws ServiceException
	 */
	void execute(AuditStorage auditStorage) throws AuditException, ServiceException;
	
	/**
	 * Gets description of this.
	 * This will be used to generate failure message.
	 * The final failure message shall be 
	 * {@link AuditConstants#FAILURE_MESSAGE_PREFIX} appended with 
	 * the returned value of this method. 
	 * @return description
	 */
	String getDescription();
}

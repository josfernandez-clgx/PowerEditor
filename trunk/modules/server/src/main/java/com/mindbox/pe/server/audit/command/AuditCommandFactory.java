package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.Auditable;

public interface AuditCommandFactory {

	AuditCommand getImportInstance(Auditable auditable, Date date, String userID);

	AuditCommand getNewInstance(Auditable auditable, Date date, String userID);

	AuditCommand getUpdateInstance(Auditable newAuditable, Auditable oldAuditable, Date date, String userID);

	AuditCommand getDeleteInstance(Auditable auditable, Date date, String userID);
}

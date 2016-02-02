package com.mindbox.pe.server.audit.command;

import java.util.Date;

import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;

public final class NewActivationAuditCommand extends AbstractKBModAuditCommand {

	private AbstractGrid <?>grid;

	public NewActivationAuditCommand(AbstractGrid<?> grid, Date date, String userID) {
		super(date, userID);
		this.grid = grid;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(auditStorage.getNextAuditID(), AuditConstants.KB_MOD_TYPE_GENERIC_ADD, getDescription());
	}

	public String getDescription() {
		return "New " + grid.getAuditDescription();
	}

	protected int getKBChangedElementID() {
		return grid.getID();
	}

	protected int getKBChangedElementTypeID() {
		return (grid.isParameterGrid()
				? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION
				: AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION);
	}

}

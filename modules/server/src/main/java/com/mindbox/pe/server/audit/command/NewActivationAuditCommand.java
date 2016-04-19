package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_ADD;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class NewActivationAuditCommand extends AbstractKBModAuditCommand {

	private AbstractGrid<?> grid;

	public NewActivationAuditCommand(AbstractGrid<?> grid, Date date, String userID) {
		super(date, userID);
		this.grid = grid;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		super.buildAuditData(auditStorage, auditDataBuilder);
		final ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_ADD));
		changeDetail.setChangeDescription(getDescription());
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	public String getDescription() {
		return "New " + grid.getAuditDescription();
	}

	protected int getKBChangedElementID() {
		return grid.getID();
	}

	protected int getKBChangedElementTypeID() {
		return (grid.isParameterGrid() ? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION : AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION);
	}

}

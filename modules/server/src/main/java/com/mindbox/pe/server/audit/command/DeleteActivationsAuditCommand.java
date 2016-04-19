package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;
import static com.mindbox.pe.server.audit.AuditUtil.generateContextStringForAudit;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public class DeleteActivationsAuditCommand extends AbstractAuditCommandWithUser {

	protected final List<? extends AbstractGrid<?>> removedGridList;

	public DeleteActivationsAuditCommand(List<? extends AbstractGrid<?>> removedGridList, Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID, "");
		this.removedGridList = Collections.unmodifiableList(removedGridList);
	}

	private void addAuditKBMaster(AbstractGrid<?> grid, AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		int kbAuditMasterID = auditStorage.getNextAuditID();
		auditDataBuilder.insertAuditMasterLog(kbAuditMasterID, (grid.isParameterGrid()
				? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION
				: AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION), grid.getID());
		ChangeDetail changeDetail = new ChangeDetail();
		changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_GENERIC_REMOVE));
		changeDetail.setChangeDescription(String.format("Removed %s", grid.getAuditDescription()));
		changeDetail.setTemplate(String.valueOf(grid.getTemplateID()));
		changeDetail.setEffectiveDate(String.valueOf((grid.getEffectiveDate() == null ? -1 : grid.getEffectiveDate().getID())));
		changeDetail.setExpirationDate(String.valueOf((grid.getExpirationDate() == null ? -1 : grid.getExpirationDate().getID())));
		changeDetail.setStatus(grid.getStatus());
		changeDetail.setContextElement(generateContextStringForAudit(grid.extractGuidelineContext()));
		auditDataBuilder.addToLastAuditKBMaster(changeDetail);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		for (Iterator<? extends AbstractGrid<?>> iter = removedGridList.iterator(); iter.hasNext();) {
			AbstractGrid<?> grid = iter.next();
			addAuditKBMaster(grid, auditStorage, auditDataBuilder);
		}
	}

	public String getDescription() {
		return "Removed " + removedGridList.size() + "grids; " + super.getDescription();
	}
}

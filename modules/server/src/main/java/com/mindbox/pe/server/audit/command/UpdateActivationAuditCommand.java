package com.mindbox.pe.server.audit.command;

import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_MODIFY_COMMENTS;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_MODIFY_EXPIRATION_DATE;
import static com.mindbox.pe.common.AuditConstants.KB_MOD_TYPE_MODIFY_STATUS;
import static com.mindbox.pe.common.AuditConstants.getModTypeDescription;

import java.util.Date;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.diff.GridCellValueChangeDetailSet;
import com.mindbox.pe.common.diff.GridDiffEngine;
import com.mindbox.pe.common.diff.GridDiffResult;
import com.mindbox.pe.common.diff.SimpleGridDiffEngine;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.xsd.audit.ChangeDetail;

public final class UpdateActivationAuditCommand<G extends AbstractGrid<?>> extends AbstractAuditCommandWithUser {

	private static final GridDiffEngine GRID_DIFF_ENGINE = new SimpleGridDiffEngine();

	private G oldGrid;
	private G newGrid;

	public UpdateActivationAuditCommand(G newGrid, G oldGrid, Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID, "");
		this.oldGrid = oldGrid;
		this.newGrid = newGrid;
	}

	private void addAuditKBMasterForGridChanges(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		try {
			final GridDiffResult gridDiffResult = GRID_DIFF_ENGINE.diff(oldGrid, newGrid);

			final GridCellValueChangeDetailSet gridCellValueChangeDetailSet = gridDiffResult.getGridCellValueChangeDetailSet();
			if (gridCellValueChangeDetailSet != null) {
				for (final ChangeDetail changeDetail : gridCellValueChangeDetailSet.getDetailList()) {
					auditDataBuilder.addToLastAuditKBMaster(changeDetail);
				}
			}
		}
		catch (Exception ex) {
			logger.error("Failed to determine differences for " + getDescription(), ex);
			throw new AuditException("Failed to determine differences between " + oldGrid.getAuditDescription() + " and " + newGrid.getAuditDescription(), ex);
		}
	}

	private void addAuditKBMasterForPropertyChanges(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		// check activation date
		if (!UtilBase.isSame(oldGrid.getEffectiveDate(), newGrid.getEffectiveDate())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: eff date changed");
			ChangeDetail changeDetail = new ChangeDetail();
			changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE));
			changeDetail.setChangeDescription(String.format("Changed effective date of %s", newGrid.getAuditDescription()));
			changeDetail.setPreviousValue((oldGrid.getEffectiveDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : oldGrid.getEffectiveDate().getName()));
			changeDetail.setPreviousNativeValue((oldGrid.getEffectiveDate() == null ? null : String.valueOf(oldGrid.getEffectiveDate().getId())));
			changeDetail.setNewValue((newGrid.getEffectiveDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : newGrid.getEffectiveDate().getName()));
			changeDetail.setNewNativeValue((newGrid.getEffectiveDate() == null ? null : String.valueOf(newGrid.getEffectiveDate().getId())));
			auditDataBuilder.addToLastAuditKBMaster(changeDetail);
		}
		// check expiration date
		if (!UtilBase.isSame(oldGrid.getExpirationDate(), newGrid.getExpirationDate())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: exp date changed");
			ChangeDetail changeDetail = new ChangeDetail();
			changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_MODIFY_EXPIRATION_DATE));
			changeDetail.setChangeDescription(String.format("Changed expiration date of %s", newGrid.getAuditDescription()));
			changeDetail.setPreviousValue((oldGrid.getExpirationDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : oldGrid.getExpirationDate().getName()));
			changeDetail.setPreviousNativeValue((oldGrid.getExpirationDate() == null ? null : String.valueOf(oldGrid.getExpirationDate().getId())));
			changeDetail.setNewValue((newGrid.getExpirationDate() == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : newGrid.getExpirationDate().getName()));
			changeDetail.setNewNativeValue((newGrid.getExpirationDate() == null ? null : String.valueOf(newGrid.getExpirationDate().getId())));
			auditDataBuilder.addToLastAuditKBMaster(changeDetail);
		}
		// check status
		if (!UtilBase.isSame(newGrid.getStatus(), oldGrid.getStatus())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: status changed");
			ChangeDetail changeDetail = new ChangeDetail();
			changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_MODIFY_STATUS));
			changeDetail.setChangeDescription(String.format("Changed status of %s", newGrid.getAuditDescription()));
			changeDetail.setPreviousValue(oldGrid.getStatus());
			changeDetail.setPreviousNativeValue(oldGrid.getStatus());
			changeDetail.setNewValue(newGrid.getStatus());
			changeDetail.setNewNativeValue(newGrid.getStatus());
			auditDataBuilder.addToLastAuditKBMaster(changeDetail);
		}
		// check comments
		if (!UtilBase.isSame(newGrid.getComments(), oldGrid.getComments())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: comments changed");
			ChangeDetail changeDetail = new ChangeDetail();
			changeDetail.setChangeType(getModTypeDescription(KB_MOD_TYPE_MODIFY_COMMENTS));
			changeDetail.setChangeDescription(String.format("Changed comments of %s", newGrid.getAuditDescription()));
			changeDetail.setPreviousValue(oldGrid.getComments());
			changeDetail.setPreviousNativeValue(oldGrid.getComments());
			changeDetail.setNewValue(newGrid.getComments());
			changeDetail.setNewNativeValue(newGrid.getComments());
			auditDataBuilder.addToLastAuditKBMaster(changeDetail);
		}
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		auditDataBuilder.insertAuditMasterLog(auditStorage.getNextAuditID(), getKBChangedElementTypeID(), getKBChangedElementID());
		addAuditKBMasterForPropertyChanges(auditStorage, auditDataBuilder);
		addAuditKBMasterForGridChanges(auditStorage, auditDataBuilder);
	}

	public String getDescription() {
		return "Update " + oldGrid.getAuditDescription() + " to " + newGrid.getAuditDescription();
	}

	protected int getKBChangedElementID() {
		return newGrid.getID();
	}

	protected int getKBChangedElementTypeID() {
		return (newGrid.isParameterGrid() ? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION : AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION);
	}

}

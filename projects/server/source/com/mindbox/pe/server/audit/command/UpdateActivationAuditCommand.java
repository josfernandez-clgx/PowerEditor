package com.mindbox.pe.server.audit.command;

import java.util.Date;
import java.util.Iterator;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.diff.GridCellValueChangeDetail;
import com.mindbox.pe.common.diff.GridDiffEngine;
import com.mindbox.pe.common.diff.GridDiffResult;
import com.mindbox.pe.common.diff.SimpleGridDiffEngine;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

public final class UpdateActivationAuditCommand<G extends AbstractGrid<?>> extends AbstractAuditCommandWithUser {

	private G oldGrid;
	private G newGrid;

	public UpdateActivationAuditCommand(G newGrid, G oldGrid, Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID);
		this.oldGrid = oldGrid;
		this.newGrid = newGrid;
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		auditDataBuilder.insertAuditMasterLog(auditStorage.getNextAuditID(), getKBChangedElementTypeID(), getKBChangedElementID());
		addAuditKBMasterForPropertyChanges(auditStorage, auditDataBuilder);
		addAuditKBMasterForGridChanges(auditStorage, auditDataBuilder);
	}

	private void addAuditKBMasterForGridChanges(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException {
		try {
			GridDiffEngine gridDiffEngine = new SimpleGridDiffEngine();
			GridDiffResult gridDiffResult = gridDiffEngine.diff(oldGrid, newGrid);
			// process new rows
			int[] rows = gridDiffResult.getInsertedRows();
			if (rows != null && rows.length > 0) {
				int kbAuditDetailID = auditStorage.getNextAuditID();
				auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
						kbAuditDetailID,
						AuditConstants.KB_MOD_TYPE_ADD_GRID_ROW,
						"Added " + (rows.length > 1 ? "rows " : "row ") + UtilBase.toString(rows) + " to " + newGrid.getAuditDescription());
				for (int i = 0; i < rows.length; i++) {
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER,
							String.valueOf(rows[i]));
				}
			}
			rows = null;

			// process deleted rows
			rows = gridDiffResult.getDeletedRows();
			if (rows != null && rows.length > 0) {
				int kbAuditDetailID = auditStorage.getNextAuditID();
				auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
						kbAuditDetailID,
						AuditConstants.KB_MOD_TYPE_REMOVE_GRID_ROW,
						"Removed " + (rows.length > 1 ? "rows " : "row ") + UtilBase.toString(rows) + " from "
								+ newGrid.getAuditDescription());
				for (int i = 0; i < rows.length; i++) {
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER,
							String.valueOf(rows[i]));
				}
			}

			// process modified cells
			if (!gridDiffResult.getGridCellValueChangeDetailSet().isEmpty()) {
				for (Iterator<GridCellValueChangeDetail> iter = gridDiffResult.getGridCellValueChangeDetailSet().getOrderedIterator(); iter.hasNext();) {
					GridCellValueChangeDetail changeDetail = iter.next();
					int kbAuditDetailID = auditStorage.getNextAuditID();
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
							kbAuditDetailID,
							AuditConstants.KB_MOD_TYPE_MODIFY_GRID_CELL,
							"Changed row " + changeDetail.getRow() + ", column " + changeDetail.getColumn() + " of "
									+ newGrid.getAuditDescription());
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER,
							String.valueOf(changeDetail.getRow()));
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_COLUMN_NAME,
							newGrid.getTemplate().getColumn(changeDetail.getColumn()).getTitle());
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE,
							changeDetail.getNewValue());
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE,
							changeDetail.getOldValue());
				}
			}
		}
		catch (Exception ex) {
			logger.error("Failed to determine differences for " + getDescription(), ex);
			throw new AuditException("Failed to determine differences between " + oldGrid.getAuditDescription() + " and "
					+ newGrid.getAuditDescription(), ex);
		}
	}

	private void addAuditKBMasterForPropertyChanges(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException {
		// check activation date
		if (!UtilBase.isSame(oldGrid.getEffectiveDate(), newGrid.getEffectiveDate())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: eff date changed");
			int kbAuditDetailID = auditStorage.getNextAuditID();
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
					kbAuditDetailID,
					AuditConstants.KB_MOD_TYPE_MODIFY_EFFECTIVE_DATE,
					"Changed effective date of " + newGrid.getAuditDescription());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE,
					(oldGrid.getEffectiveDate() == null
							? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION
							: oldGrid.getEffectiveDate().getName()));
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE,
					(newGrid.getEffectiveDate() == null
							? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION
							: newGrid.getEffectiveDate().getName()));
		}
		// check expiration date
		if (!UtilBase.isSame(oldGrid.getExpirationDate(), newGrid.getExpirationDate())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: exp date changed");
			int kbAuditDetailID = auditStorage.getNextAuditID();
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
					kbAuditDetailID,
					AuditConstants.KB_MOD_TYPE_MODIFY_EXPIRATION_DATE,
					"Changed expiration date of " + newGrid.getAuditDescription());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE,
					(oldGrid.getExpirationDate() == null
							? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION
							: oldGrid.getExpirationDate().getName()));
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE,
					(newGrid.getExpirationDate() == null
							? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION
							: newGrid.getExpirationDate().getName()));
		}
		// check status
		if (!UtilBase.isSame(newGrid.getStatus(), oldGrid.getStatus())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: status changed");
			int kbAuditDetailID = auditStorage.getNextAuditID();
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
					kbAuditDetailID,
					AuditConstants.KB_MOD_TYPE_MODIFY_STATUS,
					"Changed status of " + newGrid.getAuditDescription());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE,
					oldGrid.getStatus());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE,
					newGrid.getStatus());
		}
		// check comments
		if (!UtilBase.isSame(newGrid.getComments(), oldGrid.getComments())) {
			logger.debug("--> addAuditKBMasterForPropertyChanges: comments changed");
			int kbAuditDetailID = auditStorage.getNextAuditID();
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
					kbAuditDetailID,
					AuditConstants.KB_MOD_TYPE_MODIFY_COMMENTS,
					"Changed comments of " + newGrid.getAuditDescription());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE,
					oldGrid.getComments());
			auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
					kbAuditDetailID,
					AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE,
					newGrid.getComments());
		}
	}

	public String getDescription() {
		return "Update " + oldGrid.getAuditDescription() + " to " + newGrid.getAuditDescription();
	}

	protected int getKBChangedElementID() {
		return newGrid.getID();
	}

	protected int getKBChangedElementTypeID() {
		return (newGrid.isParameterGrid()
				? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION
				: AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION);
	}

}

package com.mindbox.pe.server.audit.command;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

public class DeleteActivationsAuditCommand extends AbstractAuditCommandWithUser {

	protected final List<? extends AbstractGrid<?>> removedGridList;

	public DeleteActivationsAuditCommand(List<? extends AbstractGrid<?>> removedGridList, Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID);
		this.removedGridList = Collections.unmodifiableList(removedGridList);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		for (Iterator<? extends AbstractGrid<?>> iter = removedGridList.iterator(); iter.hasNext();) {
			AbstractGrid<?> grid =  iter.next();
			addAuditKBMaster(grid, auditStorage, auditDataBuilder);
		}
	}

	private void addAuditKBMaster(AbstractGrid<?> grid, AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException,
			ServiceException {
		int kbAuditMasterID = auditStorage.getNextAuditID();
		int kbAuditDetailID = auditStorage.getNextAuditID();
		auditDataBuilder.insertAuditMasterLog(kbAuditMasterID, (grid.isParameterGrid()
				? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION
				: AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION), grid.getID());
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
				kbAuditDetailID,
				AuditConstants.KB_MOD_TYPE_GENERIC_REMOVE,
				"Removed " + grid.getAuditDescription());
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				kbAuditDetailID,
				AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID,
				String.valueOf(grid.getTemplateID()));
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				kbAuditDetailID,
				AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID,
				String.valueOf((grid.getEffectiveDate() == null ? -1 : grid.getEffectiveDate().getID())));
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				kbAuditDetailID,
				AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID,
				String.valueOf((grid.getExpirationDate() == null ? -1 : grid.getExpirationDate().getID())));
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				kbAuditDetailID,
				AuditConstants.KB_ELEMENT_TYPE_STATUS,
				grid.getStatus());
		GuidelineContext[] contexts = grid.extractGuidelineContext();
		for (int i = 0; i < contexts.length; i++) {
			int[] ids = contexts[i].getIDs();
			int elementType = (contexts[i].hasCategoryContext() ? AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID : AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID);
			for (int j = 0; j < ids.length; j++) {
				auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
						kbAuditDetailID,
						elementType,
						CategoryOrEntityValue.asString(contexts[i].getGenericEntityTypeForContext(), !contexts[i].hasCategoryContext(), ids[j]));
			}
		}
	}

	public String getDescription() {
		return "Removed " + removedGridList.size() + "grids; " + super.getDescription();
	}
}

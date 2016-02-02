package com.mindbox.pe.server.audit.command;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.server.spi.audit.MutableAuditKBDetail;
import com.mindbox.pe.server.spi.audit.DefaultAuditKBDetailData;
import com.mindbox.pe.server.spi.audit.MutableAuditKBMaster;

class AuditKBMasterBuilder {

	private final MutableAuditKBMaster auditKBMaster;

	public AuditKBMasterBuilder(int kbAuditID, int kbChangedTypeID, int elementID) {
		this.auditKBMaster = new MutableAuditKBMaster();
		auditKBMaster.setKbAuditID(kbAuditID);
		auditKBMaster.setKbChangedTypeID(kbChangedTypeID);
		auditKBMaster.setElementID(elementID);
	}

	public MutableAuditKBMaster getAuditKBMaster() {
		return auditKBMaster;
	}

	public final void insertAuditDetailLog(int kbAuditDetailID, int kbModTypeID, String description) {
		checkAuditDetailID(kbAuditDetailID);
		MutableAuditKBDetail auditDetail = new MutableAuditKBDetail();
		auditDetail.setKbAuditID(auditKBMaster.getKbAuditID());
		auditDetail.setKbAuditDetailID(kbAuditDetailID);
		auditDetail.setKbModTypeID(kbModTypeID);
		auditDetail.setDescription(description);
		auditKBMaster.add(auditDetail);
	}

	public final void insertAuditDetailDataLog(int kbAuditDetailID, int elementTypeID, Object element) {
		insertAuditDetailDataLog(kbAuditDetailID, elementTypeID, (element == null ? "" : (element instanceof Auditable
				? ((Auditable) element).getAuditDescription()
				: element.toString())));
	}

	public final void insertAuditDetailDataLog(int kbAuditDetailID, int elementTypeID, String elementValue) {
		DefaultAuditKBDetailData detailData = new DefaultAuditKBDetailData(kbAuditDetailID, elementTypeID, elementValue);
		for (int i = 0; i < auditKBMaster.detailCount(); i++) {
			MutableAuditKBDetail detail = (MutableAuditKBDetail) auditKBMaster.getDetail(i);
			if (detail.getKbAuditDetailID() == kbAuditDetailID) {
				detail.add(detailData);
				return;
			}
		}
		throw new IllegalArgumentException("No KB audit detail of id " + kbAuditDetailID + " found");
	}

	private void checkAuditDetailID(int kbAuditDetailID) {
		for (int i = 0; i < auditKBMaster.detailCount(); i++) {
			if (auditKBMaster.getDetail(i).getKbAuditDetailID() == kbAuditDetailID) {
				throw new IllegalArgumentException("The specified audit detail id already exists");
			}
		}
	}
}

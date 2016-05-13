package com.mindbox.pe.server.spi.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.xsd.audit.ChangeDetail;

/**
 * Encapsulates audit master.
 *
 */
public final class DefaultAuditKBMaster implements AuditKBMaster {

	private int KbAuditID;
	private int kbChangedTypeID;
	private int elementID;
	private final List<ChangeDetail> changeDetails = new ArrayList<ChangeDetail>();

	public DefaultAuditKBMaster(int kbAuditID, int kbChangedTypeID, int elementID) {
		super();
		KbAuditID = kbAuditID;
		this.kbChangedTypeID = kbChangedTypeID;
		this.elementID = elementID;
	}

	public void add(final ChangeDetail changeDetail) {
		changeDetails.add(changeDetail);
	}

	@Override
	public final List<ChangeDetail> getChangeDetails() {
		return Collections.unmodifiableList(changeDetails);
	}

	@Override
	public final int getElementID() {
		return elementID;
	}

	@Override
	public int getKbAuditID() {
		return KbAuditID;
	}

	@Override
	public final int getKbChangedTypeID() {
		return kbChangedTypeID;
	}

	@Override
	public String toString() {
		return String.format("AuditKBMaster[kbAuditID=%d,element=%s(%s),changeDetails=%s]", KbAuditID, kbChangedTypeID, elementID, changeDetails);
	}
}

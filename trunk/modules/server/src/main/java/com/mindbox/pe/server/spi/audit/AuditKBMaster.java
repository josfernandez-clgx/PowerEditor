package com.mindbox.pe.server.spi.audit;

import java.util.List;

import com.mindbox.pe.xsd.audit.ChangeDetail;

public interface AuditKBMaster {

	List<ChangeDetail> getChangeDetails();

	int getElementID();

	int getKbAuditID();

	int getKbChangedTypeID();
}

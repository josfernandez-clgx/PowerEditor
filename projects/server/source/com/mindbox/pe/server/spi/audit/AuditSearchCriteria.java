package com.mindbox.pe.server.spi.audit;

import java.util.Date;

public interface AuditSearchCriteria {
	
	int[] getAuditTypes();

	Date getBeginDate();

	Date getEndDate();
	
	int[] getKbModifiedElementTypes();
}

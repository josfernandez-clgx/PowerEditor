package com.mindbox.pe.server.spi.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultAuditEventDetail implements AuditEventDetail {

	private List<String> details;

	public DefaultAuditEventDetail(String... details) {
		this.details = new ArrayList<String>();
		for (String detail : details) {
			this.details.add(detail);
		}
	}

	public DefaultAuditEventDetail(Collection<String> details) {
		this.details = new ArrayList<String>();
		for (String detail : details) {
			this.details.add(detail);
		}
	}

	@Override
	public List<String> getDetails() {
		return Collections.unmodifiableList(details);
	}

	public void addDetail(String detail) {
		details.add(detail);
	}

}

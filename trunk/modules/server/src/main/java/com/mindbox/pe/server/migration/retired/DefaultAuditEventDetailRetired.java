package com.mindbox.pe.server.migration.retired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.server.spi.audit.AuditEventDetail;

@Deprecated
public final class DefaultAuditEventDetailRetired implements AuditEventDetail {

	private List<String> details;

	public DefaultAuditEventDetailRetired(String... details) {
		this.details = new ArrayList<String>();
		for (String detail : details) {
			this.details.add(detail);
		}
	}

	public DefaultAuditEventDetailRetired(Collection<String> details) {
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

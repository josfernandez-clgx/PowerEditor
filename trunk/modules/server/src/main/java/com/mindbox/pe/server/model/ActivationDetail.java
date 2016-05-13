package com.mindbox.pe.server.model;

import java.util.Date;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.grid.AbstractGrid;

public class ActivationDetail {

	public static ActivationDetail createInstance(String usageType, AbstractGrid<?> grid, int elementID, boolean isParameter) {
		if (grid == null) {
			DateSynonym dateSynonym = new DateSynonym();
			String desc = (isParameter ? "parameter" : "guideline") + " activation " + elementID;
			return new ActivationDetail(usageType, -1, "N/A", dateSynonym, dateSynonym, null, null, null, desc, desc);
		}
		else {
			return new ActivationDetail(
					usageType,
					grid.getTemplateID(),
					grid.getTemplate().getName(),
					grid.getEffectiveDate(),
					grid.getExpirationDate(),
					grid,
					grid.getStatus(),
					grid.getTemplate().getVersion(),
					grid.getAuditName(),
					grid.getAuditDescription());
		}
	}

	public static ActivationDetail createInstance(String usageType, int templateID, String templateName, DateSynonym effectiveDate, DateSynonym expirationDate, ContextContainer contextContainer,
			final boolean isParameter, final String status, final String version) {
		String auditDesc = templateName + " " + (effectiveDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : effectiveDate.getAuditName()) + " - "
				+ (expirationDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : expirationDate.getAuditName());
		return new ActivationDetail(usageType, templateID, templateName, effectiveDate, expirationDate, contextContainer, status, version, auditDesc, (isParameter ? "parameter" : "guideline")
				+ " activation '" + auditDesc + "'");
	}

	private int templateID;
	private String templateName;
	private Date effectiveDate;
	private Date expirationDate;
	private String effectiveDateName;
	private String expirationDateName;
	private ContextContainer contextContainer;
	private String auditDescription;
	private String auditName;
	private String status;
	private String usageType;
	private String version;

	private ActivationDetail(String usageType, int templateID, String templateName, DateSynonym effectiveDate, DateSynonym expirationDate, ContextContainer contextContainer, String status,
			final String version, String auditName, String auditDescription) {
		this.usageType = usageType;
		this.templateID = templateID;
		this.templateName = templateName;
		this.status = status;
		this.effectiveDate = (effectiveDate == null ? null : effectiveDate.getDate());
		this.effectiveDateName = (effectiveDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : effectiveDate.getAuditName());
		this.expirationDate = (expirationDate == null ? null : expirationDate.getDate());
		this.expirationDateName = (expirationDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : expirationDate.getAuditName());
		this.contextContainer = contextContainer;
		this.version = version;
		this.auditName = auditName;
		this.auditDescription = auditDescription;
	}

	public String getAuditDescription() {
		return auditDescription;
	}

	public String getVersion() {
		return version;
	}

	public String getAuditName() {
		return auditName;
	}

	public ContextContainer getContextContainer() {
		return contextContainer;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public String getEffectiveDateName() {
		return effectiveDateName;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public String getExpirationDateName() {
		return expirationDateName;
	}

	public String getStatus() {
		return status;
	}

	public int getTemplateID() {
		return templateID;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getUsageType() {
		return usageType;
	}

}

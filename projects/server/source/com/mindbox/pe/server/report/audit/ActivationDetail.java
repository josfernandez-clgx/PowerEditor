package com.mindbox.pe.server.report.audit;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.ContextContainer;
import com.mindbox.pe.model.DateSynonym;

public class ActivationDetail {

	public static ActivationDetail createInstance(String usageType, int templateID, String templateName, DateSynonym effectiveDate,
			DateSynonym expirationDate, ContextContainer contextContainer, boolean isParameter, String status) {
		String auditDesc = templateName + " "
				+ (effectiveDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : effectiveDate.getAuditName()) + " - "
				+ (expirationDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : expirationDate.getAuditName());
		return new ActivationDetail(
				usageType,
				templateID,
				templateName,
				effectiveDate,
				expirationDate,
				contextContainer,
				status,
				auditDesc,
				(isParameter ? "parameter" : "guideline") + " activation '" + auditDesc + "'");
	}

	public static ActivationDetail createInstance(String usageType, AbstractGrid<?> grid, int elementID, boolean isParameter) {
		if (grid == null) {
			DateSynonym dateSynonym = new DateSynonym();
			String desc = (isParameter ? "parameter" : "guideline") + " activation " + elementID;
			return new ActivationDetail(usageType, -1, "N/A", dateSynonym, dateSynonym, null, null, desc, desc);
		}
		else {
			return new ActivationDetail(
					usageType,
					grid.getTemplateID(),
					((Auditable) grid.getTemplate()).getAuditDescription(),
					grid.getEffectiveDate(),
					grid.getExpirationDate(),
					grid,
					grid.getStatus(),
					grid.getAuditName(),
					grid.getAuditDescription());
		}
	}

	private int templateID;
	private String templateName;
	private String effectiveDate;
	private String expirationDate;
	private String effectiveDateName;
	private String expirationDateName;
	private ContextContainer contextContainer;
	private String auditDescription;
	private String auditName;
	private String status;
	private String usageType;

	private ActivationDetail(String usageType, int templateID, String templateName, DateSynonym effectiveDate, DateSynonym expirationDate,
			ContextContainer contextContainer, String status, String auditName, String auditDescription) {
		this.usageType = usageType;
		this.templateID = templateID;
		this.templateName = templateName;
		this.status = status;
		this.effectiveDate = (effectiveDate == null ? "" : ConfigUtil.toDateXMLString(effectiveDate.getDate()));
		this.effectiveDateName = (effectiveDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : effectiveDate.getAuditName());
		this.expirationDate = (expirationDate == null ? "" : ConfigUtil.toDateXMLString(expirationDate.getDate()));
		this.expirationDateName = (expirationDate == null ? Constants.UNSPECIFIED_DATE_SYNONYM_DESCRIPTION : expirationDate.getAuditName());
		this.contextContainer = contextContainer;
		this.auditName = auditName;
		this.auditDescription = auditDescription;
	}

	public String getAuditName() {
		return auditName;
	}

	public String getUsageType() {
		return usageType;
	}

	public String getStatus() {
		return status;
	}

	public int getTemplateID() {
		return templateID;
	}

	public ContextContainer getContextContainer() {
		return contextContainer;
	}

	public String getEffectiveDateName() {
		return effectiveDateName;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public String getExpirationDateName() {
		return expirationDateName;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getAuditDescription() {
		return auditDescription;
	}


}

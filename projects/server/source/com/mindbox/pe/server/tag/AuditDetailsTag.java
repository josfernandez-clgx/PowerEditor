package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.server.report.audit.AuditLog;
import com.mindbox.pe.server.report.audit.AuditReportHelper;
import com.mindbox.pe.server.spi.ServiceException;

/**
 * Implementation of &lt;audit-detail&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>var</code> - Name of the attribute to be bound to the list of {@link AuditLog} objects</li>
 * </ul>
 */
public class AuditDetailsTag extends AbstractVarTag {

	private static final long serialVersionUID = 3038861095417400117L;

	private String templateName;
	private String templateID;
	private String usageType;
	private String columns;
	private String contextElements;
	private String includeChildren;
	private String includeParents;
	private String includeEmptyContexts;
	private String status;
	private String beginDate;
	private String endDate;
	private String errorsVar;
	private String auditTypes;

	public int doStartTag() throws JspException {
		if (UtilBase.isEmpty(errorsVar)) throw new JspException("errorsVar is required");
		AuditReportHelper auditReportHelper = new AuditReportHelper(
				auditTypes,
				templateName,
				templateID,
				usageType,
				contextElements,
				includeChildren,
				includeParents,
				includeEmptyContexts,
				status,
				beginDate,
				endDate);
		try {
			setVarObject(auditReportHelper.getAuditLogs());
		}
		catch (ServiceException e) {
			throw new JspException(e);
		}
		finally {
			if (auditReportHelper != null) pageContext.setAttribute(errorsVar, auditReportHelper.getErrorMessages());
		}
		return SKIP_BODY;
	}

	public String getErrorsVar() {
		return errorsVar;
	}

	public void setErrorsVar(String errorsVar) {
		this.errorsVar = errorsVar;
	}

	public String getAuditTypes() {
		return auditTypes;
	}

	public void setAuditTypes(String auditTypes) {
		this.auditTypes = auditTypes;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getContextElements() {
		return contextElements;
	}

	public void setContextElements(String contextElements) {
		this.contextElements = contextElements;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIncludeChildren() {
		return includeChildren;
	}

	public void setIncludeChildren(String includeChildren) {
		this.includeChildren = includeChildren;
	}

	public String getIncludeEmptyContexts() {
		return includeEmptyContexts;
	}

	public void setIncludeEmptyContexts(String includeEmptyContexts) {
		this.includeEmptyContexts = includeEmptyContexts;
	}

	public String getIncludeParents() {
		return includeParents;
	}

	public void setIncludeParents(String includeParents) {
		this.includeParents = includeParents;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUsageType() {
		return usageType;
	}

	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}


}

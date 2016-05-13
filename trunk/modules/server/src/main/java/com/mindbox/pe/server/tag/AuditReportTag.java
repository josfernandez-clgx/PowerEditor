package com.mindbox.pe.server.tag;

import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.server.report.audit.AuditReportHelper;
import com.mindbox.pe.xsd.audit.AuditReport;
import com.mindbox.pe.xsd.audit.AuditReport.Summary;

/**
 * Implementation of &lt;audit-report&gt; PowerEditor custom tag.
 * <p>
 * </ul>
 */
public class AuditReportTag extends TagSupport {

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
	private String auditTypes;

	public int doStartTag() throws JspException {
		final AuditReportHelper auditReportHelper = new AuditReportHelper(
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
			final AuditReport auditReport = new AuditReport();

			final List<AuditReport.AuditEvent> auditEvents = auditReportHelper.getAuditEvents();
			if (auditEvents != null) {
				auditReport.getAuditEvent().addAll(auditEvents);
			}

			final List<String> errorMessageList = auditReportHelper.getErrorMessages();
			if (!UtilBase.isEmpty(errorMessageList)) {
				auditReport.getErrorMessage().addAll(auditReportHelper.getErrorMessages());
			}

			final Summary summary = new Summary();
			summary.setReportDate(DateUtil.toXMLGregorianCalendar(new Date()));
			summary.setAuditEventCount(Integer.valueOf(auditEvents == null ? 0 : auditEvents.size()));

			auditReport.setSummary(summary);

			XmlUtil.marshal(auditReport, pageContext.getOut(), false, false, AuditReport.class);
		}
		catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_PAGE;
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

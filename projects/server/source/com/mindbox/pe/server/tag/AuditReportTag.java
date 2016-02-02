package com.mindbox.pe.server.tag;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.server.report.ReportException;
import com.mindbox.pe.server.report.audit.ActivationDetail;
import com.mindbox.pe.server.report.audit.AuditLog;
import com.mindbox.pe.server.report.audit.AuditReportHelper;
import com.mindbox.pe.server.report.audit.ChangeDetail;
import com.mindbox.pe.server.report.audit.ChangedElement;
import com.mindbox.pe.server.spi.ServiceException;

/**
 * Implementation of &lt;audit-report&gt; PowerEditor custom tag.
 * <p>
 * </ul>
 */
public class AuditReportTag extends AbstractXMLOutputTag {

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
			List<AuditLog> auditLogList = auditReportHelper.getAuditLogs();
			writeOpen("audit-report");
			if (!UtilBase.isEmpty(auditLogList)) {
				for (AuditLog auditLog : auditLogList) {
					write(auditLog, auditReportHelper);
				}
			}
			List<String> errorMessageList = auditReportHelper.getErrorMessages();
			if (!UtilBase.isEmpty(errorMessageList)) {
				writeElement("error-message", UtilBase.toString(errorMessageList.toArray(new String[0])));
			}
			writeOpen("summary");
			writeElement("report-date", ConfigUtil.toDateXMLString(new Date()));
			writeElement("audit-event-count", (auditLogList == null ? 0 : auditLogList.size()));
			writeClose("summary");
			writeClose("audit-report");
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		catch (ServiceException e) {
			throw new JspException(e);
		}
		return SKIP_PAGE;
	}

	private void write(AuditLog auditLog, AuditReportHelper auditReportHelper) throws IOException {
		writeOpen("audit-event", "auditID", auditLog.getAuditID());
		writeElement("type", auditLog.getAuditType().getName());
		writeElement("audit-date", auditLog.getDate());
		writeElement("username", auditLog.getUserName());
		List<ChangedElement> changedElementlList = auditLog.getChangedElements();
		if (!UtilBase.isEmpty(changedElementlList)) {
			for (ChangedElement changedElement : changedElementlList) {
				write(changedElement, auditReportHelper);
			}
		}
		writeClose("audit-event");
	}

	private void write(ChangedElement changedElement, AuditReportHelper auditReportHelper) throws IOException {
		writeOpen("change-details");
		writeOpen("changed-element", "type", changedElement.getKbChangedTypeDescription(), "name", changedElement.getElementName());
		if (changedElement.isActivation()) {
			ActivationDetail activationDetail;
			try {
				activationDetail = changedElement.getActivationDetail();
				if (activationDetail != null) {
					write(activationDetail);
				}
			}
			catch (ReportException e) {
				auditReportHelper.addErrorMessage("Failed to get activation details for " + changedElement.getKbChangedTypeDescription()
						+ ": " + changedElement.getElementName() + " (kbAuditID=" + changedElement.getKbAuditID() + ")" + "; "
						+ e.getMessage());
			}
		}
		writeClose("changed-element");
		List<ChangeDetail> changeDetailList = changedElement.getChangeDetails();
		if (!UtilBase.isEmpty(changeDetailList)) {
			for (ChangeDetail changeDetail : changeDetailList) {
				write(changeDetail, changedElement.isRemoved());
			}
		}
		writeClose("change-details");
	}

	private void write(ActivationDetail activationDetail) throws IOException {
		writeElement("usage-type", activationDetail.getUsageType());
		writeElement("template-name", activationDetail.getTemplateName());
		writeElement("activation-date-name", activationDetail.getEffectiveDateName());
		writeElement("activation-date", activationDetail.getEffectiveDate());
		writeElement("expiration-date-name", activationDetail.getExpirationDateName());
		writeElement("expiration-date", activationDetail.getExpirationDate());
		if (activationDetail.getContextContainer() != null) {
			writeElement("context", WriteContextTag.toContextTagValue(activationDetail.getContextContainer()));
		}
	}

	private void write(ChangeDetail changeDetail, boolean isChangedElementRemoved) throws IOException {
		writeOpen("change-detail");
		writeElement("change-type", changeDetail.getKbModTypeDescription());
		writeElement("change-description", changeDetail.getDescription());
		if (!isChangedElementRemoved) {
			Set<Map.Entry<Integer,String>> detailDataSet = changeDetail.getDetailDataEntrySet();
			for (Map.Entry<Integer,String> entry : detailDataSet) {
				Integer key = entry.getKey();
				String value = entry.getValue();
				WriteChangeDetailElementTag.writeChangeDetailElement(pageContext.getOut(), key.intValue(), value);
			}
		}
		writeClose("change-detail");
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

package com.mindbox.pe.server.report.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.AuditConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.model.ModelUtil;
import com.mindbox.pe.server.report.ErrorMessageStorage;
import com.mindbox.pe.server.report.ReportException;
import com.mindbox.pe.server.report.ReportFilterDataHolder;
import com.mindbox.pe.server.report.ReportGeneratorHelper;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;
import com.mindbox.pe.server.spi.audit.DefaultAuditSearchCriteria;
import com.mindbox.pe.xsd.audit.AuditReport;

public class AuditReportHelper implements ErrorMessageStorage {

	private final List<String> errorMessageList;
	private final ReportFilterDataHolder reportFilterDataHolder;
	private final Logger logger = Logger.getLogger(ReportGeneratorHelper.class);
	private final Date endDate;
	private final int[] auditTypes;

	public AuditReportHelper(String auditTypesStr, String templateName, String templateID, String usageType, String contextElements, String includeChildren, String includeParents,
			String includeEmptyContexts, String status, String beginDate, String endDate) {
		logger.debug("<init>: " + auditTypesStr + ",bDate=" + beginDate + ",eDate=" + endDate + ",status=" + status);
		this.errorMessageList = new ArrayList<String>();
		this.reportFilterDataHolder = new ReportFilterDataHolder(
				this,
				false,
				templateName,
				templateID,
				usageType,
				null,
				contextElements,
				includeChildren,
				includeParents,
				null,
				includeEmptyContexts,
				status,
				beginDate);
		this.auditTypes = UtilBase.toIntArray(auditTypesStr);
		this.endDate = (UtilBase.isEmpty(endDate) ? null : reportFilterDataHolder.parseDate(endDate));
	}

	private boolean accept(AuditEvent auditEvent) throws ReportException {
		if (auditEvent.kbMasterCount() > 0) {
			for (int i = 0; i < auditEvent.kbMasterCount(); i++) {
				if (!accept(auditEvent.getKBMaster(i))) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean accept(AuditKBMaster auditKBMaster) throws ReportException {
		if (reportFilterDataHolder.isTemplateSpecified()) {
			if (isForGuidelineActivation(auditKBMaster)) {
				ProductGrid grid = GridManager.getInstance().getProductGrid(auditKBMaster.getElementID());
				if (grid != null) {
					if (!reportFilterDataHolder.isSpecifiedTemplateID(grid.getTemplateID())) {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}

		if (reportFilterDataHolder.isContextSpecified() || reportFilterDataHolder.isStatusSpecified()) {
			if (isForPolicyActivation(auditKBMaster)) {
				final AbstractGrid<?> grid = findGrid(auditKBMaster);
				if (grid == null) {
					return false;
				}

				// check status
				if (reportFilterDataHolder.isStatusSpecified()) {
					if (!reportFilterDataHolder.matchesStatus(grid)) {
						return false;
					}
				}

				// check context
				if (reportFilterDataHolder.isContextSpecified()) {
					if (!reportFilterDataHolder.containsContext(grid)) {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

	public void addErrorMessage(String message) {
		errorMessageList.add(message);
	}

	private AbstractGrid<?> findGrid(AuditKBMaster auditKBMaster) {
		if (auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION) {
			return ParameterManager.getInstance().getGrid(auditKBMaster.getElementID());
		}
		else {
			return GridManager.getInstance().getProductGrid(auditKBMaster.getElementID());
		}
	}

	/**
	 * 
	 * @return list of {@link AuditReport.AuditEvent} instances
	 * @throws ServiceException on error
	 */
	public List<AuditReport.AuditEvent> getAuditEvents() throws ServiceException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> getAuditLogs: " + UtilBase.toString(auditTypes) + ",bDate=" + reportFilterDataHolder.getDate() + ",eDate=" + endDate);
		}

		final DefaultAuditSearchCriteria auditSearchCriteria = new DefaultAuditSearchCriteria();
		// set audit types
		if (auditTypes != null && auditTypes.length > 0) {
			auditSearchCriteria.setAuditTypes(auditTypes);
		}
		else if (isPolicyActivationsOnly()) {
			auditSearchCriteria.setAuditTypes(new int[] { AuditEventType.KB_MOD.getId() });
		}
		auditSearchCriteria.setBeginDate(reportFilterDataHolder.getDate());
		auditSearchCriteria.setEndDate(endDate);
		List<Integer> intList = new ArrayList<Integer>();
		if (isGuidelineActivationsOnly()) {
			intList.add(new Integer(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION));
		}
		if (isPolicyActivationsOnly()) {
			intList.add(new Integer(AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION));
		}
		auditSearchCriteria.setKbModifiedElementTypes(UtilBase.toIntArray(intList));

		final List<AuditEvent> auditEvents = ServiceProviderFactory.getAuditServiceProvider().retrieveAuditEvents(auditSearchCriteria);

		final List<AuditReport.AuditEvent> auditEventList = new ArrayList<AuditReport.AuditEvent>();
		for (AuditEvent auditEvent : auditEvents) {
			try {
				if (accept(auditEvent)) {
					auditEventList.add(ModelUtil.asAuditReportAuditEvent(auditEvent));
				}
			}
			catch (ReportException ex) {
				errorMessageList.add(auditEvent + " Skipped because of error: " + ex.getMessage());
			}
		}
		logger.debug("<-- event.size = " + auditEventList.size());
		return auditEventList;
	}

	public List<String> getErrorMessages() {
		return Collections.unmodifiableList(errorMessageList);
	}

	private boolean isForGuidelineActivation(AuditKBMaster auditKBMaster) {
		return auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION;
	}

	private boolean isForPolicyActivation(AuditKBMaster auditKBMaster) {
		return auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION
				|| auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
	}

	private boolean isGuidelineActivationsOnly() {
		return reportFilterDataHolder.isUsageTypeSpecified() || reportFilterDataHolder.isTemplateSpecified() || reportFilterDataHolder.isColumnNameSpecified();
	}

	private boolean isPolicyActivationsOnly() {
		return isGuidelineActivationsOnly() || reportFilterDataHolder.isContextSpecified() || reportFilterDataHolder.isStatusSpecified();
	}
}

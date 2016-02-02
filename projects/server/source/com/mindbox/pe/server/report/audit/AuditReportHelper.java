package com.mindbox.pe.server.report.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.ParameterManager;
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

public class AuditReportHelper implements ErrorMessageStorage {

	private final List<String> errorMessageList;
	private final ReportFilterDataHolder reportFilterDataHolder;
	private final Logger logger = Logger.getLogger(ReportGeneratorHelper.class);
	private final Date endDate;
	private final int[] auditTypes;

	public AuditReportHelper(String auditTypesStr, String templateName, String templateID, String usageType, String contextElements,
			String includeChildren, String includeParents, String includeEmptyContexts, String status, String beginDate, String endDate) {
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

	public void addErrorMessage(String message) {
		errorMessageList.add(message);
	}

	private boolean isGuidelineActivationsOnly() {
		return reportFilterDataHolder.isUsageTypeSpecified() || reportFilterDataHolder.isTemplateSpecified()
				|| reportFilterDataHolder.isColumnNameSpecified();
	}

	private boolean isPolicyActivationsOnly() {
		return isGuidelineActivationsOnly() || reportFilterDataHolder.isContextSpecified() || reportFilterDataHolder.isStatusSpecified();
	}

	/**
	 * 
	 * @return list of {@link AuditLog} instances
	 * @throws ServiceException on error
	 */
	public List<AuditLog> getAuditLogs() throws ServiceException {
		if (logger.isDebugEnabled()) {
			logger.debug("--> getAuditLogs: " + UtilBase.toString(auditTypes) + ",bDate=" + reportFilterDataHolder.getDate() + ",eDate=" + endDate);
		}
		DefaultAuditSearchCriteria auditSearchCriteria = new DefaultAuditSearchCriteria();
		// set audit types
		if (auditTypes != null && auditTypes.length > 0) {
			auditSearchCriteria.setAuditTypes(auditTypes);
		}
		else if (isPolicyActivationsOnly()) {
			auditSearchCriteria.setAuditTypes(new int[]{AuditEventType.KB_MOD.getId()});
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
		List<AuditEvent> details = ServiceProviderFactory.getAuditServiceProvider().retrieveAuditEvents(auditSearchCriteria);
		List<AuditLog> list = new ArrayList<AuditLog>();
		for (Iterator<AuditEvent> iter = details.iterator(); iter.hasNext();) {
			AuditEvent auditEvent = iter.next();
			try {
				if (accept(auditEvent)) {
					list.add(AuditLog.valueOf(auditEvent));
				}
			}
			catch (ReportException ex) {
				errorMessageList.add(auditEvent + " Skipped because of error: " + ex.getMessage());
			}
		}
		logger.debug("<-- event.size = " + list.size());
		return list;
	}

	public List<String> getErrorMessages() {
		return Collections.unmodifiableList(errorMessageList);
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
					// TODO Kim, 2007-06-05: make if more efficient by doing this w/o using ChangedElement
					ChangedElement changedElement = ChangedElement.valueOf(auditKBMaster);
					if (!reportFilterDataHolder.isSpecifiedTemplateID(changedElement.getActivationDetail().getTemplateID())) {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}
		if (reportFilterDataHolder.isContextSpecified() || reportFilterDataHolder.isStatusSpecified()) {
			if (isForPolicyActivation(auditKBMaster)) {
				AbstractGrid<?> grid = findGrid(auditKBMaster);
				// TODO Kim, 2007-06-05: make if more efficient by doing this w/o using ChangedElement
				ActivationDetail activationDetail = null;
				if (grid == null) {
					activationDetail = ChangedElement.valueOf(auditKBMaster).getActivationDetail();
				}
				// check status
				if (reportFilterDataHolder.isStatusSpecified()) {
					if (grid != null) {
						if (!reportFilterDataHolder.matchesStatus(grid)) {
							return false;
						}
					}
					else {
						if (!reportFilterDataHolder.matchesStatus(activationDetail.getStatus())) {
							return false;
						}
					}
				}
				// check context
				if (reportFilterDataHolder.isContextSpecified()) {
					if (grid != null) {
						if (!reportFilterDataHolder.containsContext(grid)) {
							return false;
						}
					}
					else {
						if (!reportFilterDataHolder.containsContext(activationDetail.getContextContainer())) {
							return false;
						}
					}
				}
			}
			else {
				return false;
			}
		}
		return true;
	}

	private boolean isForGuidelineActivation(AuditKBMaster auditKBMaster) {
		return auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION;
	}

	private boolean isForPolicyActivation(AuditKBMaster auditKBMaster) {
		return auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION
				|| auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION;
	}

	private AbstractGrid<?> findGrid(AuditKBMaster auditKBMaster) {
		if (auditKBMaster.getKbChangedTypeID() == AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION) {
			return ParameterManager.getInstance().getGrid(auditKBMaster.getElementID());
		}
		else {
			return GridManager.getInstance().getProductGrid(auditKBMaster.getElementID());
		}
	}
}

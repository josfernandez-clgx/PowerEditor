package com.mindbox.pe.server.servlet.handlers;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BulkUpdateGridDataRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

public class BulkUpdateGridDataRequestHandler extends AbstractSessionRequestHandler<BulkUpdateGridDataRequest> {

	public ResponseComm handleRequest(BulkUpdateGridDataRequest request, HttpServletRequest httpservletrequest) throws ServerException, LockException {
		User user = getUser(request.getUserID());

		List<GuidelineReportData> reportdataList = request.getGuidelineReportData();
		String status = request.getStatus();
		DateSynonym effDate = request.getEffDate();
		DateSynonym expDate = request.getExpDate();
		GuidelineReportData reportdata = null;
		for (Iterator<GuidelineReportData> iter = reportdataList.iterator(); iter.hasNext();) {
			reportdata = iter.next();
			if (reportdata != null) {
				if (reportdata.getDataType().equals("GuidelineGrid")) {
					int templateID = reportdata.getId();
					String privName = GuidelineTemplateManager.getInstance().getTemplatePermission(templateID, false);
					if (!isAuthorized(request, privName)) {
						return generateAuthorizationFailureResponse();
					}
					List<ProductGrid> grids = GridActionCoordinator.getInstance().fetchGridData(templateID, reportdata.getContext());
					for (ProductGrid gridData : grids) {
						if (gridData.getID() == reportdata.getGridID()) {

							// If status is not null, set it and set the status change date.
							if (status != null) {
								gridData.setStatus(status);
								gridData.setStatusChangeDate(new java.util.Date());
							}

							if (effDate != null && effDate.getDate() != null) {
								gridData.setEffectiveDate(effDate);
							}
							if (expDate != null && expDate.getDate() != null) {
								gridData.setExpirationDate(expDate);
							}
						}
						ServerContextUtil.setContext(gridData, reportdata.getContext());
					}
					GridActionCoordinator.getInstance().saveGridData(templateID, grids, true, user);
				}
				else { // parameter grid items
					ParameterGrid paramGrid = reportdata.getParameterGrid();
					if (status != null) {
						paramGrid.setStatus(status);
						paramGrid.setStatusChangeDate(new java.util.Date());
					}
					if (effDate != null && effDate.getDate() != null) {
						paramGrid.setEffectiveDate(effDate);
					}
					if (expDate != null && expDate.getDate() != null) {
						paramGrid.setExpirationDate(expDate);
					}
					try {
						BizActionCoordinator.getInstance().save(paramGrid, user);
					}
					catch (ServletActionException sae) {
						return generateErrorResponse(sae);
					}
				}
			}
		}
		return new SuccessResponse();
	}

}
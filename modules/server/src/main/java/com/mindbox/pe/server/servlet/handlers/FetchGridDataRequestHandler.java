package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchGridDataRequest;
import com.mindbox.pe.communication.GridDataResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.servlet.ServletActionException;


public class FetchGridDataRequestHandler extends AbstractSessionRequestHandler<FetchGridDataRequest> {

	public ResponseComm handleRequest(FetchGridDataRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		List<ProductGrid> resultList = GridActionCoordinator.getInstance().fetchGridData(request.getTemplateID(), request.getContexts());

		logger.info("process request returned " + resultList);

		GridDataResponse response = new GridDataResponse(
				GuidelineTemplateManager.getInstance().getTemplate(request.getTemplateID()),
				resultList,
				Util.toString(request.getContexts()));
		return response;
	}

}
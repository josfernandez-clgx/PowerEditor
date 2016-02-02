package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchParameterGridRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public final class FetchParameterGridRequestHandler extends AbstractSessionRequestHandler<FetchParameterGridRequest> {

	public ResponseComm handleRequest(FetchParameterGridRequest request, HttpServletRequest httpservletrequest) {
		int templateID = request.getTemplateID();
		
		ListResponse<ParameterGrid> response = new ListResponse<ParameterGrid>(BizActionCoordinator.getInstance().fetchParameterGrids(templateID));
		return response;
	}
}

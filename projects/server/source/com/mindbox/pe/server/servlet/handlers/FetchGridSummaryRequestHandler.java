package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchGridSummaryRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.GridSummary;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.model.User;

public class FetchGridSummaryRequestHandler extends AbstractSessionRequestHandler<FetchGridSummaryRequest> {

	public ResponseComm handleRequest(FetchGridSummaryRequest request, HttpServletRequest httpservletrequest) throws ServerException {
		User user = getUser(request.getUserID());
		List<GridSummary> resultList = GridActionCoordinator.getInstance().fetchGridSummaries(
				request.getUsageType(),
				request.getContexts(),
				user);
		logger.info("process request returned " + resultList);

		ListResponse<GridSummary> response = new ListResponse<GridSummary>(resultList);
		return response;
	}
}
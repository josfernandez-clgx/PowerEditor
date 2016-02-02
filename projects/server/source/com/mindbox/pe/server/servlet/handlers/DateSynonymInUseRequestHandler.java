package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.DateSynonymInUseRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

/**
 * Processes {@link com.mindbox.pe.communication.DateSynonymInUseRequest}.
 * @author MDA MindBox, Inc
 */
public class DateSynonymInUseRequestHandler extends AbstractSessionRequestHandler<DateSynonymInUseRequest> {

	public ResponseComm handleRequest(DateSynonymInUseRequest request, HttpServletRequest httpservletrequest) {
		return new BooleanResponse(BizActionCoordinator.getInstance().isDateSynonymInUse(request.getDateSynonym()));
	}
}

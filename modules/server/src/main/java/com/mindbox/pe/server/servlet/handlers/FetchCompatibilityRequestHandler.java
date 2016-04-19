package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchCompatibilityRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 3.0.0
 */
public final class FetchCompatibilityRequestHandler extends AbstractSessionRequestHandler<FetchCompatibilityRequest> {

	public ResponseComm handleRequest(FetchCompatibilityRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		return new ListResponse<GenericEntityCompatibilityData>(BizActionCoordinator.getInstance().fetchCompatibilityData(
				request.getSourceType(),
				request.getTargetType()));
	}
}
package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ReplaceDateSynonymsRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.User;

/**
 * @author MDA MindBox, Inc
 * @since PowerEditor 4.4.2
 */
public final class ReplaceDateSynonymsRequestHandler extends AbstractSessionRequestHandler<ReplaceDateSynonymsRequest> {

	public ResponseComm handleRequest(ReplaceDateSynonymsRequest request, HttpServletRequest httpservletrequest) throws ServerException {
		processRequest(request.getToBeReplaced(), request.getReplacement(), getUser(request.getUserID()));
		return new SuccessResponse();
	}

	private void processRequest(DateSynonym[] toBeReplaced, DateSynonym replacement, User user) throws ServerException {
		BizActionCoordinator.getInstance().replaceDateSynonyms(toBeReplaced, replacement, user);
	}
}

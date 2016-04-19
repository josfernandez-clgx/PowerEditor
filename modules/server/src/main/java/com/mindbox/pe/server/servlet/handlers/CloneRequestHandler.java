package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.CloneRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveResponse;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class CloneRequestHandler extends AbstractSessionRequestHandler<CloneRequest> {

	public ResponseComm handleRequest(CloneRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		int entityID = processRequest(
				(GenericEntity) request.getPersistent(),
				request.doLockEntity(),
				request.shouldCopyPolicies(),
				getUser(request.getUserID()));
		logger.info("process request returned " + entityID);

		SaveResponse response = new SaveResponse(entityID);
		return response;
	}

	private int processRequest(GenericEntity object, boolean lockEntity, boolean copyPolicies, User user) throws ServletActionException {
		return BizActionCoordinator.getInstance().clone(object, copyPolicies, user, !lockEntity);
	}

}

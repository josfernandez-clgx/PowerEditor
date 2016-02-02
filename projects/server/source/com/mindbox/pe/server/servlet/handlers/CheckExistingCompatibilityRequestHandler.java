package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.CheckExistingCompatibilityRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.servlet.ServletActionException;

public class CheckExistingCompatibilityRequestHandler extends
		AbstractSessionRequestHandler<CheckExistingCompatibilityRequest> {

	@Override
	protected ResponseComm handleRequest(CheckExistingCompatibilityRequest request,
			HttpServletRequest httpservletrequest) throws DataValidationFailedException, LockException,
			ServletActionException, ServerException {
		return new BooleanResponse(EntityManager.getInstance().isCached(
				request.getEntityType1(),
				request.getId1(),
				request.getEntityType2(),
				request.getId2()) != null);
	}

}

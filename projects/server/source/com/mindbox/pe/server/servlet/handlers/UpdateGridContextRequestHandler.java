package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.communication.UpdateGridContextRequest;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Processes a UpdateGridContextRequest.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class UpdateGridContextRequestHandler extends AbstractSessionRequestHandler<UpdateGridContextRequest> {

	@Override
	protected String getRequiredPrivilegeName(UpdateGridContextRequest request) {
		return GuidelineTemplateManager.getInstance().getTemplatePermission(request.getTemplateID(), false);
	}

	public ResponseComm handleRequest(UpdateGridContextRequest request, HttpServletRequest httpservletrequest)
			throws ServletActionException, LockException {
		User user = getUser(request.getUserID());
		GridActionCoordinator.getInstance().updateGridContext(
				request.getTemplateID(),
				request.getGridList(),
				request.getNewContexts(),
				user);

		return new SuccessResponse();
	}

}
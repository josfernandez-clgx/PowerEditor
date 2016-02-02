package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.communication.UpdateGridDataRequest;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Processes  a UpdateGridDataRequest.
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class UpdateGridDataRequestHandler extends AbstractSessionRequestHandler<UpdateGridDataRequest> {

	@Override
	protected String getRequiredPrivilegeName(UpdateGridDataRequest request) {
		return GuidelineTemplateManager.getInstance().getTemplatePermission(request.getTemplateID(), false);
	}

	public ResponseComm handleRequest(UpdateGridDataRequest request, HttpServletRequest httpservletrequest) throws ServletActionException, LockException {
		User user = getUser(request.getUserID());
		GridActionCoordinator.getInstance().syncGridData(
				request.getTemplateID(),
				request.getGridList(),
				request.getRemovedGrids(),
				false,
				user);
		return new SuccessResponse();
	}

}
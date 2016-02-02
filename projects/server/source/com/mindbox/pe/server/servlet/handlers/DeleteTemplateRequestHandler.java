package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.DeleteTemplateRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SuccessResponse;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class DeleteTemplateRequestHandler extends AbstractSessionRequestHandler<DeleteTemplateRequest> {

	@Override
	protected String getRequiredPrivilegeName(DeleteTemplateRequest request) {
		return GuidelineTemplateManager.getInstance().getTemplatePermission(request.getEntityID(), false);
	}

	public ResponseComm handleRequest(DeleteTemplateRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		User user = getUser(request.getUserID());
		BizActionCoordinator.getInstance().deleteTemplate(request.getEntityID(), request.isDeleteGuidelinesOn(), user);
		return new SuccessResponse();
	}

}

package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchFullGuidelineContextRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Processes a FetchFullGuidelineContextRequest.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class FetchFullGuidelineContextRequestHandler extends AbstractSessionRequestHandler<FetchFullGuidelineContextRequest> {

	public ResponseComm handleRequest(FetchFullGuidelineContextRequest request, HttpServletRequest httpservletrequest) throws ServletActionException, ServerException {
		int templateID = request.getTemplateID();

		String s = GuidelineTemplateManager.getInstance().getTemplatePermission(templateID, false);
		if (!isAuthorized(request, s)) {
			return generateAuthorizationFailureResponse();
		}
		else {
			List<GuidelineContext> contextList = GridActionCoordinator.getInstance().fetchFullContext(
					request.getTemplateID(),
					request.getSubContexts());

			return new ListResponse<GuidelineContext>(contextList);
		}
	}

}
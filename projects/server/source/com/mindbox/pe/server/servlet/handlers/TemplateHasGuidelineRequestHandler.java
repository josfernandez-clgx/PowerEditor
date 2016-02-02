package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.TemplateHasGuidelineRequest;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Template has guideline request handler.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 * @see com.mindbox.pe.communication.TemplateHasGuidelineRequest
 */
public final class TemplateHasGuidelineRequestHandler extends AbstractSessionRequestHandler<TemplateHasGuidelineRequest> {

	public ResponseComm handleRequest(TemplateHasGuidelineRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		boolean result = BizActionCoordinator.getInstance().hasGuidelines(request.getTemplateID());
		return new BooleanResponse(result);
	}
}
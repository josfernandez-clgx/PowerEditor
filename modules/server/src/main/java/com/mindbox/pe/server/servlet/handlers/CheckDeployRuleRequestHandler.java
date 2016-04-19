package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.BooleanResponse;
import com.mindbox.pe.communication.CheckDeployRuleRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public final class CheckDeployRuleRequestHandler extends AbstractSessionRequestHandler<CheckDeployRuleRequest> {

	public ResponseComm handleRequest(CheckDeployRuleRequest request, HttpServletRequest httpservletrequest) {
		int templateID = request.getTemplateID();
		int columnID = request.getColumnID();
		
		BooleanResponse response = new BooleanResponse(BizActionCoordinator.getInstance().hasDeployRule(templateID, columnID));
		return response;
	}
}

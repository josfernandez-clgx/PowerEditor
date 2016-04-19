package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchNextRuleIDRequest;
import com.mindbox.pe.communication.LongResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.servlet.ServletActionException;

public class FetchNextRuleIDRequestHandler extends AbstractSessionRequestHandler<FetchNextRuleIDRequest> {

	public ResponseComm handleRequest(FetchNextRuleIDRequest request, HttpServletRequest httpservletrequest) throws ServletActionException {
		return new LongResponse(BizActionCoordinator.getInstance().getNextRuleID());
	}

}

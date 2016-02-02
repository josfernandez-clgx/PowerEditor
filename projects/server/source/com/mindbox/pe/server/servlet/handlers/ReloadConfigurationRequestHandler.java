package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ReloadConfigurationRequest;
import com.mindbox.pe.communication.ReloadConfigurationResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

public final class ReloadConfigurationRequestHandler extends AbstractActionRequestHandler<ReloadConfigurationRequest> {

	public ResponseComm handleRequest(ReloadConfigurationRequest reloadrequestcomm, HttpServletRequest httpservletrequest) throws ServerException {
		BizActionCoordinator.getInstance().reloadConfiguration();
		return new ReloadConfigurationResponse();
	}
}
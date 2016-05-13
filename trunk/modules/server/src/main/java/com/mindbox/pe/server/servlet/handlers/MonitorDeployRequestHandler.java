package com.mindbox.pe.server.servlet.handlers;

import static com.mindbox.pe.common.LogUtil.logDebug;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.MonitorDeployRequest;
import com.mindbox.pe.communication.MonitorDeployResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.cache.DeploymentManager;

public final class MonitorDeployRequestHandler extends AbstractSessionRequestHandler<MonitorDeployRequest> {

	@Override
	protected String getRequiredPrivilegeName(MonitorDeployRequest request) {
		return PrivilegeConstants.PRIV_DEPLOY;
	}

	public ResponseComm handleRequest(final MonitorDeployRequest request, final HttpServletRequest httpservletrequest) {
		final MonitorDeployResponse response = new MonitorDeployResponse(request.getGenerateRunId(), DeploymentManager.getInstance().monitor(request.getGenerateRunId()));
		logDebug(logger, "Stats for [%d] = %s", request.getGenerateRunId(), response.getGenerateStats());
		return response;
	}
}
package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.MonitorDeployRequest;
import com.mindbox.pe.communication.MonitorDeployResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.server.cache.DeploymentManager;

public final class MonitorDeployRequestHandler extends AbstractSessionRequestHandler<MonitorDeployRequest> {

	@Override
	protected String getRequiredPrivilegeName(MonitorDeployRequest request) {
		return PrivilegeConstants.PRIV_DEPLOY;
	}

	public ResponseComm handleRequest(MonitorDeployRequest monitordeployrequestcomm, HttpServletRequest httpservletrequest) {
		DeploymentManager deploymentmanager = DeploymentManager.getInstance();
		int i = monitordeployrequestcomm.getGenerateRunId();
		List<GenerateStats> stats = deploymentmanager.monitor(i);
		MonitorDeployResponse monitordeployresponsecomm = new MonitorDeployResponse(i, stats);
		return monitordeployresponsecomm;
	}
}
package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.communication.DeployRequest;
import com.mindbox.pe.communication.DeployResponse;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.generator.RuleGenerationException;

/**
 * Handler for {@link DeployRequest}.
 * 
 */
public final class DeployRequestHandler extends AbstractSessionRequestHandler<DeployRequest> {

	@Override
	protected String getRequiredPrivilegeName(DeployRequest request) {
		return PrivilegeConstants.PRIV_DEPLOY;
	}

	public ResponseComm handleRequest(DeployRequest deployRequest, HttpServletRequest httpservletrequest) {
		getUser(deployRequest.getUserID());
		final DeploymentManager deploymentManager = DeploymentManager.getInstance();
		final int id;
		try {
			id = DBIdGenerator.getInstance().nextSequentialID();
			logger.info("Generated id: " + id);
		}
		catch (SapphireException _ex) {
			return new ErrorResponse("ServerError", "Could not obtain new ID to generate.");
		}

		try {
			final boolean flag = !deploymentManager.deploy(id, deployRequest.getGuidelineReportFilter(), deployRequest.isExportPolicies(), deployRequest.getUserID());

			String deployDir = deploymentManager.getCurrentDeployDir();
			if (flag) {
				return new ErrorResponse("ServerError", "Another deployment is currently in progress!!! Please wait!");
			}
			else {
				return new DeployResponse(id, deployDir);
			}
		}
		catch (RuleGenerationException ex) {
			return new ErrorResponse("ServerError", ex.getMessage());
		}
	}
}
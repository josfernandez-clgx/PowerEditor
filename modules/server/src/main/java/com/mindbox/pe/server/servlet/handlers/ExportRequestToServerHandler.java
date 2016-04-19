package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ExportRequestToServer;
import com.mindbox.pe.communication.ExportRequestToServerResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.db.DBIdGenerator;

/**
 * Handles export request where export data is written to deploy directory on the server
 * @author Vineet Khosla
 * @since PowerEditor 5.0.0
 */
public class ExportRequestToServerHandler extends AbstractSessionRequestHandler<ExportRequestToServer> {


	public ResponseComm handleRequest(ExportRequestToServer request, HttpServletRequest httpservletrequest) {
		int i;
		try {
			i = DBIdGenerator.getInstance().nextSequentialID();
			logger.info("Generated id: " + i);
		}
		catch (SapphireException _ex) {
			return new ErrorResponse("ServerError", "Could not obtain new ID to generate.");
		}

		try {
			String fileName = request.getFileName();
			BizActionCoordinator.getInstance().writeExportXML(request.getGuidelineReportFilter(), fileName, request.getUserID());
			return new ExportRequestToServerResponse(i);
		}
		catch (Exception ex) {
			logger.error("Failed to export to Server", ex);
			return new ErrorResponse("ServerError", ex.getMessage());
		}
	}

}

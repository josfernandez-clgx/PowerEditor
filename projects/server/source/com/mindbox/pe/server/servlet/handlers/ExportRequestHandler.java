package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ByteArrayResponse;
import com.mindbox.pe.communication.ExportRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.imexport.ExportException;

/**
 * Export request handler.
 * @author Geneho Kim
 * @author MindBox
 */
public class ExportRequestHandler extends AbstractSessionRequestHandler<ExportRequest> {

	public ResponseComm handleRequest(ExportRequest request, HttpServletRequest httpservletrequest) {
		ByteArrayResponse response;
		try {
			response = new ByteArrayResponse(BizActionCoordinator.getInstance().fetchExportXML(
					request.getGuidelineReportFilter(),
					request.getUserID()));
			return response;
		}
		catch (IOException e) {
			logger.error("IO Error on export", e);
			return generateErrorResponse(new ExportException(e.getMessage()));
		}
		catch (ExportException e) {
			return generateErrorResponse(e);
		}
	}
}
package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ByteArrayResponse;
import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.FetchDomainDefinitionXMLRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class FetchDomainDefinitionXMLRequestHandler extends AbstractSessionRequestHandler<FetchDomainDefinitionXMLRequest> {

	public ResponseComm handleRequest(FetchDomainDefinitionXMLRequest request, HttpServletRequest httpservletrequest) {
		try {
			ByteArrayResponse response =
				new ByteArrayResponse(BizActionCoordinator.getInstance().fetchDomainDefinitionXML());
			return response;
		}
		catch (IOException ex) {
			logger.error("Failed to get domain XML", ex);
			return new ErrorResponse("msg.error.failure.get.xml.domain",ex.getMessage());
		}
	}
}

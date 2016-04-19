package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.GetNamedEntityRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SingleEntityResponse;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class GetNamedEntityRequestHandler extends AbstractSessionRequestHandler<GetNamedEntityRequest> {

	public ResponseComm handleRequest(GetNamedEntityRequest request, HttpServletRequest httpservletrequest) {
		Persistent object = SearchCooridinator.getInstance().retrieveEntity(request.getEntityType(), request.getName());
		SingleEntityResponse response = new SingleEntityResponse(object);
		return response;
	}

}

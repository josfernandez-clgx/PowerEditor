package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.NonSessionSearchRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;

public final class NonSessionSearchRequestHandler extends AbstractActionRequestHandler<NonSessionSearchRequest<?>> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseComm handleRequest(NonSessionSearchRequest<?> searchRequest, HttpServletRequest httpservletrequest)
			throws ServerException {
		SearchFilter<?> searchFilter = searchRequest.getSearchFilter();
		if (searchFilter == null) {
			return new ErrorResponse("InvalidRequestError", "Invalid Request: search filter is not specified.");
		}
		List<?> resultList = SearchCooridinator.getInstance().process(searchFilter, "");
		ListResponse listResponse = new ListResponse(resultList);
		return listResponse;
	}
}
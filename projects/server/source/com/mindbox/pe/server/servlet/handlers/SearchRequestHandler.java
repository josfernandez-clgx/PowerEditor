package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SearchRequest;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class SearchRequestHandler extends AbstractSessionRequestHandler<SearchRequest<?>> {


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseComm handleRequest(SearchRequest<?> request, HttpServletRequest httpservletrequest) throws ServerException {
		// TODO Kim: Replace this check with validator framework (OVal)
		SearchFilter<?> searchFilter = request.getSearchFilter();
		if (searchFilter == null) {
			return new ErrorResponse("InvalidRequestError", "Invalid Request: search filter is not specified.");
		}
		List<?> resultList = SearchCooridinator.getInstance().process(searchFilter, request.getUserID());
		ListResponse listResponse = new ListResponse(resultList);

		return listResponse;
	}

}

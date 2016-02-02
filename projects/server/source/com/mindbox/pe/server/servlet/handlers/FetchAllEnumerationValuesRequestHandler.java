package com.mindbox.pe.server.servlet.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchAllEnumerationValuesRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Handler for {@link FetchAllEnumerationValuesRequest}.
 *
 */
public class FetchAllEnumerationValuesRequestHandler extends AbstractSessionRequestHandler<FetchAllEnumerationValuesRequest> {

	@Override
	protected ResponseComm handleRequest(FetchAllEnumerationValuesRequest requestcomm, HttpServletRequest httpservletrequest)
			throws DataValidationFailedException, LockException, ServletActionException, ServerException {
		List<EnumValue> list = new ArrayList<EnumValue>();
		list.addAll(ConfigurationManager.getInstance().getEnumerationSourceConfigSet().getAllEnumValues(requestcomm.getSourceName()));
		ListResponse<EnumValue> response = new ListResponse<EnumValue>(list);
		if (logger.isDebugEnabled()) logger.debug("# of enum values = " + list.size());
		return response;
	}

}

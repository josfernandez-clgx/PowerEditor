package com.mindbox.pe.server.servlet.handlers;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchApplicableEnumerationValuesRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Handler for {@link FetchApplicableEnumerationValuesRequest}.
 *
 */
public class FetchApplicableEnumerationValuesRequestHandler extends AbstractSessionRequestHandler<FetchApplicableEnumerationValuesRequest> {

	@Override
	protected ResponseComm handleRequest(FetchApplicableEnumerationValuesRequest requestcomm, HttpServletRequest httpservletrequest)
			throws DataValidationFailedException, LockException, ServletActionException, ServerException {
		List<EnumValue> list = new LinkedList<EnumValue>();
		list.addAll(ConfigurationManager.getInstance().getEnumerationSourceConfigSet().getApplicableEnumValues(
				requestcomm.getSourceName(),
				requestcomm.getSelectorValue()));
		ListResponse<EnumValue> response = new ListResponse<EnumValue>(list);
		if (logger.isDebugEnabled())
			logger.debug(String.format("# of applicable enum values for %s = %d", requestcomm.getSelectorValue(), list.size()));
		return response;
	}
}

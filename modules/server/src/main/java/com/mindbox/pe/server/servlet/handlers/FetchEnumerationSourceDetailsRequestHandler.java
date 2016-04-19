package com.mindbox.pe.server.servlet.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.FetchEnumerationSourceDetailsRequest;
import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.ExternalEnumSourceDetail;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.EnumerationSourceConfigHelper;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Handler for {@link FetchEnumerationSourceDetailsRequest}.
 *
 */
public class FetchEnumerationSourceDetailsRequestHandler extends AbstractSessionRequestHandler<FetchEnumerationSourceDetailsRequest> {

	@Override
	protected ResponseComm handleRequest(FetchEnumerationSourceDetailsRequest requestcomm, HttpServletRequest httpservletrequest) throws DataValidationFailedException, LockException,
			ServletActionException, ServerException {
		List<ExternalEnumSourceDetail> detailList = new ArrayList<ExternalEnumSourceDetail>();
		EnumerationSourceConfigHelper enumerationSourceConfigSet = ConfigurationManager.getInstance().getEnumerationSourceConfigHelper();
		for (String name : enumerationSourceConfigSet.getEnumerationSourceNames()) {
			ExternalEnumSourceDetail detail = new ExternalEnumSourceDetail(name, enumerationSourceConfigSet.getEnumerationSource(name).isSelectorSupported());
			detailList.add(detail);
		}
		ListResponse<ExternalEnumSourceDetail> response = new ListResponse<ExternalEnumSourceDetail>(detailList);
		return response;
	}
}

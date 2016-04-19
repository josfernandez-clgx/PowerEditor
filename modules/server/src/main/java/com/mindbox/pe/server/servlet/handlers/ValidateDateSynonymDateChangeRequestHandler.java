package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.ValidateDateSynonymDateChangeRequest;
import com.mindbox.pe.communication.ValidateDateSynonymDateChangeResponse;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.servlet.ServletActionException;

public class ValidateDateSynonymDateChangeRequestHandler extends AbstractActionRequestHandler<ValidateDateSynonymDateChangeRequest> {

	@Override
	protected ResponseComm handleRequest(ValidateDateSynonymDateChangeRequest request, HttpServletRequest httpservletrequest) throws DataValidationFailedException, LockException,
			ServletActionException, ServerException {
		final List<GuidelineReportData> wouldBeInvalidGuidelines = BizActionCoordinator.getInstance().validateDateSynonymDateChange(
				request.getDateSynonymId(),
				request.getNewDate(),
				request.getUserID());
		final ValidateDateSynonymDateChangeResponse response = new ValidateDateSynonymDateChangeResponse(
				wouldBeInvalidGuidelines == null || wouldBeInvalidGuidelines.isEmpty(),
				wouldBeInvalidGuidelines);
		return response;
	}

}

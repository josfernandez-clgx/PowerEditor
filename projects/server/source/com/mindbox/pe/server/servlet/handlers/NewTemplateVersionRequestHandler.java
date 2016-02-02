package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.NewTemplateVersionRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveResponse;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.GridActionCoordinator;
import com.mindbox.pe.server.model.User;


/**
 * Handler for NewTemplateVersionRequest
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public final class NewTemplateVersionRequestHandler extends AbstractSessionRequestHandler<NewTemplateVersionRequest<?>> {

	public ResponseComm handleRequest(NewTemplateVersionRequest<?> request, HttpServletRequest httpservletrequest) throws ServerException {
		User user = getUser(request.getUserID());
		if (request.isForCommit()) {
			int id = GridActionCoordinator.getInstance().cutoverForAndStoreTemplate(
					request.getTemplateID(),
					request.getTemplate(),
					request.getDateSynonym(),
					user);
			return new SaveResponse(id);
		}
		else {
			return new ListResponse<List<GuidelineReportData>>(GridActionCoordinator.getInstance().retrieveCutoverGuidelinesForTemplate(
					request.getTemplateID(),
					request.getDateSynonym(),
					user.getUserID()));
		}
	}

}
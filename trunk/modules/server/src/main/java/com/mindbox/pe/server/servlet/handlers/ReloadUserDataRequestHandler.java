package com.mindbox.pe.server.servlet.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ListResponse;
import com.mindbox.pe.communication.ReloadUserDataRequest;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;

/**
 * Handler for {@link com.mindbox.pe.communication.ReloadUserDataRequest}.
 * @author Geneho Kim
 * @since PowerEditor 4.5.0
 */
public final class ReloadUserDataRequestHandler extends AbstractActionRequestHandler<ReloadUserDataRequest> {

	public ResponseComm handleRequest(ReloadUserDataRequest reloadrequestcomm, HttpServletRequest httpservletrequest) throws ServerException {
			List<UserData> list = BizActionCoordinator.getInstance().reloadUserData();
			return new ListResponse<UserData>(list);
	}
}
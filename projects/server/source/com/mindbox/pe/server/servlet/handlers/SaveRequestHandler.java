package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SaveRequest;
import com.mindbox.pe.communication.SaveResponse;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class SaveRequestHandler extends AbstractSessionRequestHandler<SaveRequest> {
	
	public ResponseComm handleRequest(SaveRequest request, HttpServletRequest httpservletrequest) throws ServletActionException, DataValidationFailedException {
		int entityID = processRequest(request.getPersistent(), request.isValidate(), getUser(request.getUserID()));
		logger.info("process request returned " + entityID);

		SaveResponse response = new SaveResponse(entityID);
		return response;
	}

	// TODO: davies, Aug 21, 2006: refactor: create map from concrete Persistent type to BizActionCoordinator save method.  Invoke with reflection.
	private int processRequest(Persistent object, boolean validate, User user) throws ServletActionException, DataValidationFailedException {

		if (object instanceof GenericCategory) {
			return BizActionCoordinator.getInstance().save((GenericCategory) object, validate, user);
		}
		if (object instanceof GenericEntity) {
			return BizActionCoordinator.getInstance().save((GenericEntity) object, validate, user);
		}
		if (object instanceof GenericEntityCompatibilityData) {
			BizActionCoordinator.getInstance().save((GenericEntityCompatibilityData) object, user);
			return 0;
		}
		if (object instanceof UserData) {
			User userData = User.valueOf((UserData) object);
			return BizActionCoordinator.getInstance().save(userData, user);
		}
		if (object instanceof Role) {
			return BizActionCoordinator.getInstance().save((Role) object, user);
		}
		if (object instanceof ParameterGrid) {
			return BizActionCoordinator.getInstance().save((ParameterGrid) object, user);
		}
		if (object instanceof Phase) {
			return BizActionCoordinator.getInstance().save((Phase) object, user);
		}
		if (object instanceof ProcessRequest) {
			return BizActionCoordinator.getInstance().save((ProcessRequest) object, user);
		}
		if (object instanceof GridTemplate) {
			return BizActionCoordinator.getInstance().save((GridTemplate) object, user);
		}
		if (object instanceof CBRCaseBase) {
			return BizActionCoordinator.getInstance().save((CBRCaseBase) object, user);
		}
		if (object instanceof CBRAttribute) {
			return BizActionCoordinator.getInstance().save((CBRAttribute) object, user);
		}
		if (object instanceof CBRCase) {
			return BizActionCoordinator.getInstance().save((CBRCase) object, user);
		}
		if (object instanceof DateSynonym) {
			return BizActionCoordinator.getInstance().save((DateSynonym) object, user);
		}
		if (object instanceof ActionTypeDefinition) {
			return BizActionCoordinator.getInstance().save((ActionTypeDefinition) object, user);
		}
		if (object instanceof TestTypeDefinition) {
			return BizActionCoordinator.getInstance().save((TestTypeDefinition) object, user);
		}
		throw new ServletActionException("InvalidRequestError", "Unsupported save request - " + object);
	}
}

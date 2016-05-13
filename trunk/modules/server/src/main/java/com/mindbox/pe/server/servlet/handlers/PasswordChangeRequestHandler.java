package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.PasswordChangeRequest;
import com.mindbox.pe.communication.PasswordChangeResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.spi.PasswordValidatorProvider;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

/**
 * All request for changing passwords are taken by this handler
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public class PasswordChangeRequestHandler extends AbstractActionRequestHandler<PasswordChangeRequest> {

	public ResponseComm handleRequest(PasswordChangeRequest pwChangeReq, HttpServletRequest httpservletrequest) throws ServerException {
		String userID = pwChangeReq.getLoginUserId();
		String oldPassword = pwChangeReq.getLoginPassword();
		String newPasswordAsClearText = pwChangeReq.getNewPasswordAsClearText();
		String confirmNewPasswordAsClearText = pwChangeReq.getConfirmNewPasswordAsClearText();

		LoginAttempt loginAttempt;
		try {
			loginAttempt = new LoginAttempt(userID, oldPassword);
		}
		catch (Exception e) {
			logger.error("Failed to change password", e);
			throw new ServerException(e.getMessage());
		}

		if (loginAttempt.failed()) {
			return PasswordChangeResponse.failureInstance(loginAttempt.getFailureReason());
		}

		if (!newPasswordAsClearText.equals(confirmNewPasswordAsClearText)) {
			return PasswordChangeResponse.failureInstance(ResourceUtil.getInstance().getResource("change.pwd.noMatch"));
		}
		String newPasswordAsOneWayHash = pwChangeReq.getNewPasswordAsOneWayHash();

		PasswordValidatorProvider passwordValidator = ServiceProviderFactory.getPasswordValidatorProvider();
		User user = SecurityCacheManager.getInstance().getUser(userID);
		if (!passwordValidator.isValidPassword(newPasswordAsClearText, newPasswordAsOneWayHash, user.getRecentPasswords())) {
			return PasswordChangeResponse.failureInstance(ResourceUtil.getInstance().getResource("msg.pwd.failed.validation"));
		}

		// TT-61: check if cannot change interval is up
		if (ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getCannotChangeIntervalMins() > 0) {
			if (user.getCurrentPasswordChangeDate() != null
					&& (System.currentTimeMillis() - user.getCurrentPasswordChangeDate().getTime()) <= 60L * 1000 * ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getCannotChangeIntervalMins()) {
				return PasswordChangeResponse.failureInstance(ResourceUtil.getInstance().getResource(
						"msg.pwd.cannot.be.changed",
						ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper().getCannotChangeIntervalMins()));
			}
		}

		// success, update user
		BizActionCoordinator.getInstance().updateUserPassword(userID, newPasswordAsOneWayHash, userID);

		return PasswordChangeResponse.successInstance();
	}

}

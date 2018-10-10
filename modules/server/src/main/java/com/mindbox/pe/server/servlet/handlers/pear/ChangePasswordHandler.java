package com.mindbox.pe.server.servlet.handlers.pear;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.ChangePasswordRequest;
import com.mindbox.pe.communication.pear.ChangePasswordResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.handlers.LoginAttempt;
import com.mindbox.pe.server.spi.PasswordValidatorProvider;
import com.mindbox.pe.server.spi.ServiceProviderFactory;

public class ChangePasswordHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(ChangePasswordHandler.class);

    public static ChangePasswordResponse process(ChangePasswordRequest request, HttpServletRequest servletRequest)
            throws Exception {
        LOG.debug("process(): request=" + request.toString());

        try {
            // Must successfully attempt to log in
            LoginAttempt loginAttempt = new LoginAttempt(request.username, request.oldPassword);
            if (loginAttempt.failed()) {
                return new ChangePasswordResponse(false, loginAttempt.getFailureReason());
            }

            User user = SecurityCacheManager.getInstance().getUser(request.username);

            // Must have a valid new password
            if (!request.ignoreValidation) {
                PasswordValidatorProvider passwordValidator = ServiceProviderFactory.getPasswordValidatorProvider();
                LOG.debug("process(): passwordValidator.getClass()=" + passwordValidator.getClass().getName());
                if (!passwordValidator.isValidPassword(request.newPassword, request.newPasswordHashed,
                        user.getRecentPasswords())) {
                    return new ChangePasswordResponse(false,
                            ResourceUtil.getInstance().getResource("msg.pwd.failed.validation"));
                }
            }

            // Must not violate no-change interval policy
            if (!request.ignoreChangeInterval) {
                int cannotChangeInterval = ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper()
                        .getCannotChangeIntervalMins();
                if (cannotChangeInterval > 0) {
                    if (user.getCurrentPasswordChangeDate() != null
                            && (System.currentTimeMillis() - user.getCurrentPasswordChangeDate().getTime()) <= 60L
                                    * 1000 * cannotChangeInterval) {
                        return new ChangePasswordResponse(false,
                                ResourceUtil.getInstance().getResource("msg.pwd.cannot.be.changed",
                                        ConfigurationManager.getInstance().getUserPasswordPoliciesConfigHelper()
                                                .getCannotChangeIntervalMins()));
                    }
                }
            }

            // success, update user
            BizActionCoordinator.getInstance().updateUserPassword(request.username, request.newPasswordHashed,
                    request.username);
            return new ChangePasswordResponse(true, null);

        } catch (Exception e) {
            LOG.error("process()", e);
            throw new ServerException(e.getMessage());
        }
    }

}

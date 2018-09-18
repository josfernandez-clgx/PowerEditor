package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;

public class LoginUserResponse extends Response {
    private static final long serialVersionUID = 836378051590328928L;
    private static final Logger LOG = Logger.getLogger(LoginUserResponse.class);

    public final boolean failed;
    public final String failureReason;
    public final int daysUntilPasswordExpires;
    public final boolean passwordChangeRequired;
    public final boolean passwordExpirationNotificationRequired;
    public final String sessionID;

    public LoginUserResponse(boolean failed, String failureReason, int daysUntilPasswordExpires, boolean passwordChangeRequired, boolean passwordExpirationNotificationRequired, String sessionID) {
        this.failed = failed;
        this.failureReason = failureReason;
        this.daysUntilPasswordExpires = daysUntilPasswordExpires;
        this.passwordChangeRequired = passwordChangeRequired;
        this.passwordExpirationNotificationRequired = passwordExpirationNotificationRequired;
        this.sessionID = sessionID;
        LOG.trace("LoginUserResponse() this=" + this.toString());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LoginUserResponse[");
        UtilBase.nameEqualsValue(builder, "failed", failed);
        UtilBase.nameEqualsValue(builder, ", failureReason", failureReason);
        UtilBase.nameEqualsValue(builder, ", passwordChangeRequired", passwordChangeRequired);
        UtilBase.nameEqualsValue(builder, ", passwordExpirationNotificationRequired", passwordExpirationNotificationRequired);
        UtilBase.nameEqualsValue(builder, ", sessionID", sessionID);
        builder.append("]");
        return builder.toString();
    }
}

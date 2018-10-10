package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;

public class VerifyUserResponse extends Response {

    private static final long serialVersionUID = -6576832233425975236L;
    private static final Logger LOG = Logger.getLogger(VerifyUserResponse.class);

    public final boolean failed;
    public final String failureReason;
    public final int daysUntilPasswordExpires;
    public final boolean passwordChangeRequired;
    public final boolean passwordExpirationNotificationRequired;

    public VerifyUserResponse(boolean failed, String failureReason, int daysUntilPasswordExpires, boolean passwordChangeRequired, boolean passwordExpirationNotificationRequired) {
        this.failed = failed;
        this.failureReason = failureReason;
        this.daysUntilPasswordExpires = daysUntilPasswordExpires;
        this.passwordChangeRequired = passwordChangeRequired;
        this.passwordExpirationNotificationRequired = passwordExpirationNotificationRequired;
        LOG.trace("VerifyUserResponse() this=" + this.toString());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VerifyUserResponse[");
        UtilBase.nameEqualsValue(builder, "failed", failed);
        UtilBase.nameEqualsValue(builder, ",failureReason", failureReason);
        UtilBase.nameEqualsValue(builder, ", passwordChangeRequired", passwordChangeRequired);
        UtilBase.nameEqualsValue(builder, ",passwordExpirationNotificationRequired", passwordExpirationNotificationRequired);
        builder.append("]");
        return builder.toString();
    }
}

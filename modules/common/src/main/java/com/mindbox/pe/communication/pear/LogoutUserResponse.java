package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;

public class LogoutUserResponse extends Response {
    private static final long serialVersionUID = -3056571825026653276L;
    private static final Logger LOG = Logger.getLogger(LogoutUserResponse.class);

    public final boolean failed;
    public final String failureReason;

    public LogoutUserResponse(boolean failed, String failureReason) {
        this.failed = failed;
        this.failureReason = failureReason;
        LOG.trace("LogoutUserResponse() this=" + this.toString());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LogoutUserResponse[");
        UtilBase.nameEqualsValue(builder, "failed", failed);
        UtilBase.nameEqualsValue(builder, ", failureReason", failureReason);
        builder.append("]");
        return builder.toString();
    }
}

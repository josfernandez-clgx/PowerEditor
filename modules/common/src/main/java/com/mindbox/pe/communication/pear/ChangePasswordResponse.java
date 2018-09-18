package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;
import com.mindbox.pe.common.UtilBase;

public class ChangePasswordResponse extends Response {

    private static final long serialVersionUID = 2802172926563633583L;

    private static final Logger LOG = Logger.getLogger(ChangePasswordResponse.class);

    public final boolean success;
    public final String message;

    public ChangePasswordResponse(final boolean success, final String message) {
        this.success = success;
        this.message = message;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ChangePasswordResponse[");
        UtilBase.nameEqualsValue(builder, "success", success);
        UtilBase.nameEqualsValue(builder, ",message", message);
        builder.append("]");
        return builder.toString();
    }
}

package com.mindbox.pe.communication.pear;

import com.mindbox.pe.common.UtilBase;

public class LogoutUserRequest extends Request<LogoutUserResponse> {
    private static final long serialVersionUID = -5759847839246944038L;

    public final String sessionID;
    public final String username;

    public LogoutUserRequest(String sessionID, String username) {
        this.sessionID = sessionID;
        this.username = username;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("LogoutUserRequest[");
        UtilBase.nameEqualsValue(builder, "sessionID", sessionID);
        UtilBase.nameEqualsValue(builder, ", username", username);
        builder.append(']');
        return builder.toString();
    }
}

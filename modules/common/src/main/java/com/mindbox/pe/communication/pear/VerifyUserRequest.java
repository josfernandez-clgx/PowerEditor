package com.mindbox.pe.communication.pear;

import com.mindbox.pe.common.UtilBase;

public class VerifyUserRequest extends Request<VerifyUserResponse> {

    private static final long serialVersionUID = -6831713898077656110L;

    public final String username;
    public final String password;

    public VerifyUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("VerifyUserRequest[");
        UtilBase.nameEqualsValue(builder, "username", username);
        UtilBase.nameEqualsValue(builder, ",password", password);
        builder.append(']');
        return builder.toString();
    }
}

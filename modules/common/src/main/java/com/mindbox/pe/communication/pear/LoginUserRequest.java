package com.mindbox.pe.communication.pear;

import com.mindbox.pe.common.UtilBase;

public class LoginUserRequest extends Request<LoginUserResponse> {
    private static final long serialVersionUID = -7298433494111186740L;
    
    private final String username;
    private final String password;

    public LoginUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("LoginUserRequest[");
        UtilBase.nameEqualsValue(builder, "username", username);
        builder.append(']');
        return builder.toString();
    }

    public String toStringWithPassword()
    {
        StringBuilder builder = new StringBuilder("LoginUserRequest[");
        UtilBase.nameEqualsValue(builder, "username", username);
        UtilBase.nameEqualsValue(builder, ",password", password);
        builder.append(']');
        return builder.toString();
    }
}

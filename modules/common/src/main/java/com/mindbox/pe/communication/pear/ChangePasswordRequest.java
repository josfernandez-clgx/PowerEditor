package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.PasswordOneWayHashUtil;
import com.mindbox.pe.common.UtilBase;

public class ChangePasswordRequest extends Request<ChangePasswordResponse> {

    private static final long serialVersionUID = -2027034192495917874L;
    private static final Logger LOG = Logger.getLogger(ChangePasswordRequest.class);

    public final String username;
    public final String oldPassword;
    public final String newPassword;
    public final String newPasswordHashed;
    public final boolean ignoreValidation;
    public final boolean ignoreChangeInterval;

    public ChangePasswordRequest(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordHashed = PasswordOneWayHashUtil.convertToOneWayHash(newPassword, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
        ignoreValidation = false;
        ignoreChangeInterval = false;
    }

    public ChangePasswordRequest(String username, String oldPassword, String newPassword, boolean ignoreValidation, boolean ignoreChangeInterval) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordHashed = PasswordOneWayHashUtil.convertToOneWayHash(newPassword, PasswordOneWayHashUtil.HASH_ALGORITHM_MD5);
        this.ignoreValidation = ignoreValidation;
        this.ignoreChangeInterval = ignoreChangeInterval;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("ChangePasswordRequest[");
        UtilBase.nameEqualsValue(builder, "username", username);
        UtilBase.nameEqualsValue(builder, ",oldPassword", oldPassword);
        UtilBase.nameEqualsValue(builder, ",newPassword", newPassword);
        UtilBase.nameEqualsValue(builder, ",newPasswordHashed", newPasswordHashed);
        builder.append(']');
        return builder.toString();
    }
}

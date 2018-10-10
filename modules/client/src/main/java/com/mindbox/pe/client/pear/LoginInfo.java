package com.mindbox.pe.client.pear;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;

public class LoginInfo {

    public final String peUrl; // URL for PowerEditor web application
    public final String peServletUrl; // URL for PowerEditor servlet
    public final String pearServletUrl; // URL for PEAR servlet

    public PowerEditorInfoResponse peInfo = null;
    public String username = null;
    public String sessionID = null;

    public LoginInfo(String peUrl, String peServletUrl, String pearServletUrl) {
        this.peUrl = peUrl;
        this.peServletUrl = peServletUrl;
        this.pearServletUrl = pearServletUrl;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("LoginInfo[");
        UtilBase.nameEqualsValue(builder, ",peUrl", peUrl);
        UtilBase.nameEqualsValue(builder, ",peServletUrl", peServletUrl);
        UtilBase.nameEqualsValue(builder, ",pearServletUrl", pearServletUrl);
        UtilBase.nameEqualsValue(builder, ",peInfo", peInfo);
        UtilBase.nameEqualsValue(builder, ",username", username);
        UtilBase.nameEqualsValue(builder, ",sessionID", sessionID);
        builder.append(']');
        return builder.toString();
    }
}

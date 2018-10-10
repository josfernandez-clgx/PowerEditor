package com.mindbox.pe.communication.pear;

import javax.swing.ImageIcon;

import com.mindbox.pe.common.UtilBase;

public class PowerEditorInfoResponse extends Response {

    private static final long serialVersionUID = -7017866041840121140L;

    public final String accessWarning;
    public final String clientWindowTitle;
    public final ImageIcon mindboxIcon;
    public final String passwordRequirements;
    public final String version;

    public PowerEditorInfoResponse(String accessWarning, String clientWindowTitle, ImageIcon mindboxIcon, String passwordRequirements, String version) {
        this.accessWarning = accessWarning;
        this.clientWindowTitle = clientWindowTitle;
        this.mindboxIcon = mindboxIcon;
        this.passwordRequirements = passwordRequirements;
        this.version = version;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PowerEditorInfoResponse[");
        UtilBase.nameEqualsValue(builder, "accessWarning", accessWarning);
        UtilBase.nameEqualsValue(builder, ",clientWindowTitle", clientWindowTitle);
        UtilBase.nameEqualsValue(builder, ",mindboxIcon", mindboxIcon);
        UtilBase.nameEqualsValue(builder, ",passwordRequirements", passwordRequirements);
        UtilBase.nameEqualsValue(builder, ",version", version);
        builder.append(']');
        return builder.toString();
    }
}

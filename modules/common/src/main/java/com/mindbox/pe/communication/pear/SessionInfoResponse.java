package com.mindbox.pe.communication.pear;

import com.mindbox.pe.common.UtilBase;

public class SessionInfoResponse extends Response {

    private static final long serialVersionUID = 3338148569446251898L;

    public final int current;
    public final int maximum;
    public final String map;

    public SessionInfoResponse(int current, int maximum, String map) {
        this.current = current;
        this.maximum = maximum;
        this.map = map;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("SessionInfoResponse[");
        UtilBase.nameEqualsValue(builder, "current", current);
        UtilBase.nameEqualsValue(builder, ",maximum", maximum);
        UtilBase.nameEqualsValue(builder, ",map", map);
        builder.append("]");
        return builder.toString();
    }
}

package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

public class Request<ResponseType extends Response> extends Serializer<ResponseType> {

    private static final long serialVersionUID = -3624976504973173867L;
    private static final Logger LOG = Logger.getLogger(Request.class);

    public Request() {
        LOG.trace("Request()");
    }
}

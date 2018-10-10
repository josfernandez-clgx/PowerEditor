package com.mindbox.pe.communication.pear;

import org.apache.log4j.Logger;

public class Response extends Serializer<Response> {

    private static final long serialVersionUID = 7530797437076641665L;
    private static final Logger LOG = Logger.getLogger(Response.class);

    public Response() {
        LOG.trace("Response()");
    }
}

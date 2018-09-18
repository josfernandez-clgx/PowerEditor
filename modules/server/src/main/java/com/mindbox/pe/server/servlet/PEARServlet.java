package com.mindbox.pe.server.servlet;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.Request;
import com.mindbox.pe.communication.pear.Response;
import com.mindbox.pe.communication.pear.Serializer;
import com.mindbox.pe.server.servlet.handlers.pear.Handler;

public class PEARServlet extends HttpServlet {
    private static final long serialVersionUID = 8012052725138245990L;
    private static final Logger LOG = Logger.getLogger(PEARServlet.class);

    public PEARServlet() {
        super();
        LOG.debug("PEARServlet()");
    }

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            LOG.info("doPost(): httpRequest=" + httpRequest.toString() + ", httpResponse=" + httpResponse.toString());
            Request<?> request = (Request<?>) Serializer.deserialize(httpRequest.getInputStream());
            Method method = Handler.findHandler(request);
            LOG.info("doPost(): request=" + request.toString());
            Response response = (Response) method.invoke(null, request, httpRequest);
            response.serialize(httpResponse.getOutputStream());
        } catch (Exception e) {
            LOG.error("doPost()", e);
        }
    }

    @Override
    public void init() {
        LOG.debug("init()");
    }
}

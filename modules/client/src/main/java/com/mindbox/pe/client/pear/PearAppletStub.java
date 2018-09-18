package com.mindbox.pe.client.pear;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PearAppletStub implements AppletStub {
    private static final Logger LOG = Logger.getLogger(PearAppletStub.class);

    private Properties parameters = new Properties();
    private String rootServer = null;
    private URL rootURL = null;

    public PearAppletStub(String server) throws MalformedURLException {
        this.rootServer = server;
        this.rootURL = new URL(server);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void appletResize(int arg0, int arg1) {
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public URL getCodeBase() {
        return rootURL;
    }

    @Override
    public URL getDocumentBase() {
        return rootURL;
    }

    @Override
    public String getParameter(String name) {
        if (null == name) return null;
        return parameters.getProperty(name);
    }

    public Properties getParameters() {
        return parameters;
    }

    public void setParameter(String name, String value) {
        parameters.setProperty(name, value);
    }

}

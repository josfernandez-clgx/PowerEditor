package com.mindbox.pe.server.servlet;

import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.LoginRequest;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.SapphireComm;
import com.mindbox.pe.communication.SessionRequest;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.ServerControl;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

/**
 * PowerEditor Servlet.
 *
 * @author Geneho Kim
 * @since PowerEditor 1.0
 */
public class PowerEditorServlet extends HttpServlet {

    private static final long serialVersionUID = -5642092409070959267L;
    private static final Logger LOG = Logger.getLogger(PowerEditorServlet.class);

    private static final boolean INVALIDATE_UPON_NEW_SESSION = true;
    private static final String BYPASS_RESTART_CHECK = "pe.bypassRestartCheck";

    private Date lastActivityDate;

    @Override
    public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException {
        SapphireComm<?> responseObj = null;
        try {
            echoAll("-----------------------------------------------------------------");
            echoAll("[PowerEditorServlet.doPost] POST Request received @ %s", getTimeDate());
            echoAll("[PowerEditorServlet.doPost] Time since last activity = %s", timeSinceLastActivity());
            echoAll("-----------------------------------------------------------------");

            final HttpSession httpSession = httpservletrequest.getSession();

            // check server status
            if (ServerControl.isServerReloading()) {
                responseObj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.reloading",
                        null);
            } else if (ServerControl.isServerStopped()) {
                responseObj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.stopped",
                        null);
            }
            // check relogin
            else if (SessionManager.getInstance().needsRelogin(httpSession.getId())) {
                responseObj = new ErrorResponse(ErrorResponse.SERVER_RESTARTED_ERROR, "msg.warning.server.refresh",
                        null);
            } else {
                final RequestComm<?> requestcomm = (RequestComm<?>) SapphireComm
                        .serializeInUnchecked(httpservletrequest.getInputStream());
                if (requestcomm == null) {
                    echoAll("Could not serialize input stream...");
                    throw new ServletException("Could not serialize input stream...");
                }

                if (requestcomm instanceof LoginRequest) {
                    httpSession.setAttribute(BYPASS_RESTART_CHECK, Boolean.TRUE);
                }

                if (httpSession.getAttribute(BYPASS_RESTART_CHECK) == null
                        && httpSession.getCreationTime() < AppContextListener.getServerStartDate().getTime()) {
                    echoAll("Session was created before servlet restarted! Session Start at: %s; Server started at: %s",
                            printDate(httpSession.getCreationTime()),
                            AppContextListener.getServerStartDate().toString());
                    responseObj = new ErrorResponse("ServerRestartError", "");
                } else {
                    if (httpSession.isNew() && INVALIDATE_UPON_NEW_SESSION) {
                        echoAll("NEW SESSION received with id = [%s]", httpSession.getId());

                        // HTTP-Only cookie: Check if the specified session is valid; then do not
                        // terminate session!
                        if (SessionRequest.class.isInstance(requestcomm)) {
                            final String sessionIdToUse = SessionRequest.class.cast(requestcomm).getSessionID();
                            echoAll("Checking new session [%s] with [session-id=%s] ", httpSession.getId(),
                                    sessionIdToUse);

                            if (!SessionManager.getInstance().hasSession(sessionIdToUse)) {
                                SessionManager.getInstance().terminateSession(httpSession.getId());
                            }
                        } else {
                            SessionManager.getInstance().terminateSession(httpSession.getId());
                        }
                    }

                    if (SessionRequest.class.isInstance(requestcomm)) {
                        final String sessionIdToUse = SessionRequest.class.cast(requestcomm).getSessionID();
                        if (SessionManager.getInstance().isSessionExpired(sessionIdToUse)) {
                            logInfo(LOG, "session [%s] has expired; terminating the session.", sessionIdToUse);
                            BizActionCoordinator.getInstance().performLogoff(sessionIdToUse, SecurityCacheManager
                                    .getInstance().getUser(SessionRequest.class.cast(requestcomm).getUserID()));
                        } else {
                            SessionManager.getInstance().resetLastAccessedTime(sessionIdToUse);
                        }
                    }

                    responseObj = processInput(requestcomm, httpservletrequest);
                    echoAll("Exiting doPost, sessionId = %s", getSessionId(httpservletrequest));
                }
            }
        } catch (Exception exception) {
            responseObj = logError(exception, "Exception servicing POST request: " + exception.toString());
        } catch (Error error) {
            responseObj = logError(error, "Error servicing POST request: " + error.toString());
        }

        logInfo(LOG, "serializing back to client: %s", responseObj);

        // Serialize response
        responseObj.serializeOut(new BufferedOutputStream(httpservletresponse.getOutputStream()));
    }

    @Override
    public void init() throws ServletException {
        servletLogDebug();
        String hostname = getHostname();
        LOG.debug("init() hostname=" + getHostname());
        String powerEditorURI = computePowerEditorURI(hostname);
        LOG.info("init() powerEditorURI=" + powerEditorURI);
        createJNLP(powerEditorURI);
    }

    private String computePowerEditorURI(String hostname) throws ServletException {
        List<String> serverURLs = computeServerURLs(hostname);
        if (serverURLs.isEmpty()) {
            String message = "computePowerEditorURI() found no server URLs";
            LOG.error(message);
            throw new ServletException(message);
        }
        int countURLs = serverURLs.size();
        if (1 != countURLs) {
            LOG.info("computerPowerEditorURI() using first of " + Integer.toString(countURLs) + " URLs");
        }
        String result = serverURLs.get(0) + getServletContext().getContextPath();
        LOG.debug("computePowerEditorURI() returns " + result);
        return result;
    }

    private List<String> computeServerURLs(String hostname) {
        List<String> result = new ArrayList<String>();
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            QueryExp subQuery1 = Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"));
            QueryExp subQuery2 = Query.anySubString(Query.attr("protocol"), Query.value("Http11"));
            QueryExp query = Query.or(subQuery1, subQuery2);
            Set<ObjectName> objects = server.queryNames(new ObjectName("*:type=Connector,*"), query);
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            for (Iterator<ObjectName> i = objects.iterator(); i.hasNext();) {
                ObjectName object = i.next();
                String scheme = server.getAttribute(object, "scheme").toString();
                String port = object.getKeyProperty("port");
                for (InetAddress address : addresses) {
                    if (address.isMulticastAddress() || (address instanceof Inet6Address)) {
                        continue;
                    }
                    result.add(scheme + "://" + hostname + ":" + port);
                }
            }
        } catch (Exception e) {
            LOG.error("init()", e);
        }
        return result;
    }

    private void createJNLP(String powerEditorUrl) {
        String debugHeader = "createJNLP\"(" + powerEditorUrl + "\")";
        LOG.debug(debugHeader);
        try {
            String jnlpFile = getServletContext().getRealPath(".") + File.separator + "PowerEditor.jnlp";
            LOG.debug(debugHeader + " creating " + jnlpFile);
            PrintStream jnlpStream = new PrintStream(jnlpFile);
            jnlpStream.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            jnlpStream.format("<jnlp spec=\"1.0+\" codebase=\"%s\" href=\"PowerEditor.jnlp\">\n", powerEditorUrl);
            jnlpStream.format("\n");
            jnlpStream.format("\t<information>\n");
            jnlpStream.format("\t\t<title>PowerEditor Client GUI</title>\n");
            jnlpStream.format("\t\t<vendor>CoreLogic/MindBox</vendor>\n");
            jnlpStream.format("\t\t<homepage href=\"%s\" />\n", powerEditorUrl);
            jnlpStream.format("\t\t<description>PowerEditor client GUI</description>\n");
            jnlpStream.format("\t</information>\n");
            jnlpStream.format("\n");
            jnlpStream.format("\t<security>\n");
            jnlpStream.format("\t\t<all-permissions/>\n");
            jnlpStream.format("\t</security>\n");
            jnlpStream.format("\n");
            jnlpStream.format("\t<resources>\n");
            jnlpStream.format("\t\t<j2se version=\"1.6+\" />\n");
            jnlpStream.format("\t\t<jar href=\"PowerEditor.jar\" />\n");
            jnlpStream.format("\t</resources>\n");
            jnlpStream.format("\n");
            jnlpStream.format("\t<application-desc main-class=\"com.mindbox.pe.client.pear.MainGUI\">\n");
            jnlpStream.format("\t\t<argument>%s</argument>\n", powerEditorUrl);
            jnlpStream.format("\t\t<argument>%s/PowerEditorServlet</argument>\n", powerEditorUrl);
            jnlpStream.format("\t\t<argument>%s/PEARServlet</argument>\n", powerEditorUrl);
            jnlpStream.format("\t</application-desc>\n");
            jnlpStream.format("</jnlp>\n");
            jnlpStream.close();
        } catch (Exception e) {
            LOG.error(debugHeader, e);
        }
    }

    private void echoAll(String msg, Object... args) {
        if (LOG.isInfoEnabled()) {
            try {
                LOG.info(String.format(msg, args));
            } catch (Exception e) {
                LOG.info(String.format("LOG-FAILED:message=[%s], args=%s", msg, args), e);
            }
        }
    }

    private String getHostname() {
        String name = ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getHostname();
        LOG.debug("getHostname() ConfigurationManager.getInstance().getPowerEditorConfiguration().getServer().getHostname()=" + name);
        if (null == name) {
            try {
                name = InetAddress.getLocalHost().getHostName();
                LOG.debug("getHostname() InetAddress.getLocalHost().getHostName()=" + name);
            } catch (UnknownHostException e) {
                LOG.error("getHostname()", e);
            }
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    private void servletLogDebug() {
        String parameterName;
        Enumeration<String> parameterNames;

        parameterNames = getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            parameterName = parameterNames.nextElement();
            LOG.debug("init() getInitParameter(\"" + parameterName + "\")=" + getInitParameter(parameterName));
        }

        ServletConfig config = getServletConfig();
        parameterNames = config.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            parameterName = parameterNames.nextElement();
            LOG.debug("init() getServletConfig().getInitParameter(\"" + parameterName + "\")="
                    + config.getInitParameter(parameterName));
        }
        LOG.debug("init() getServletConfig().getServletName()=" + config.getServletName());

        ServletContext context = getServletContext();
        LOG.debug("init() getServletContext().getContextPath()=" + context.getContextPath());
        parameterNames = context.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            parameterName = parameterNames.nextElement();
            LOG.debug("init() getServletContext().getInitParameter(\"" + parameterName + "\")="
                    + context.getInitParameter(parameterName));
        }
        LOG.debug("init() getServletContext().getRealPath(\"/\")=" + context.getRealPath("/"));
        LOG.debug("init() getServletContext().getRealPath(\".\")=" + context.getRealPath("."));
        LOG.debug("init() getServletContext().getServerInfo()=" + context.getServerInfo());
        LOG.debug("init() getServletContext().getServletContextName()=" + context.getServletContextName());
    }

    private String getSessionId(HttpServletRequest httpservletrequest) {
        echoAll(">> Calling getSessionId()...");
        HttpSession httpsession = httpservletrequest.getSession(false);
        if (httpsession == null) {
            echoAll("No session associated with request");
            return null;
        } else {
            String s = httpsession.getId();
            echoAll(">> getSessionId() returned..." + s);
            return s;
        }
    }

    private String getTimeDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(11) + ":" + calendar.get(12) + ":" + calendar.get(13) + " on " + (calendar.get(2) + 1) + "-"
                + calendar.get(5) + "-" + calendar.get(1);
    }

    private ResponseComm logError(final Throwable exception, final String message) {
        LOG.error(message, exception);
        return new ErrorResponse("ServiceError", message);
    }

    private String printDate(long l) {
        return (new Date(l)).toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ResponseComm processInput(RequestComm<?> requestcomm, HttpServletRequest httpservletrequest) {
        Object obj = null;
        echoAll(">> PowerEditorServlet.processInput() ");
        try {
            IRequestCommHandler handler = HandlerFactory.getHandler(requestcomm);
            echoAll("  using handler = %s", handler);
            obj = handler.serviceRequest(requestcomm, httpservletrequest);
        } catch (Exception exception) {
            LOG.error("Failed to process request " + requestcomm, exception);
            obj = new ErrorResponse(ErrorResponse.UNKNOWN_ERROR, exception.toString());
        }
        echoAll("<< PowerEditorServlet.processInput() with %s", obj);
        return ((ResponseComm) (obj));
    }

    private String timeSinceLastActivity() {
        long l = -1L;
        Date date = new Date();
        if (lastActivityDate != null) {
            l = date.getTime() - lastActivityDate.getTime();
        }
        lastActivityDate = date;
        if (l == -1L) {
            return "n/a";
        } else {
            return "" + (double) l / 60000D + " mins.";
        }
    }
}

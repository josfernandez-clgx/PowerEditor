<%@ page session="true"%>

<%@ page import="java.io.*,
                 com.crystaldecisions.report.web.viewer.CrystalReportViewer,
                 com.crystaldecisions.sdk.occa.report.reportsource.IReportSource,
                 com.crystaldecisions.xml.serialization.*" %>
<%
String CLOSED_RPT_ID = "closedreportid";
String RPT_ID = "reportid";
String RPT_SOURCE = "reportsource";

String closeRptID = request.getParameter(CLOSED_RPT_ID);
String reportID = request.getParameter(RPT_ID);
String serializedRptSrc = request.getParameter(RPT_SOURCE);

if (closeRptID != null && closeRptID.length() > 0)
{
    // Clear the report source in session with the given closedreportid
    session.removeAttribute(closeRptID);
}

if (reportID != null && reportID.length() > 0)
{
    CrystalReportViewer viewer = new CrystalReportViewer();
    viewer.setName("htmlpreview");
    viewer.setHasRefreshButton(false);
    viewer.setHasExportButton(false);
    viewer.setHasPrintButton(false);
    viewer.setOwnForm(true);
    viewer.setOwnPage(true);

    IReportSource reportSource = null;

    if (serializedRptSrc != null && serializedRptSrc.length() > 0)
    {
        // Got a seralizedRprtSrc string
        // need to deserialize the report source string and pass to viewer
        byte[] byteRptSrc = serializedRptSrc.getBytes();
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteRptSrc);

        try
        {
            XMLObjectSerializer serializer = new XMLObjectSerializer();
            SaveOption saveOpt = serializer.getSaveOption();
            saveOpt.setExcludeNullObjects(true);
            // Enabled since server is ready.
            saveOpt.setSkipWritingIdenticalObject(true);

            reportSource = (IReportSource)serializer.load(byteInStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            out.write("Error deserializing report source.");
            return;
        }
        finally
        {
            byteInStream.close();
        }
        viewer.setReportSource(reportSource);
        session.setAttribute(reportID, viewer.getReportSource());
    }
    else
    {
        // Try to load report source from session
        reportSource = (IReportSource) session.getAttribute(reportID);
        if (reportSource == null)
        {
            out.write("<script language=\"javascript\" src=\"js/previewerror.js\"></script>");
            out.write("<script>writeError(L_SessionExpired, L_PleaseRefresh);</script>");
            return;   
        }
        viewer.setReportSource(reportSource);
    }
    viewer.setURI(request.getRequestURI() + "?" + RPT_ID + "=" + reportID);
    viewer.processHttpRequest(request, response, application, null);
}
%>



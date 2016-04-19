<%@ include file="/includes/global.jsp" %>

<c:set var="ssid" value="${param.ssid}" />
<c:set var="width" value="${param.w}" />
<c:set var="height" value="${param.h}" />
<c:set var="etc" value="${param.etc}" />
<pe:extract-userid-from-session var="userId" sessionId="${ssid}"/>

<c:choose>
	<c:when test="${empty ssid or empty userId or empty width or empty height}">
		<c:redirect url="/invalid_request.jsp"/>
	</c:when>
	
	<c:otherwise>	
		<pe:get-configuration var="configuration" serverLogVar="serverLogConfig" />
		<c:set var="userInterfaceConfig" value="${configuration.powerEditorConfiguration.userInterface}" />
		<c:set var="logoutUrl" value="${configuration.logoutUrlToUse}" />
		<c:url var="contextPath" value="/" />
		<c:set var="archivePaths" value="PowerEditor.jar" />
		<c:set var="servletPath" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${contextPath}PowerEditorServlet"/>

<html>
<head>
<meta http-equiv="Pragma" content="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
<title><pe:write-application-title full="true"/>&nbsp;-&nbsp;signed in as <pe:write-user-display-name userId="${userId}"/></title>
<SCRIPT type="text/javascript" language="Javascript">
function closeIt() {
	var msg = "Unsaved PowerEditor work will be lost if you continue and navigate away from this page.";
	if (window.event) { // IE
		event.returnValue = msg;
	}
	else {
		alert(msg);
		return true;
	}
}

function processUnload() {
	opener.location='<c:out value="${logoutUrl}"/>';
}
</SCRIPT>
</head>
<%-- Note: onbeforeunload is IE-specific event --%>
<body style='margin-top:0px;margin-left:0px;margin-right:0px;margin-bottom:0px;' onunload='processUnload();' onbeforeunload="return closeIt();">
<table cellpadding='0' cellspacing='0' border='0' width='100%'>
<tbody>
	<tr>
		<td width='100%%'>
			<OBJECT classid='clsid:8AD9C840-044E-11D1-B3E9-00805F499D93' 
			        WIDTH='<c:out value="${width}"/>' HEIGHT='<c:out value="${height}"/>'
			        codebase='http://java.sun.com/products/plugin/autodl/jinstall-1_6_0-win.cab#Version=1,6,0,0'>
				<param name='code' value='com.mindbox.pe.client.applet.PowerEditorLoggedApplet.class'>
				<param name='archive' value='<c:out value="${archivePaths}"/>'>
				<param name='server' value='<c:out value="${servletPath}"/>'>
				<param name='bgColor' value='16777215'>
				<param name='hlColor' value='16763904'>
				<param name='width' value='<c:out value="${width}"/>'>
				<param name='height' value='<c:out value="${height}"/>'>
				<param name='type' value='application/x-java-applet;version=1.6'>
				<param name='scriptable' value='false'>
				<param name='ssid' value='<c:out value="${ssid}"/>'>
				<param name='logoffURL' value='<c:out value="${logoutUrl}"/>'>
				<c:if test="${not empty userInterfaceConfig.clientJavaOptions}">
					<param name="java_arguments" value='<c:out value="${userInterfaceConfig.clientJavaOptions}"/>'>
				</c:if>
				<c:if test="${not empty userInterfaceConfig.lookAndFeelValue}">
					<param name="lookAndFeel" value='<c:out value="${userInterfaceConfig.lookAndFeelValue}"/>'>
				</c:if>
				<COMMENT>
					<EMBED type='application/x-java-applet;version=1.6'
					       code='com.mindbox.pe.client.applet.PowerEditorLoggedApplet.class'
					       archive='<c:out value="${archivePaths}"/>'
					       server='<c:out value="${servletPath}"/>' 
					       width='<c:out value="${width}"/>' 
					       height='<c:out value="${height}"/>' 
					       ssid='<c:out value="${ssid}"/>' 
					       logoffURL='<c:out value="${logoutUrl}"/>' 
					       scriptable='false' 
				<c:if test="${not empty userInterfaceConfig.clientJavaOptions}">
					       java_arguments='<c:out value="${userInterfaceConfig.clientJavaOptions}"/>'
				</c:if>
				<c:if test="${not empty userInterfaceConfig.lookAndFeelValue}">
						   lookAndFeel='<c:out value="${userInterfaceConfig.lookAndFeelValue}"/>'
				</c:if>
					       pluginspage='http://java.sun.com/products/plugin/downloads/index.html'>
						<NOEMBED></NOEMBED>
					</EMBED>
				</COMMENT>
			</OBJECT>
		</td>
	</tr>
</tbody>
</table>
</body>
</html>

	</c:otherwise>
</c:choose>
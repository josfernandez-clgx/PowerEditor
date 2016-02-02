<%@ include file="/includes/global.jsp" %>

<pe:reset-logout-url />
<pe:extract-authenticated-userid var="authenticatedUserId" />

<c:choose>
	<%-- No authenticated user id found; forward to the login page. --%>
	<c:when test="${empty authenticatedUserId}">
		<pe:get-login-url var="loginUrl" />
		<c:redirect url="${loginUrl}" />
	</c:when>
	
	<%-- Authenticated user id found; proceed and launch application --%>
	<c:otherwise>

<HTML>
<HEAD>
<TITLE><pe:write-application-title /></TITLE>
<meta http-equiv="Pragma" content="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<LINK rel="stylesheet" href='<c:url value="/styles/mb_style.css"/>' type="text/css">
<SCRIPT type="text/javascript" language="Javascript">
<!-- //
function launchApplet(ssid) {
	if (screen) {
		pewidth = screen.availWidth-6;
		peheight = screen.availHeight-24;
	}
	else {
		pewidth = 1024;
		peheight= 768;
	}
	var currDate = new Date();
	var winName = currDate.getHours()*3600000 + currDate.getMinutes()*60000 + currDate.getSeconds()*1000 + currDate.getMilliseconds();
	locationStr = '<c:url value="/auth/powereditor_applet.jsp"/>' + "?ssid="+ssid+"&w="+(pewidth-4)+"&h="+(peheight-6)+"&etc=1";
	peWindow = window.open(locationStr,'pe'+winName,
	                       'toolbar=0,location=0,status=0,scrollbars=0,menubar=0,resizable=0,width='+pewidth + ',height=' + peheight +',left=0,top=0');
	
	return peWindow;
}

// -->
</SCRIPT>
</HEAD>
<BODY bgcolor='#eaf0ff'>
<center>
<img src="images/MB.gif" border="0"><br>
<H2>PowerEditor Sign In</H2>
Signing in user <b><pe:write-user-display-name userId="${authenticatedUserId}"/></b><br/>
<p>
<div class='body'>

<script type="text/javascript" language="Javascript">
<!--//
var peWindow = launchApplet('<c:out value="${pageContext.session.id}"/>');
if (peWindow == null) {
	alert("PowerEditor requires popups. Please enable popups and try again.");
	document.write("<span class='error'>");
	document.write("Unable to launch PowerEditor because popup is blocked.<br/>");
	document.write("Enable popups and <a href=\"login.jsp\">sign in</a> again.");
	document.write("</span>");
}
else {
	document.write("PLEASE CLOSE THIS WINDOW NOW.");
	document.write("<br/><br/>");
	document.write("Do not re-enter PowerEditor from this browser window.");
	document.write("<br/><br/>");
	document.write("To re-enter PowerEditor, close all browser windows and open a new browser window.");
}
//-->
</script>
</div>

<p>
&nbsp;
<p>
<hr noshade width="300" size='2'>
<font size="1"><%=(new java.util.Date().toString())%></font>

</center>
</BODY>
</HTML>

	</c:otherwise>
</c:choose>
<%@ page errorPage="/error.jsp" %>
<%@ page import="com.mindbox.pe.server.spi.*" %>

<% Package serverPackage = Package.getPackage("com.mindbox.pe.server"); %>

<html:html locale="true">
<head>
<title>MindBox <%=serverPackage.getSpecificationTitle()%> <%=serverPackage.getSpecificationVersion()%> Password Expiration Notification</title>
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" language="Javascript">
    function forward(location) {
        var requestParm="?userID=<%=request.getParameter("userID")%>";
        window.location.href=location + requestParm;
    }
</script>
</head>

<body class="mb">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
    <td width="100%"><img src="images/blank.gif" width="1" height="18" border="0">
    </td>
  </tr>
  <tr class="header">
    <td align="center">
      <table cellpadding="0" cellspacing="2" border="0" width="620">
        <tr valign="bottom"><td width="100%" align="center"><img src="images/MB.gif" border="0"></td></tr>
        <tr valign="top"><td class="title" align="center">Password Expiration Notification</td></tr>
      </table>
    </td>
  </tr>
  <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
  <tr>
    <td align="center">
      <table style="font-size: 10pt;" cellpadding="0" cellspacing="2" border="0" width="620">
        <tr valign="top"><td align="center">Your password will expire in <%=request.getParameter("days")%> days.
          <br><br>Do you want to change it now?
        </td>
      </tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
      <tr valign="top">
        <td align="center">
          <button class="smallButton" onClick="javascript:forward('change_pwd.jsp')">Yes, change password now</button>
          <img src="images/blank.gif" width="20" height="20" border="0">
          <button class="smallButton" onClick="javascript:forward('launch.jsp')">Not now, continue with login</button>
        </td>
      </tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
      <tr valign="bottom"><td width="100%" align="center"><img src="images/blank.gif" border="0"></td></tr>
    </table>
  </td>
</tr>
<tr valign="center">
  <td style="font-size: 10pt;" align="center">
    <a href="login.jsp">Home</a>
  </td>
</tr>
<tr class="footer">
	<td align="center">
	<%@ include file="footer.jsp" %>
	</td>
</tr>
</table>

</body>
</html:html>


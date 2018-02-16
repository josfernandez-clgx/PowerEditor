<%@ page errorPage="/error.jsp" contentType="text/html; encoding='UTF-8'" %>
<%@ taglib prefix="pe" uri="/WEB-INF/powereditor.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<title>PowerEditor Server Statistics</title>
<meta http-equiv="Pragma" content="no-cache">
<LINK rel="stylesheet" href="../styles/mb_style.css" type="text/css">
</head>
<pe:server-stats name="serverStats"/>
<body>
<h1>PowerEditor Server Statistics</h1>
<hr noshade>
<table cellpadding='3' cellspacing='1' border='1'>
<tr><td>Server Version:</td><td><c:out value="${serverStats.version}"/></td></tr>
<tr><td>Server Started On:</td><td><c:out value="${serverStats.startedDate}"/></td></tr>
<tr><td>Number of Users Logged In:</td><td><c:out value="${serverStats.userCount}"/></td></tr>
</table><br>
<a href="validate_config.jsp">View Configuration Details</a>
</body>
</html>
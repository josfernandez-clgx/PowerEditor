<%@ include file="/includes/global.jsp"%>
<pe:get-app-version var="versionString" appendBuild="true" />
<html>
<head>
<title><pe:write-application-title /> - Version Information</title>
<link rel="stylesheet" href='<c:url value="/styles/mb_style.css"/>' type="text/css">
<meta http-equiv="Pragma" content="no-cache">
</head>
<body align='center'>
	<div>
		<img src="images/MB.gif" border="0">
	</div>
	<div>
		<h1>PowerEditor</h1>
		<h2>
			<c:out value="Version ${versionString}" />
		</h2>
	</div>
</body>
</html>

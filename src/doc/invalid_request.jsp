<%@ page contentType="text/html"%>
<%@ include file="/includes/global.jsp" %>

<html>
<head>
<title><pe:write-application-title /> - Invalid Request</title>
<script type="text/javascript">
	function closeThis() {
		window.close();
	}
</script>
</head>
<body>

<H1>Invalid Request</H2>

<p class='error'>
The request you made is invalid. <c:out value="${param.reason}"/>
<br/>
Please try again.
</p>
<br />
<input type='button' onsubmit='closeThis();' value='Close' />
</body>
</html>
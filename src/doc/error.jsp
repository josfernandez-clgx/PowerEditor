
<%@ page isErrorPage="true" %>

<html>
<head>
<title>PowerEditor Error</title>
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
</head>
<body class="mb">

<span class="error">
<pre>
<%=exception.getMessage()%>
<%
java.io.StringWriter writer = new java.io.StringWriter();
exception.printStackTrace(new java.io.PrintWriter(writer));
%>
<%=writer.toString()%>
</pre>
</span>

</body>
</html>

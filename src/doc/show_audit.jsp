<%@ page import="com.mindbox.pe.server.db.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%! 
private static final String Q_SELECT_AUDIT =
	"SELECT user_name,edit_type,edit_object_id,edit_value_from,edit_value_to,edit_date from MB_AUDIT order by edit_date desc";

%>

<HTML>
<HEAD>
<TITLE>PowerEditor - Audit Trails</TITLE>
<meta http-equiv="Pragma" content="no-cache">
<LINK rel="stylesheet" href="styles/mb_style.css" type="text/css">
</HEAD>
<BODY class="mb">
<img src="images/MB-small.gif" border="0"><br>
<H2>PowerEditor - View Audit Trails</H2>

<table cellpadding="0" cellspacing="1" width="100%" border="0" style="background-color: #282828;">
<tr>
<td>

<table class="result" cellpadding="3" cellspacing="1" border="0" style="background-color: #a8a8a8;">
<tr class="colheading">
	<td>Date</td><td>Type</td><td><nobr>Entity ID</nobr></td><td>Prev Value</td><td>New Value</td><td>User</td>
</tr>

<%
DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();

Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try {
	conn = dbconnectionmanager.getConnection();

	ps = conn.prepareStatement(Q_SELECT_AUDIT);

	rs = ps.executeQuery();
	int rowID = 1;
	while (rs.next()) {
		String name = rs.getString(1);
		String type = rs.getString(2);
		int id = rs.getInt(3);
		String fromStr = rs.getString(4);
		String toStr = rs.getString(5);
		String dateStr = rs.getString(6);
%>

<tr class="row<%=rowID%>" valign="top">
	<td><nobr><%=dateStr%></nobr></td>
	<td><nobr><%=type%></nobr></td>
	<td><%=String.valueOf(id)%></td>
	<td width="50%"><%=fromStr%></td>
	<td width="50%"><%=toStr%></td>
	<td><nobr><%=name%></nobr></td>
</tr>

<%
		rowID = (rowID == 1 ? 2 : 1);
	}
	rs.close();
	rs = null;
}
catch (Exception ex) {
	ex.printStackTrace(System.err);
%>
<tr class="error">
	<td colspan="6">Error: <%=ex.getMessage()%> (<%=ex.getClass().getName()%>)</td>
</tr>
<%
}
finally {
	try {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
		if (conn != null) dbconnectionmanager.freeConnection(conn);
	}
	catch (Exception ex2) {
		System.err.println("Failed to close DB resources");
		ex2.printStackTrace(System.err);
	}
}
%>
<tr>
</tr>
</table>
</td>
</tr>
</table>

<hr noshade>
<font size="1"><%=(new java.util.Date().toString())%></font>
</BODY>
</HTML>



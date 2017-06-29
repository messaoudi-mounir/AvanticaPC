<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<% 
Class.forName("org.h2.Driver");
Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost:9031/petrolink", "sa", "");
// add application code here
Statement statement = conn.createStatement() ;
String query= "select * from alerts";
ResultSet rs = statement.executeQuery(query); 
            
      %>
      
      <%
int sec = 5000;
if (request.getParameter("sec") == null) {
} else {
    sec = Integer.parseInt(request. getParameter("sec"));
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Alert Status</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>
<body>
<div class="container">
	<input type="number" class="form-control" id="secs" value="<%=sec%>" style="width:250px; display:inline-block">
	<button class="btn btn-primary" onclick="updateSecs()">SET</button>
	<button class="btn btn-primary" onclick="reload()">REFRESH</button>
	<table class="table">
		<thead>
			<tr>
				<th>UUID</th>
				<th>status</th>
				<th>lastStatusChange</th>
				<th>created</th>
				<th>lastOccurrence</th>
				<th>acknowledgeBy</th>
				<th>acknowledgeAt</th>
				<th>comment</th>
				<th>commentBy</th>
				<th>commentedAt</th>
<!-- 				<th>tally</th> -->
<!-- 				<th>name</th> -->
<!-- 				<th>description</th> -->
<!-- 				<th>domain</th> -->
<!-- 				<th>classification</th> -->
<!-- 				<th>severity</th> -->
<!-- 				<th>priority</th> -->
<!-- 				<th>details</th> -->
<!-- 				<th>createdIndex</th> -->
<!-- 				<th>lastIndex</th> -->
				
			</tr>
		</thead>
		<tbody>
			
			<% while(rs.next()){ %>
			<tr>
				<td><%=rs.getString("UUID") %></td>
				<td><%=rs.getString("status") %></td>
				<td><%=rs.getString("lastStatusChange") %></td>
				<td><%=rs.getString("created") %></td>
				<td><%=rs.getString("lastOccurrence") %></td>
				<td><%=rs.getString("acknowledgeBy") %></td>
				<td><%=rs.getString("acknowledgeAt") %></td>
				<td><%=rs.getString("comment") %></td>
				<td><%=rs.getString("commentBy") %></td>
				<td><%=rs.getString("commentedAt") %></td>
			</tr>
			<% }
			
			conn.close();%>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	var sec = <%=sec%>;

	function updateSecs(){
		sec = document.getElementById("secs").value;
	}
	
	function reload(){
		window.location = window.location.pathname+"?sec="+sec;
	}
	
  	setTimeout(function () { window.location = window.location.pathname+"?sec="+sec }, sec);
</script>
</body>
</html>
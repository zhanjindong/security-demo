<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
		out.println("<a href=\"/security-demo/xss.jsp?name=<script>alert('XSS attacked!')</script>\" >Test XSS(Use IE).Redirect to xss.jsp</a> <br />");
		out.println("<a href=\"/security-demo/target.jsp\">Test CSRF.Redirect to target.jsp</a> <br />");
	%>

	<%out.println("Request Token CSRF <br/>"); %>
	<form action="/security-demo/token.jsp?csrf=${csrf}" method="POST"
		enctype="multipart/form-data">
		<input type="file" name="file" size="50" />
	</form>
</body>
</html>
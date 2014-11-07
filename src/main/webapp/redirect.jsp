<%@page import="org.owasp.esapi.ESAPI"%>
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
		String url = request.getParameter("url");
		if (url != null && !"".equals(url)) {
			ESAPI.httpUtilities().sendRedirect(response, url);
			//response.sendRedirect(url);
		}
	%>
</body>
</html>
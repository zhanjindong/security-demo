<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hello World!</title>
</head>
<body>
	<%!private int accessCount = 0;%>
	<H2>
		Accesses to page since server reboot:
		<%=++accessCount%></H2>

	<%
		out.println("<a href=\"http://www.cnblogs.com/magialmoon/\">Click to Download</a> <br />");
		String name = request.getParameter("name");
		if (name != null) {
			out.println("Welcome " + name);
		}
		//chrome浏览器非常强大，已经对参数进行过滤了，防止XSS攻击。
		//而且比较智能，如果下面有和攻击一样的语句自动屏蔽。
		//chrome浏览器查看会发现输出的html语句被标红。提示：Token contains a reflected XSS vector.
		//即使进行了ASCII编码也能过滤。
		//更牛B的是你从chrome浏览器中copy地址，如果潜在XSS攻击都会被转义。
		//相比之下，用IE一测试，高下立见。
		out.println("<script>alert(\"ok\")</script>");
	%>
</body>
</html>
<%@ page import="java.sql.*,db.DBUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Home</title>
	</head>
	<body>
		<%
    		String currentUser = (String) session.getAttribute("username");
    		if (currentUser == null) {
        		response.sendRedirect("login.jsp");
        		return;
    		}
		%>
		<h2>Welcome, <%= currentUser %>!</h2>
		<h3>List of Users:</h3>
		<ul>
			<%
    			try (Connection conn = DBUtil.getConnection()) {
        			Statement stmt = conn.createStatement();
        			ResultSet rs = stmt.executeQuery("SELECT username FROM users");
        			while (rs.next()) {
            			out.println("<li>" + rs.getString("username") + "</li>");
        			}
    			} catch (Exception e) {
        			out.println("Error: " + e.getMessage());
    			}
			%>
		</ul>
	</body>
</html>

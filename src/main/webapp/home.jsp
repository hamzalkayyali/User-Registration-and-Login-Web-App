<%@ page import="java.sql.*, db.DBUtil" %>
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


		<form action="LogoutServlet" method="get" style="margin-bottom:20px;">
    		<button type="submit">Logout</button>
		</form>

		<h3>List of Users:</h3>
		<ul>
			<%
    			try (Connection conn = DBUtil.getConnection()) {
        			Statement stmt = conn.createStatement();
        			ResultSet rs = stmt.executeQuery("SELECT username FROM users");

        		while (rs.next()) {
            		String uname = rs.getString("username");

            		if (uname.equals(currentUser)) {
            			out.println("<li>" + uname + 
            		    	" <form style='display:inline;' action='editUser.jsp' method='get'>" +
            		    	"<input type='hidden' name='username' value='" + uname + "'>" +
            		    	"<button type='submit'>Edit</button>" +
            		    	"</form> | " +
            		    	"<form style='display:inline;' action='DeleteMyAccountServlet' method='post'>" +
            		    	"<input type='hidden' name='username' value='" + uname + "'>" +
            		    	"<button type='submit' onclick='return confirm(\"Are you sure you want to delete your account?\");'>Delete</button>" +
            		    	"</form>" +
            		    	"</li>");

            		} else {
                		out.println("<li>" + uname + "</li>");
            		}
        		}
    		} catch (Exception e) {
        		out.println("Error: " + e.getMessage());
    		}
			%>
		</ul>
	</body>
</html>

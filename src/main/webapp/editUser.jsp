<%@ page import="java.sql.*, db.DBUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Edit User</title>
	</head>
	<body>
		<%
    		String currentUser = (String) session.getAttribute("username");
    		String usernameParam = request.getParameter("username");

    		if (currentUser == null) {
        		response.sendRedirect("login.jsp");
        		return;
    		}

   	 		if (usernameParam == null || !usernameParam.equals(currentUser)) {
		%>
        <p>You are not allowed to edit this user.</p>
		<%
        	return;
    		}

    		String message = (String) request.getAttribute("message");
    		if (message != null && !message.isEmpty()) {
		%>
        <p style="color:red;"><%= message %></p>
		<%
    		}
		%>

		<h2>Edit Username</h2>
		<form action="UpdateUserServlet" method="post">
    		<input type="hidden" name="oldUsername" value="<%= currentUser %>">
    		<label>New Username:</label>
    		<input type="text" name="newUsername" required value="<%= currentUser %>"><br><br>
    		<button type="submit">Update Username</button>
		</form>

		<hr>

		<h2>Change Password</h2>
		<form action="UpdatePasswordServlet" method="post">
    		<input type="hidden" name="username" value="<%= currentUser %>">

    		<label>Current Password:</label>
    		<input type="password" name="currentPassword" required><br><br>

    		<label>New Password:</label>
    		<input type="password" name="newPassword" required><br><br>

    		<label>Confirm New Password:</label>
    		<input type="password" name="confirmPassword" required><br><br>

    		<button type="submit">Update Password</button>
		</form>

		<br><br>
		<form action="home.jsp" method="get">
    		<button type="submit">Back to Home</button>
		</form>
	</body>
</html>

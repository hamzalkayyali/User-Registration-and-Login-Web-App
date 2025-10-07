<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Register</title>
	</head>
	<body>
    	<h2>Register</h2>

    	<% 
        	String message = (String) request.getAttribute("message");
        	if (message != null && !message.isEmpty()) {
    	%>
        	<p style="color:red;"><%= message %></p>
    	<% } %>

    	<form action="RegisterServlet" method="post">
        	<label>Username:</label>
        	<input type="text" name="username" required><br><br>

        	<label>Password:</label>
        	<input type="password" name="password" required><br><br>

        	<button type="submit">Register</button>
    	</form>

    	<p>Already have an account? <a href="login.jsp">Login here</a></p>
	</body>
</html>

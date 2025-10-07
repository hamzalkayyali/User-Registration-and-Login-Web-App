<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Login</title>
	</head>
	<body>
    	<h2>Login</h2>

    	<% 
        	String message = (String) request.getAttribute("message");
        	if (message == null) {
            	message = request.getParameter("message");
        	}
        	if (message != null && !message.isEmpty()) {
    	%>
        	<p style="color:red;"><%= message %></p>
    	<% } %>

    	<form action="LoginServlet" method="post">
        	<label>Username:</label>
        	<input type="text" name="username" required><br><br>

        	<label>Password:</label>
        	<input type="password" name="password" required><br><br>

        	<button type="submit">Login</button>
    	</form>

    	<p>Don't have an account? <a href="register.jsp">Register here</a></p>

    	<p><a href="resetPassword.jsp">Forgot or want to reset your password?</a></p>
	</body>
</html>

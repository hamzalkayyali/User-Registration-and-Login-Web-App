<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Register</title>
	</head>
	<body>
		<h2>User Registration</h2>
		<form action="RegisterServlet" method="post">
    		Username: <input type="text" name="username" required><br>
    		Password: <input type="password" name="password" required><br>
    		<input type="submit" value="Register">
		</form>
		<!-- Button that links to login.jsp -->
		<br>
		<a href="login.jsp">
    		<button type="button">Go to Login</button>
		</a>

	</body>
</html>

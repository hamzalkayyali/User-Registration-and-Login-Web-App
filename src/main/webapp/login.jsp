<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
    	<title>Login</title>
	</head>
	<body>
		<h2>User Login</h2>
		<form action="LoginServlet" method="post">
    		Username: <input type="text" name="username" required><br>
    		Password: <input type="password" name="password" required><br>
    		<input type="submit" value="Login">
		</form>
	</body>
</html>

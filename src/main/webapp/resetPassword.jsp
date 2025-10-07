<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Reset Password</title>
</head>
<body>
    <h2>Reset Password</h2>

    <% 
        String message = (String) request.getAttribute("message");
        if (message == null) {
            message = request.getParameter("message");
        }
        if (message != null && !message.isEmpty()) {
    %>
        <p style="color:red;"><%= message %></p>
    <% } %>

    <form action="ResetPasswordServlet" method="post">
        <label>Username:</label>
        <input type="text" name="username" required><br><br>

        <label>New Password:</label>
        <input type="password" name="newPassword" required><br><br>

        <button type="submit">Reset Password</button>
    </form>

    <p><a href="login.jsp">Back to Login</a></p>
</body>
</html>

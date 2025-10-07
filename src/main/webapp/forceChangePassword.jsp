<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
    <title>Change Expired Password</title>
</head>
<body>
<%
    if (session == null || session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String username = (String) session.getAttribute("username");
    String message = request.getParameter("message");
    if (message == null) {
        message = (String) request.getAttribute("message");
    }
%>

<h2>Password Expired - Please Change Password</h2>

<% if (message != null && !message.isEmpty()) { %>
    <p style="color:red;"><%= message %></p>
<% } %>

<form action="ForceChangePasswordServlet" method="post">
    <input type="hidden" name="username" value="<%= username %>">

    <label>New Password:</label>
    <input type="password" name="newPassword" required><br><br>

    <label>Confirm New Password:</label>
    <input type="password" name="confirmPassword" required><br><br>

    <button type="submit">Update Password</button>
</form>
</body>
</html>

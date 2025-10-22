<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Assign Roles</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        label { display: inline-block; width: 120px; }
        input, select { padding: 5px; margin-bottom: 10px; }
        .message-success { color: green; }
        .message-error { color: red; }
    </style>
</head>
<body>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || !"admin".equals(role)) {
        request.setAttribute("message", "Access Denied: Only admins can assign roles.");
    }
%>

<h2>Assign Role to User</h2>

<form action="AssignRoleServlet" method="post">
    <label for="username">Username:</label>
    <input type="text" name="username" required><br>

    <label for="role">Select Role:</label>
    <select name="newRole" required>
        <option value="USER">User</option>
        <option value="ADMIN">Admin</option>
        <option value="MANAGER">Manager</option>
    </select><br>

    <input type="submit" value="Assign Role">
</form>

<%
    String message = (String) request.getAttribute("message");
    if (message != null) {
        String cssClass = message.toLowerCase().contains("error") || message.toLowerCase().contains("denied")
                          ? "message-error" : "message-success";
%>
    <p class="<%= cssClass %>"><%= message %></p>
<%
    }
%>

<p><a href="home.jsp">Back to Home</a></p>
</body>
</html>

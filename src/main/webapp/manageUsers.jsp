<%@ page import="java.sql.*, db.DBUtil, utils.PermissionUtil, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Manage Users</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .message { padding: 10px; margin: 10px 0; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .reset-link { background-color: #fff3cd; padding: 15px; margin: 15px 0; border: 1px solid #ffc107; }
        .reset-link input { width: 80%; padding: 8px; margin: 10px 0; }
        button { padding: 8px 15px; margin: 2px; cursor: pointer; }
        .btn-primary { background-color: #007bff; color: white; border: none; }
        .btn-danger { background-color: #dc3545; color: white; border: none; }
        .btn-success { background-color: #28a745; color: white; border: none; }
        .role-badge { display: inline-block; padding: 3px 8px; margin: 2px; background-color: #007bff; color: white; border-radius: 3px; font-size: 12px; }
    </style>
</head>
<body>
<%
    String currentUser = (String) session.getAttribute("username");
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    int currentUserId = -1;
    boolean canViewAllUsers = false;
    boolean canAssignRoles = false;
    boolean canResetPassword = false;
    
    try {
        currentUserId = PermissionUtil.getUserId(currentUser);
        canViewAllUsers = PermissionUtil.hasPermission(currentUserId, "VIEW_ALL_USERS");
        canAssignRoles = PermissionUtil.hasPermission(currentUserId, "ASSIGN_ROLES");
        canResetPassword = PermissionUtil.hasPermission(currentUserId, "RESET_PASSWORD");
    } catch (Exception e) {
        out.println("<p class='message error'>Error checking permissions: " + e.getMessage() + "</p>");
    }

    if (!canViewAllUsers && !canAssignRoles && !canResetPassword) {
        out.println("<h2>Access Denied</h2>");
        out.println("<p>You don't have permission to access this page.</p>");
        out.println("<a href='home.jsp'>Back to Home</a>");
        return;
    }

    String message = (String) request.getAttribute("message");
    String resetLink = (String) request.getAttribute("resetLink");
    String targetUsername = (String) request.getAttribute("targetUsername");
%>

<h2>Manage Users</h2>
<p>Welcome, <%= currentUser %>! | <a href="home.jsp">Back to Home</a></p>

<% if (message != null) { %>
    <div class="message <%= message.contains("success") || message.contains("successfully") ? "success" : "error" %>">
        <%= message %>
    </div>
<% } %>

<% if (resetLink != null) { %>
    <div class="reset-link">
        <h3>Password Reset Link Generated for <%= targetUsername %></h3>
        <p>Share this link with the user:</p>
        <input type="text" value="<%= resetLink %>" readonly onclick="this.select()">
        <button onclick="copyToClipboard('<%= resetLink %>')">Copy Link</button>
        <p><small>Link expires in 24 hours</small></p>
    </div>
<% } %>

<h3>Users List</h3>
<table>
    <tr>
        <th>Username</th>
        <th>Roles</th>
        <% if (canAssignRoles) { %><th>Assign/Remove Role</th><% } %>
        <% if (canResetPassword) { %><th>Reset Password</th><% } %>
    </tr>
<%
    try (Connection conn = DBUtil.getConnection()) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, username FROM users ORDER BY username");
        
        while (rs.next()) {
            int userId = rs.getInt("id");
            String username = rs.getString("username");
            
            List<String> userRoles = PermissionUtil.getUserRoles(userId);
            
            out.println("<tr>");
            out.println("<td>" + username + "</td>");
            
            out.println("<td>");
            if (userRoles.isEmpty()) {
                out.println("<span style='color: gray;'>No roles assigned</span>");
            } else {
                for (String role : userRoles) {
                    out.println("<span class='role-badge'>" + role + "</span>");
                }
            }
            out.println("</td>");
            
            if (canAssignRoles) {
                out.println("<td>");
                out.println("<form method='post' action='AssignRoleServlet' style='display:inline;'>");
                out.println("<input type='hidden' name='targetUsername' value='" + username + "'>");
                out.println("<select name='roleName' required>");
                out.println("<option value=''>Select Role</option>");
                out.println("<option value='ADMIN'>ADMIN</option>");
                out.println("<option value='MANAGER'>MANAGER</option>");
                out.println("<option value='USER'>USER</option>");
                out.println("</select>");
                out.println("<button type='submit' name='action' value='assign' class='btn-success'>Assign</button>");
                out.println("<button type='submit' name='action' value='remove' class='btn-danger'>Remove</button>");
                out.println("</form>");
                out.println("</td>");
            }
            
            if (canResetPassword) {
                out.println("<td>");
                if (!username.equals(currentUser)) {
                    out.println("<form method='post' action='GenerateResetLinkServlet' style='display:inline;'>");
                    out.println("<input type='hidden' name='targetUsername' value='" + username + "'>");
                    out.println("<button type='submit' class='btn-primary'>Generate Reset Link</button>");
                    out.println("</form>");
                } else {
                    out.println("<span style='color: gray;'>N/A (yourself)</span>");
                }
                out.println("</td>");
            }
            
            out.println("</tr>");
        }
    } catch (Exception e) {
        out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
    }
%>
</table>

<script>
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(function() {
        alert('Link copied to clipboard!');
    }, function(err) {
        alert('Failed to copy link');
    });
}
</script>

</body>
</html>
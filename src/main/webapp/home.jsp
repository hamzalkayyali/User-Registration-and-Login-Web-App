<%@ page import="java.sql.*, db.DBUtil, utils.PermissionUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Home</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .nav-buttons { margin: 20px 0; }
        .nav-buttons a, .nav-buttons button { 
            padding: 10px 20px; 
            margin-right: 10px; 
            text-decoration: none; 
            background-color: #007bff; 
            color: white; 
            border: none; 
            cursor: pointer; 
            display: inline-block;
            border-radius: 4px;
        }
        .nav-buttons a:hover, .nav-buttons button:hover { background-color: #0056b3; }
        .logout-btn { background-color: #dc3545 !important; }
        .logout-btn:hover { background-color: #c82333 !important; }
        ul { list-style-type: none; padding: 0; }
        li { padding: 8px; margin: 5px 0; background-color: #f8f9fa; border-radius: 4px; }
        .user-info { background-color: #e7f3ff; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .role-badge { 
            display: inline-block; 
            padding: 3px 8px; 
            margin: 2px; 
            background-color: #28a745; 
            color: white; 
            border-radius: 3px; 
            font-size: 12px; 
        }
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
    java.util.List<String> userRoles = new java.util.ArrayList<>();
    java.util.List<String> userPermissions = new java.util.ArrayList<>();
    boolean canManageUsers = false;
    
    try {
        currentUserId = PermissionUtil.getUserId(currentUser);
        userRoles = PermissionUtil.getUserRoles(currentUserId);
        userPermissions = PermissionUtil.getUserPermissions(currentUserId);
        canManageUsers = PermissionUtil.hasPermission(currentUserId, "VIEW_ALL_USERS") ||
                        PermissionUtil.hasPermission(currentUserId, "ASSIGN_ROLES") ||
                        PermissionUtil.hasPermission(currentUserId, "RESET_PASSWORD");
    } catch (Exception e) {
        out.println("<p style='color:red;'>Error loading user info: " + e.getMessage() + "</p>");
    }
%>

<div class="header">
    <h2>Welcome, <%= currentUser %>!</h2>
    <form action="LogoutServlet" method="get" style="margin:0;">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</div>

<div class="user-info">
    <p><strong>Your Roles:</strong> 
    <% 
        if (userRoles.isEmpty()) {
            out.println("<span style='color: gray;'>No roles assigned</span>");
        } else {
            for (String role : userRoles) {
                out.println("<span class='role-badge'>" + role + "</span>");
            }
        }
    %>
    </p>
    <p><strong>Your Permissions:</strong>
    <%
        if (userPermissions.isEmpty()) {
            out.println("<span style='color: gray;'>Basic user access only</span>");
        } else {
            out.println("<br>");
            for (String perm : userPermissions) {
                out.println("• " + perm.replace("_", " ") + "<br>");
            }
        }
    %>
    </p>
</div>

<div class="nav-buttons">
    <a href="editUser.jsp?username=<%= currentUser %>">Edit My Profile</a>
    <% if (canManageUsers) { %>
        <a href="manageUsers.jsp">Manage Users</a>
    <% } %>
</div>

<h3>List of Users:</h3>
<ul>
<%
    try (Connection conn = DBUtil.getConnection()) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT username FROM users ORDER BY username");
        
        while (rs.next()) {
            String uname = rs.getString("username");
            
            if (uname.equals(currentUser)) {
                out.println("<li><strong>" + uname + " (You)</strong> | ");
                out.println("<form style='display:inline;' action='editUser.jsp' method='get'>");
                out.println("<input type='hidden' name='username' value='" + uname + "'>");
                out.println("<button type='submit' style='padding:5px 10px; cursor:pointer;'>Edit</button>");
                out.println("</form> | ");
                out.println("<form style='display:inline;' action='DeleteMyAccountServlet' method='post'>");
                out.println("<input type='hidden' name='username' value='" + uname + "'>");
                out.println("<button type='submit' onclick='return confirm(\"Are you sure you want to delete your account?\");' style='padding:5px 10px; cursor:pointer; background-color:#dc3545; color:white; border:none;'>Delete</button>");
                out.println("</form>");
                out.println("</li>");
            } else {
                out.println("<li>" + uname + "</li>");
            }
        }
    } catch (Exception e) {
        out.println("<li style='color:red;'>Error: " + e.getMessage() + "</li>");
    }
%>
</ul>

</body>
</html>

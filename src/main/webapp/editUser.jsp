<%@ page import="java.sql.*, db.DBUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Edit User</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 700px;
            margin: 30px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            border: 1px solid #ddd;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
            margin-bottom: 25px;
        }
        .section {
            margin-bottom: 40px;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 6px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        input[type="text"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: #4CAF50;
            box-shadow: 0 0 5px rgba(76,175,80,0.3);
        }
        input[type="hidden"] {
            display: none;
        }
        button {
            padding: 12px 25px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            margin-right: 10px;
        }
        button[type="submit"] {
            background-color: #4CAF50;
            color: white;
        }
        button[type="submit"]:hover {
            background-color: #45a049;
        }
        .btn-back {
            background-color: #6c757d;
            color: white;
        }
        .btn-back:hover {
            background-color: #5a6268;
        }
        .message {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 4px;
            text-align: center;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .requirements {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
            background-color: #fff;
            padding: 8px;
            border-radius: 4px;
        }
        hr {
            border: none;
            border-top: 1px solid #ddd;
            margin: 30px 0;
        }
        .back-button-container {
            text-align: center;
            margin-top: 30px;
        }
    </style>
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
    <div class="container">
        <h2>Access Denied</h2>
        <p class="message error">You are not allowed to edit this user.</p>
        <div class="back-button-container">
            <form action="home.jsp" method="get">
                <button type="submit" class="btn-back">Back to Home</button>
            </form>
        </div>
    </div>
<%
        return;
    }
    
    String message = (String) request.getAttribute("message");
%>

    <div class="container">
        <h2>Edit Profile</h2>
        
        <% if (message != null && !message.isEmpty()) { 
            boolean isSuccess = message.toLowerCase().contains("success");
        %>
            <div class="message <%= isSuccess ? "success" : "error" %>"><%= message %></div>
        <% } %>
        
        <div class="section">
            <h2>Update Username</h2>
            <form action="UpdateUserServlet" method="post">
                <input type="hidden" name="oldUsername" value="<%= currentUser %>">
                <div class="form-group">
                    <label>New Username:</label>
                    <input type="text" name="newUsername" required value="<%= currentUser %>" placeholder="Enter new username">
                </div>
                <button type="submit">Update Username</button>
            </form>
        </div>
        
        <hr>
        
        <div class="section">
            <h2>Change Password</h2>
            <form action="UpdatePasswordServlet" method="post">
                <input type="hidden" name="username" value="<%= currentUser %>">
                
                <div class="form-group">
                    <label>Current Password:</label>
                    <input type="password" name="currentPassword" required placeholder="Enter current password">
                </div>
                
                <div class="form-group">
                    <label>New Password:</label>
                    <input type="password" name="newPassword" required placeholder="Enter new password">
                    <div class="requirements">
                        Must be at least 8 characters with uppercase, lowercase, number, and special character
                    </div>
                </div>
                
                <div class="form-group">
                    <label>Confirm New Password:</label>
                    <input type="password" name="confirmPassword" required placeholder="Confirm new password">
                </div>
                
                <button type="submit">Update Password</button>
            </form>
        </div>
        
        <div class="back-button-container">
            <form action="home.jsp" method="get">
                <button type="submit" class="btn-back">Back to Home</button>
            </form>
        </div>
    </div>
</body>
</html>

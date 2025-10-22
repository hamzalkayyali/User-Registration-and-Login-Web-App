<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
    <title>Change Expired Password</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 500px;
            margin: 50px auto;
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
            color: #d9534f;
            text-align: center;
            margin-bottom: 20px;
        }
        .warning-box {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffc107;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 25px;
            font-size: 14px;
        }
        .warning-box strong {
            display: block;
            margin-bottom: 5px;
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
        input[type="password"],
        input[type="hidden"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        input[type="password"]:focus {
            outline: none;
            border-color: #4CAF50;
            box-shadow: 0 0 5px rgba(76,175,80,0.3);
        }
        input[type="hidden"] {
            display: none;
        }
        button {
            width: 100%;
            padding: 12px;
            background-color: #d9534f;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
        }
        button:hover {
            background-color: #c9302c;
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
        .requirements {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
            background-color: #f8f9fa;
            padding: 8px;
            border-radius: 4px;
        }
    </style>
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

    <div class="container">
        <h2>⚠ Password Expired</h2>
        
        <div class="warning-box">
            <strong>Your password has expired!</strong>
            For security reasons, you must change your password before continuing. 
            Your password must not match any of your last 4 passwords.
        </div>
        
        <% if (message != null && !message.isEmpty()) { %>
            <div class="message error"><%= message %></div>
        <% } %>
        
        <form action="ForceChangePasswordServlet" method="post">
            <input type="hidden" name="username" value="<%= username %>">
            
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
</body>
</html>

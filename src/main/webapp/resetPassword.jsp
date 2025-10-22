<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Reset Password</title>
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
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .info-box {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 14px;
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
        button {
            width: 100%;
            padding: 12px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
        }
        button:hover {
            background-color: #45a049;
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
        .footer-links {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .footer-links a {
            color: #4CAF50;
            text-decoration: none;
            font-weight: bold;
        }
        .footer-links a:hover {
            text-decoration: underline;
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
    <div class="container">
        <h2>Reset Password</h2>
        
        <div class="info-box">
            <strong>Note:</strong> This will reset your password without requiring authentication. 
            Please enter your username and new password below.
        </div>
        
        <% 
            String message = (String) request.getAttribute("message");
            if (message == null) {
                message = request.getParameter("message");
            }
            if (message != null && !message.isEmpty()) {
                boolean isSuccess = message.toLowerCase().contains("success");
        %>
            <div class="message <%= isSuccess ? "success" : "error" %>"><%= message %></div>
        <% } %>
        
        <form action="ResetPasswordServlet" method="post">
            <div class="form-group">
                <label>Username:</label>
                <input type="text" name="username" required placeholder="Enter your username">
            </div>
            
            <div class="form-group">
                <label>New Password:</label>
                <input type="password" name="newPassword" required placeholder="Enter new password">
                <div class="requirements">
                    Must be at least 8 characters with uppercase, lowercase, number, and special character
                </div>
            </div>
            
            <button type="submit">Reset Password</button>
        </form>
        
        <div class="footer-links">
            <a href="login.jsp">Back to Login</a>
        </div>
    </div>
</body>
</html>

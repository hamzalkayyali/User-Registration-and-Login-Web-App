<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login</title>
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
            font-size: 14px;
        }
        .footer-links a {
            color: #4CAF50;
            text-decoration: none;
            font-weight: bold;
        }
        .footer-links a:hover {
            text-decoration: underline;
        }
        .divider {
            margin: 15px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Login</h2>
        
        <% 
            String message = (String) request.getAttribute("message");
            if (message == null) {
                message = request.getParameter("message");
            }
            if (message != null && !message.isEmpty()) {
                boolean isSuccess = message.toLowerCase().contains("success") || 
                                   message.toLowerCase().contains("registered") ||
                                   message.toLowerCase().contains("reset");
        %>
            <div class="message <%= isSuccess ? "success" : "error" %>"><%= message %></div>
        <% } %>
        
        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label>Username:</label>
                <input type="text" name="username" required placeholder="Enter your username">
            </div>
            
            <div class="form-group">
                <label>Password:</label>
                <input type="password" name="password" required placeholder="Enter your password">
            </div>
            
            <button type="submit">Login</button>
        </form>
        
        <div class="footer-links">
            <div>Don't have an account? <a href="register.jsp">Register here</a></div>
            <div class="divider"></div>
            <div><a href="resetPassword.jsp">Forgot or want to reset your password?</a></div>
        </div>
    </div>
</body>
</html>

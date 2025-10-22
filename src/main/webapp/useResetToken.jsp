<%@ page import="java.sql.*, db.DBUtil" %>
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
        }
        .container {
            border: 1px solid #ddd;
            padding: 30px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            text-align: center;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="password"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
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
        }
        button:hover {
            background-color: #45a049;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        .requirements {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Reset Your Password</h2>
        
        <%
            String token = request.getParameter("token");
            String message = request.getParameter("message");
            
            if (token == null || token.isEmpty()) {
                out.println("<div class='message error'>Invalid or missing reset token.</div>");
                out.println("<p><a href='login.jsp'>Back to Login</a></p>");
                return;
            }
            
            // Verify token exists and is valid
            boolean tokenValid = false;
            String tokenError = null;
            
            try (Connection conn = DBUtil.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, expires_at, used FROM PASSWORD_RESET_TOKENS WHERE token = ?"
                );
                ps.setString(1, token);
                ResultSet rs = ps.executeQuery();
                
                if (!rs.next()) {
                    tokenError = "Invalid reset token.";
                } else {
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    String used = rs.getString("used");
                    
                    if ("1".equals(used)) {
                        tokenError = "This reset token has already been used.";
                    } else if (expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                        tokenError = "This reset token has expired.";
                    } else {
                        tokenValid = true;
                    }
                }
            } catch (Exception e) {
                tokenError = "Error validating token: " + e.getMessage();
            }
            
            if (!tokenValid) {
        %>
                <div class='message error'><%= tokenError %></div>
                <p><a href='login.jsp'>Back to Login</a></p>
        <%
                return;
            }
            
            if (message != null && !message.isEmpty()) {
        %>
                <div class='message error'><%= message %></div>
        <%
            }
        %>
        
        <div class='message info'>
            Please enter your new password below.
        </div>
        
        <form action="UseResetTokenServlet" method="post">
            <input type="hidden" name="token" value="<%= token %>">
            
            <div class="form-group">
                <label>New Password:</label>
                <input type="password" name="newPassword" required>
                <div class="requirements">
                    Must be at least 8 characters with uppercase, lowercase, number, and special character
                </div>
            </div>
            
            <div class="form-group">
                <label>Confirm New Password:</label>
                <input type="password" name="confirmPassword" required>
            </div>
            
            <button type="submit">Reset Password</button>
        </form>
        
        <p style="text-align: center; margin-top: 20px;">
            <a href="login.jsp">Back to Login</a>
        </p>
    </div>
</body>
</html>
package servlets;

import java.io.IOException;
import java.sql.*;

import db.DBUtil;
import utils.PasswordUtil; // ✅ use same utility class
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String newPassword = request.getParameter("newPassword");

        // ✅ 1. Check required fields
        if (username == null || newPassword == null || username.isEmpty() || newPassword.isEmpty()) {
            request.setAttribute("message", "Please fill in all fields.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        // ✅ 2. Enforce password complexity
        if (!isPasswordComplex(newPassword)) {
            request.setAttribute("message", "Password must be at least 8 characters long, contain uppercase, lowercase, a number, and a special character.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // ✅ 3. Get user ID
            int userId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                } else {
                    request.setAttribute("message", "User not found!");
                    request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
                    return;
                }
            }

            // ✅ 4. Check last 4 passwords (using PasswordUtil.verifyPassword)
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT password_hash FROM (SELECT password_hash FROM password_history WHERE user_id = ? ORDER BY created_at DESC) WHERE ROWNUM <= 4")) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (PasswordUtil.verifyPassword(newPassword, rs.getString("password_hash"))) {
                        request.setAttribute("message", "New password cannot be the same as any of the last 4 passwords!");
                        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
                        return;
                    }
                }
            }

            // ✅ 5. Hash new password with PasswordUtil
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            // ✅ 6. Update user's password
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET password_hash = ? WHERE id = ?")) {
                ps.setString(1, hashedPassword);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // ✅ 7. Add new password to password history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO password_history (user_id, password_hash) VALUES (?, ?)")) {
                ps.setInt(1, userId);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();
            }

            conn.commit();
            response.sendRedirect("login.jsp?message=Password+reset+successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }

    // ✅ Password complexity check (same rules you use elsewhere)
    private boolean isPasswordComplex(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}

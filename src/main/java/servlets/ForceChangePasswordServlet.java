package servlets;

import db.DBUtil;
import utils.PasswordUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ForceChangePasswordServlet")
public class ForceChangePasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Passwords do not match!");
            request.getRequestDispatcher("forceChangePassword.jsp").forward(request, response);
            return;
        }

        if (!isPasswordComplex(newPassword)) {
            request.setAttribute("message", "Password must be at least 8 characters long, contain uppercase, lowercase, a number, and a special character.");
            request.getRequestDispatcher("forceChangePassword.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT password_hash FROM (SELECT password_hash FROM password_history WHERE user_id=? ORDER BY created_at DESC) WHERE ROWNUM <= 4")) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (PasswordUtil.verifyPassword(newPassword, rs.getString("password_hash"))) {
                        request.setAttribute("message", "New password cannot be the same as any of the last 4 passwords!");
                        request.getRequestDispatcher("forceChangePassword.jsp").forward(request, response);
                        return;
                    }
                }
            }

            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET password_hash=? WHERE id=?")) {
                ps.setString(1, hashedPassword);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO password_history (user_id, password_hash) VALUES (?, ?)")) {
                ps.setInt(1, userId);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();
            }

            conn.commit();

            response.sendRedirect("home.jsp?message=Password+changed+successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
            request.getRequestDispatcher("forceChangePassword.jsp").forward(request, response);
        }
    }

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


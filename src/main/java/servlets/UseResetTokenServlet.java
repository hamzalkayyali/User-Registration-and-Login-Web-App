package servlets;

import db.DBUtil;
import utils.PasswordUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UseResetTokenServlet")
public class UseResetTokenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || newPassword == null || confirmPassword == null) {
            response.sendRedirect("useResetToken.jsp?token=" + token + "&message=All+fields+are+required");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect("useResetToken.jsp?token=" + token + "&message=Passwords+do+not+match");
            return;
        }

        if (!isPasswordComplex(newPassword)) {
            response.sendRedirect("useResetToken.jsp?token=" + token + 
                "&message=Password+must+be+at+least+8+characters+with+uppercase,+lowercase,+number,+and+special+character");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // Validate token
            PreparedStatement psToken = conn.prepareStatement(
                "SELECT user_id, expires_at, used FROM PASSWORD_RESET_TOKENS WHERE token = ?"
            );
            psToken.setString(1, token);
            ResultSet rsToken = psToken.executeQuery();

            if (!rsToken.next()) {
                response.sendRedirect("useResetToken.jsp?token=" + token + "&message=Invalid+token");
                return;
            }

            int userId = rsToken.getInt("user_id");
            Timestamp expiresAt = rsToken.getTimestamp("expires_at");
            String used = rsToken.getString("used");

            // Check if token is used
            if ("1".equals(used)) {
                response.sendRedirect("useResetToken.jsp?token=" + token + "&message=This+token+has+already+been+used");
                return;
            }

            // Check if token is expired
            if (expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                response.sendRedirect("useResetToken.jsp?token=" + token + "&message=This+token+has+expired");
                return;
            }

            // Check password history
            PreparedStatement psHistory = conn.prepareStatement(
                "SELECT password_hash FROM (SELECT password_hash FROM password_history " +
                "WHERE user_id=? ORDER BY created_at DESC) WHERE ROWNUM <= 4"
            );
            psHistory.setInt(1, userId);
            ResultSet rsHistory = psHistory.executeQuery();

            while (rsHistory.next()) {
                if (PasswordUtil.verifyPassword(newPassword, rsHistory.getString("password_hash"))) {
                    response.sendRedirect("useResetToken.jsp?token=" + token + 
                        "&message=Password+cannot+be+same+as+last+4+passwords");
                    return;
                }
            }

            // Hash new password
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            // Update user password
            PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE users SET password_hash = ? WHERE id = ?"
            );
            psUpdate.setString(1, hashedPassword);
            psUpdate.setInt(2, userId);
            psUpdate.executeUpdate();

            // Insert into password history
            PreparedStatement psInsertHistory = conn.prepareStatement(
                "INSERT INTO password_history (user_id, password_hash) VALUES (?, ?)"
            );
            psInsertHistory.setInt(1, userId);
            psInsertHistory.setString(2, hashedPassword);
            psInsertHistory.executeUpdate();

            // Mark token as used
            PreparedStatement psMarkUsed = conn.prepareStatement(
                "UPDATE PASSWORD_RESET_TOKENS SET used = '1' WHERE token = ?"
            );
            psMarkUsed.setString(1, token);
            psMarkUsed.executeUpdate();

            conn.commit();
            response.sendRedirect("login.jsp?message=Password+reset+successfully!+You+can+now+login.");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("useResetToken.jsp?token=" + token + "&message=Error:+" + e.getMessage());
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
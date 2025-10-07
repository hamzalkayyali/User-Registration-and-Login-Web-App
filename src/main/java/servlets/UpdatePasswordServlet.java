package servlets;

import db.DBUtil;
import utils.PasswordUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdatePasswordServlet")
public class UpdatePasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "New passwords do not match!");
            request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {

            // Step 1: Find user ID using username
            int userId = -1;
            PreparedStatement findUser = conn.prepareStatement("SELECT id, password_hash FROM users WHERE username = ?");
            findUser.setString(1, username);
            ResultSet rsUser = findUser.executeQuery();

            if (!rsUser.next()) {
                request.setAttribute("message", "User not found!");
                request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);
                return;
            }

            userId = rsUser.getInt("id");
            String storedHash = rsUser.getString("password_hash");

            // Step 2: Verify current password
            if (!PasswordUtil.verifyPassword(currentPassword, storedHash)) {
                request.setAttribute("message", "Current password is incorrect!");
                request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);
                return;
            }

            // Step 3: Check last 4 password hashes
            PreparedStatement ps = conn.prepareStatement(
                "SELECT password_hash FROM (SELECT password_hash FROM password_history WHERE user_id=? ORDER BY created_at DESC) WHERE ROWNUM <= 4"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (PasswordUtil.verifyPassword(newPassword, rs.getString("password_hash"))) {
                    request.setAttribute("message", "New password cannot be the same as any of the last 4 passwords!");
                    request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);
                    return;
                }
            }

            // Step 4: Hash new password and update
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            PreparedStatement updateUser = conn.prepareStatement(
                "UPDATE users SET password_hash=? WHERE id=?"
            );
            updateUser.setString(1, hashedPassword);
            updateUser.setInt(2, userId);
            updateUser.executeUpdate();

            // Step 5: Insert into password_history
            PreparedStatement insertHistory = conn.prepareStatement(
                "INSERT INTO password_history (id, user_id, password_hash) VALUES (password_history_seq.NEXTVAL, ?, ?)"
            );
            insertHistory.setInt(1, userId);
            insertHistory.setString(2, hashedPassword);
            insertHistory.executeUpdate();

            // Step 6: Success message
            request.setAttribute("message", "Password updated successfully!");
            request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Database error: " + e.getMessage());
            request.getRequestDispatcher("editUser.jsp?username=" + username).forward(request, response);
        }
    }
}

package servlets;

import db.DBUtil;
import utils.PermissionUtil;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/GenerateResetLinkServlet")
public class GenerateResetLinkServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentUsername = (String) session.getAttribute("username");
        String targetUsername = request.getParameter("targetUsername");

        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            request.setAttribute("message", "Please specify a username!");
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            int currentUserId = PermissionUtil.getUserId(currentUsername);
            if (!PermissionUtil.hasPermission(currentUserId, "RESET_PASSWORD")) {
                request.setAttribute("message", "You don't have permission to reset passwords!");
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                return;
            }

            PreparedStatement psTarget = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            psTarget.setString(1, targetUsername.trim());
            ResultSet rsTarget = psTarget.executeQuery();

            if (!rsTarget.next()) {
                request.setAttribute("message", "Target user not found!");
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                return;
            }

            int targetUserId = rsTarget.getInt("id");

            if (currentUserId == targetUserId) {
                request.setAttribute("message", "You cannot generate a reset link for yourself. Use the change password feature.");
                request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
                return;
            }

            // Generate unique token
            String token = UUID.randomUUID().toString();

            // Token expires in 24 hours
            Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            PreparedStatement psInsert = conn.prepareStatement(
                "INSERT INTO PASSWORD_RESET_TOKENS (user_id, token, created_by, expires_at) VALUES (?, ?, ?, ?)"
            );
            psInsert.setInt(1, targetUserId);
            psInsert.setString(2, token);
            psInsert.setInt(3, currentUserId);
            psInsert.setTimestamp(4, expiresAt);
            psInsert.executeUpdate();

            String resetLink = request.getScheme() + "://" + 
                             request.getServerName() + ":" + 
                             request.getServerPort() + 
                             request.getContextPath() + 
                             "/useResetToken.jsp?token=" + token;

            request.setAttribute("resetLink", resetLink);
            request.setAttribute("targetUsername", targetUsername);
            request.setAttribute("message", "Reset link generated successfully!");
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
        }
    }
}
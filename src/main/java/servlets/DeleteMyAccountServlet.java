package servlets;

import java.io.IOException;
import java.sql.*;
import db.DBUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DeleteMyAccountServlet")
public class DeleteMyAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        System.out.println("Attempting to delete account for user: " + username);
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int userId = -1;
            PreparedStatement psUserId = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ?"
            );
            psUserId.setString(1, username);
            ResultSet rs = psUserId.executeQuery();
            
            if (!rs.next()) {
                request.setAttribute("message", "User not found.");
                request.getRequestDispatcher("home.jsp").forward(request, response);
                return;
            }
            
            userId = rs.getInt("id");
            System.out.println("Found user ID: " + userId);
            psUserId.close();

            try {
                PreparedStatement psTokens = conn.prepareStatement(
                    "DELETE FROM password_reset_tokens WHERE user_id = ? OR created_by = ?"
                );
                psTokens.setInt(1, userId);
                psTokens.setInt(2, userId);
                int tokensDeleted = psTokens.executeUpdate();
                System.out.println("Deleted " + tokensDeleted + " password reset tokens");
                psTokens.close();
            } catch (SQLException e) {
                System.out.println("Note: Could not delete from password_reset_tokens: " + e.getMessage());
            }

            try {
                PreparedStatement psRoles = conn.prepareStatement(
                    "DELETE FROM user_roles WHERE user_id = ?"
                );
                psRoles.setInt(1, userId);
                int rolesDeleted = psRoles.executeUpdate();
                System.out.println("Deleted " + rolesDeleted + " user role assignments");
                psRoles.close();
            } catch (SQLException e) {
                System.out.println("Note: Could not delete from user_roles: " + e.getMessage());
            }

            PreparedStatement psHistory = conn.prepareStatement(
                "DELETE FROM password_history WHERE user_id = ?"
            );
            psHistory.setInt(1, userId);
            int historyDeleted = psHistory.executeUpdate();
            System.out.println("Deleted " + historyDeleted + " password history records");
            psHistory.close();

            PreparedStatement psUser = conn.prepareStatement(
                "DELETE FROM users WHERE id = ?"
            );
            psUser.setInt(1, userId);
            int deleted = psUser.executeUpdate();
            System.out.println("Deleted user record: " + deleted);
            psUser.close();

            if (deleted > 0) {
                conn.commit();
                System.out.println("Account deletion successful for: " + username);
                session.invalidate();
                response.sendRedirect("login.jsp?message=Account+deleted+successfully!");
            } else {
                conn.rollback();
                System.err.println("Failed to delete user record");
                request.setAttribute("message", "Error deleting account!");
                request.getRequestDispatcher("home.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in DeleteMyAccountServlet: " + e.getMessage());
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            
            request.setAttribute("message", "Database error: " + e.getMessage());
            request.getRequestDispatcher("home.jsp").forward(request, response);
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

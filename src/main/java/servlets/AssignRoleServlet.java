package servlets;

import db.DBUtil;
import utils.PermissionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AssignRoleServlet")
public class AssignRoleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String currentUser = (session != null) ? (String) session.getAttribute("username") : null;

        if (currentUser == null) {
            request.setAttribute("message", "Access Denied: Please login.");
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            return;
        }

        boolean isAdmin = false;
        try {
            int currentUserId = PermissionUtil.getUserId(currentUser);
            isAdmin = PermissionUtil.hasRole(currentUserId, "ADMIN");
        } catch (SQLException e) {
            request.setAttribute("message", "Error checking admin role: " + e.getMessage());
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            return;
        }

        if (!isAdmin) {
            request.setAttribute("message", "Access Denied: Only admins can assign roles.");
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            return;
        }

        String targetUsername = request.getParameter("targetUsername");
        String roleName = request.getParameter("roleName");
        String action = request.getParameter("action");

        if (targetUsername == null || roleName == null || action == null) {
            request.setAttribute("message", "Invalid request parameters.");
            request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            int targetUserId = PermissionUtil.getUserId(targetUsername);

            try {
                if ("assign".equalsIgnoreCase(action)) {
                    PermissionUtil.assignRole(conn, targetUserId, roleName.toUpperCase());
                    request.setAttribute("message", "Role '" + roleName + "' assigned to '" + targetUsername + "' successfully!");
                } else if ("remove".equalsIgnoreCase(action)) {
                    PermissionUtil.removeRole(conn, targetUserId, roleName.toUpperCase());
                    request.setAttribute("message", "Role '" + roleName + "' removed from '" + targetUsername + "' successfully!");
                } else {
                    request.setAttribute("message", "Unknown action: " + action);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("message", "Error: " + e.getMessage());
            }

        } catch (SQLException e) {
            request.setAttribute("message", "Database error: " + e.getMessage());
        }

        request.getRequestDispatcher("manageUsers.jsp").forward(request, response);
    }
}

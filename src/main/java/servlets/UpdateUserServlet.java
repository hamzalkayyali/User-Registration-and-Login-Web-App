package servlets;

import db.DBUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String oldUsername = (String) session.getAttribute("username");
        String newUsername = request.getParameter("newUsername").trim();

        if (newUsername.isEmpty()) {
            request.setAttribute("message", "Username cannot be empty!");
            request.getRequestDispatcher("editUser.jsp?username=" + oldUsername).forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkPs = conn.prepareStatement(
                    "SELECT id FROM users WHERE username=?")) {
                checkPs.setString(1, newUsername);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    request.setAttribute("message", "Username already exists!");
                    request.getRequestDispatcher("editUser.jsp?username=" + oldUsername).forward(request, response);
                    return;
                }
            }

            int updated;
            try (PreparedStatement updateUser = conn.prepareStatement(
                    "UPDATE users SET username=? WHERE username=?")) {
                updateUser.setString(1, newUsername);
                updateUser.setString(2, oldUsername);
                updated = updateUser.executeUpdate();
            }

            if (updated > 0) {
                conn.commit();
                session.setAttribute("username", newUsername);
                request.setAttribute("message", "Username updated successfully!");
            } else {
                conn.rollback();
                request.setAttribute("message", "Error: User not found or could not update username.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Database error: " + e.getMessage());
        }

        request.getRequestDispatcher("editUser.jsp?username=" + newUsername).forward(request, response);
    }
}

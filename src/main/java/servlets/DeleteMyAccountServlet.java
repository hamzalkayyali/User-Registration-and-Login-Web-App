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

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

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

            int userId = rs.getInt("id");

            PreparedStatement psHistory = conn.prepareStatement(
                "DELETE FROM password_history WHERE user_id = ?"
            );
            psHistory.setInt(1, userId);
            psHistory.executeUpdate();

            PreparedStatement psUser = conn.prepareStatement(
                "DELETE FROM users WHERE id = ?"
            );
            psUser.setInt(1, userId);
            int deleted = psUser.executeUpdate();

            if (deleted > 0) {
                conn.commit();
                session.invalidate();
                response.sendRedirect("login.jsp?message=Account deleted successfully!");
            } else {
                conn.rollback();
                request.setAttribute("message", "Error deleting account!");
                request.getRequestDispatcher("home.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Database error: " + e.getMessage());
            request.getRequestDispatcher("home.jsp").forward(request, response);
        }
    }
}


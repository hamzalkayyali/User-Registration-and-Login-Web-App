package servlets;

import db.DBUtil;
import utils.PasswordUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            request.setAttribute("message", "Please enter both username and password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, password_hash FROM users WHERE username=?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                request.setAttribute("message", "Invalid username or password.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            int userId = rs.getInt("id");
            String storedHash = rs.getString("password_hash");

            if (!PasswordUtil.verifyPassword(password, storedHash)) {
                request.setAttribute("message", "Invalid username or password.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
            ps = conn.prepareStatement(
                    "SELECT created_at FROM (SELECT created_at FROM password_history WHERE user_id=? ORDER BY created_at DESC) WHERE ROWNUM=1"
            );
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp lastChanged = rs.getTimestamp("created_at");
                long elapsedMillis = System.currentTimeMillis() - lastChanged.getTime();

                // 1 minute for testing: 60 * 1000 ms
				long ninetyDaysMillis = 90L * 24 * 60 * 60 * 1000;

				if (elapsedMillis > ninetyDaysMillis) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("userId", userId);
                    response.sendRedirect("forceChangePassword.jsp?message=Your+password+has+expired.+Please+change+it.");
                    return;
                }
            }
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("userId", userId);

            response.sendRedirect("home.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Unexpected error: " + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}

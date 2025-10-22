package servlets;

import db.DBUtil;
import utils.PasswordUtil;
import utils.PermissionUtil;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== RegisterServlet CALLED ===");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            request.setAttribute("message", "Username and password are required!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("message", "Username and password cannot be empty!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);


            try (PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM users WHERE username=?")) {
                checkPs.setString(1, username);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    request.setAttribute("message", "Username already exists!");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                    return;
                }
            }

            if (!PasswordUtil.isComplex(password)) {
                request.setAttribute("message", "Password must be at least 8 characters with uppercase, lowercase, number, and special character!");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            String hashedPassword = PasswordUtil.hashPassword(password);


            int userId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, username, password_hash) VALUES (users_seq.NEXTVAL, ?, ?)",
                    new String[]{"id"})) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    System.out.println("User created with ID: " + userId);
                }
            }

            if (userId != -1) {
                try (PreparedStatement psHist = conn.prepareStatement(
                        "INSERT INTO password_history (id, user_id, password_hash) VALUES (password_history_seq.NEXTVAL, ?, ?)")) {
                    psHist.setInt(1, userId);
                    psHist.setString(2, hashedPassword);
                    psHist.executeUpdate();
                    System.out.println("Password history created");
                }

                System.out.println("Assigning default USER role...");
                PermissionUtil.assignRole(conn, userId, "USER");
            }

            conn.commit();
            System.out.println("Registration successful for user: " + username);

            request.setAttribute("message", "Registration successful! You can now login.");
            request.getRequestDispatcher("login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in RegisterServlet: " + e.getMessage());
            request.setAttribute("message", "Database error: " + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}

package utils;

import db.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static boolean hasPermission(int userId, String permissionName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER_ROLES ur " +
                     "JOIN ROLE_PERMISSIONS rp ON ur.role_id = rp.role_id " +
                     "JOIN PERMISSIONS p ON rp.permission_id = p.id " +
                     "WHERE ur.user_id = ? AND p.permission_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, permissionName);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static List<String> getUserPermissions(int userId) throws SQLException {
        List<String> permissions = new ArrayList<>();
        String sql = "SELECT DISTINCT p.permission_name FROM USER_ROLES ur " +
                     "JOIN ROLE_PERMISSIONS rp ON ur.role_id = rp.role_id " +
                     "JOIN PERMISSIONS p ON rp.permission_id = p.id " +
                     "WHERE ur.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) permissions.add(rs.getString("permission_name"));
        }
        return permissions;
    }

    public static List<String> getUserRoles(int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.role_name FROM USER_ROLES ur " +
                     "JOIN ROLES r ON ur.role_id = r.id " +
                     "WHERE ur.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) roles.add(rs.getString("role_name"));
        }
        return roles;
    }

    public static boolean hasRole(int userId, String... roleNames) throws SQLException {
        List<String> userRoles = getUserRoles(userId);
        for (String roleName : roleNames) {
            if (userRoles.contains(roleName)) return true;
        }
        return false;
    }

    public static int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        throw new SQLException("User not found");
    }

    public static void assignRole(Connection conn, int userId, String roleName) throws SQLException {
        String sql = "INSERT INTO USER_ROLES (user_id, role_id) " +
                     "SELECT ?, id FROM ROLES WHERE UPPER(role_name) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, roleName);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Role '" + roleName + "' not found.");
        }
    }

    public static void assignRole(int userId, String roleName) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(true);
            assignRole(conn, userId, roleName);
        }
    }

    public static void removeRole(Connection conn, int userId, String roleName) throws SQLException {
        String sql = "DELETE FROM USER_ROLES WHERE user_id = ? " +
                     "AND role_id = (SELECT id FROM ROLES WHERE UPPER(role_name) = UPPER(?))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, roleName);
            ps.executeUpdate();
        }
    }

    public static void removeRole(int userId, String roleName) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(true);
            removeRole(conn, userId, roleName);
        }
    }
}

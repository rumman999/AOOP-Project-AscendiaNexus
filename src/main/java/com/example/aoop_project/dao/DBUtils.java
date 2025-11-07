package com.example.aoop_project.dao;

import java.sql.*;
import java.util.List;

public class DBUtils {
    // ⚠️ Replace "java_user_database" with your actual DB name in phpMyAdmin
    private static final String URL = "jdbc:mysql://localhost:4306/java_user_database";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Save a message into the DB (private, group, or broadcast)
     */
    public static void saveMessage(int senderId, Integer receiverId, Integer groupId, String text) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, group_id, message_text) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);

            if (receiverId != null) stmt.setInt(2, receiverId);
            else stmt.setNull(2, Types.INTEGER);

            if (groupId != null) stmt.setInt(3, groupId);
            else stmt.setNull(3, Types.INTEGER);

            stmt.setString(4, text);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
    }

    /**
     * Get last N private messages between two users
     */
    public static ResultSet getPrivateMessages(int userId1, int userId2, int limit, Timestamp lastLoadedTimestamp) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.*, u.full_name AS sender_name " +
                        "FROM messages m " +
                        "JOIN user u ON m.sender_id = u.id " +
                        "WHERE (m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?) " +
                        "ORDER BY m.timestamp ASC LIMIT ?"   // ✅ FIXED (ASC instead of DESC)
        );
        stmt.setInt(1, userId1);
        stmt.setInt(2, userId2);
        stmt.setInt(3, userId2);
        stmt.setInt(4, userId1);
        stmt.setInt(5, limit);
        return stmt.executeQuery();
    }

    /**
     * Get last N messages from a group chat
     */
    public static ResultSet getGroupMessages(int groupId, int limit) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.*, u.full_name AS sender_name " +
                        "FROM messages m " +
                        "JOIN user u ON m.sender_id = u.id " +
                        "WHERE m.group_id=? " +
                        "ORDER BY m.timestamp ASC LIMIT ?"   // ✅ FIXED (ASC instead of DESC)
        );
        stmt.setInt(1, groupId);
        stmt.setInt(2, limit);
        return stmt.executeQuery();
    }

    /**
     * Get last N broadcast messages
     */
    public static ResultSet getBroadcastMessages(int limit) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.*, u.full_name AS sender_name " +
                        "FROM messages m " +
                        "JOIN user u ON m.sender_id = u.id " +
                        "WHERE m.receiver_id IS NULL AND m.group_id IS NULL " +
                        "ORDER BY m.timestamp ASC LIMIT ?"   // ✅ FIXED (ASC instead of DESC)
        );
        stmt.setInt(1, limit);
        return stmt.executeQuery();
    }

    /**
     * Get all groups for a user
     */
    public static ResultSet getUserGroups(int userId) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT g.id, g.name " +
                        "FROM groups g " +
                        "JOIN group_members gm ON g.id = gm.group_id " +
                        "WHERE gm.user_id=?"
        );
        stmt.setInt(1, userId);
        return stmt.executeQuery();
    }

    public static ResultSet getAllOtherUsers(int currentUserId) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, full_name FROM user WHERE id <> ?"
        );
        stmt.setInt(1, currentUserId);
        return stmt.executeQuery();
    }

    public static int createGroup(String name, int creatorId, List<Integer> memberIds) throws Exception {
        String sqlGroup = "INSERT INTO groups (name) VALUES (?)";
        String sqlMembers = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmtGroup = conn.prepareStatement(sqlGroup, Statement.RETURN_GENERATED_KEYS)) {

            // Insert group
            stmtGroup.setString(1, name);
            stmtGroup.executeUpdate();

            ResultSet rs = stmtGroup.getGeneratedKeys();
            if (rs.next()) {
                int groupId = rs.getInt(1);

                // Insert members (including creator)
                try (PreparedStatement stmtMembers = conn.prepareStatement(sqlMembers)) {
                    for (int memberId : memberIds) {
                        stmtMembers.setInt(1, groupId);
                        stmtMembers.setInt(2, memberId);
                        stmtMembers.addBatch();
                    }
                    stmtMembers.executeBatch();
                }

                return groupId;
            }
        }
        return -1;
    }

    public static ResultSet getGroupMembers(int groupId) throws SQLException {
        String sql = "SELECT user_id FROM group_members WHERE group_id = ?";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, groupId);
        return ps.executeQuery();
    }

    public static boolean isUserInGroup(int userId, int groupId) throws SQLException {
        String sql = "SELECT 1 FROM group_members WHERE user_id = ? AND group_id = ?";
        PreparedStatement ps = getConnection().prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, groupId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }



}
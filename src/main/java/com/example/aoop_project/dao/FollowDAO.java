package com.example.aoop_project.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowDAO {

    /**
     * Establishes a connection to the database.
     */
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        // Using connection details from your other DAO files
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:4306/java_user_database", "root", "");
    }

    /**
     * Checks if a user is already following another user.
     *
     * @param followerId  The ID of the user who might be following.
     * @param followingId The ID of the user who might be followed.
     * @return true if the follow relationship exists, false otherwise.
     */
    public boolean isFollowing(int followerId, int followingId) throws Exception {
        String sql = "SELECT COUNT(*) FROM followers WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, followerId);
            pst.setInt(2, followingId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Creates a new follow relationship in the database.
     *
     * @param followerId  The ID of the user who is following.
     * @param followingId The ID of the user being followed.
     */
    public void follow(int followerId, int followingId) throws Exception {
        String sql = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, followerId);
            pst.setInt(2, followingId);
            pst.executeUpdate();
        }
    }

    /**
     * Removes a follow relationship from the database.
     *
     * @param followerId  The ID of the user who is unfollowing.
     * @param followingId The ID of the user being unfollowed.
     */
    public void unfollow(int followerId, int followingId) throws Exception {
        String sql = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, followerId);
            pst.setInt(2, followingId);
            pst.executeUpdate();
        }
    }
}
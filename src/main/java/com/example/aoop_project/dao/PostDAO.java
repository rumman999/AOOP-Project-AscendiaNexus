package com.example.aoop_project.dao;

import com.example.aoop_project.models.Comment;
import com.example.aoop_project.models.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:4306/java_user_database", "root", "");
    }

    public void createPost(Post post) throws Exception {
        String sql = "INSERT INTO posts (user_id, caption, image_path) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, post.getUserId());
            pst.setString(2, post.getCaption());
            pst.setString(3, post.getImagePath());

            pst.executeUpdate();
        }
    }

    public List<Comment> getCommentsForPost(int postId) throws Exception {
        String sql = """
        SELECT c.comment_text, u.full_name AS user_name, c.created_at
        FROM post_comments c
        JOIN user u ON c.user_id = u.id
        WHERE c.post_id = ?
        ORDER BY c.created_at ASC
    """;
        List<Comment> comments = new ArrayList<>();
        try (var conn = getConnection();
             var pst = conn.prepareStatement(sql)) {
            pst.setInt(1, postId);
            try (var rs = pst.executeQuery()) {
                while (rs.next()) {
                    Comment cm = new Comment();
                    cm.setUserName(rs.getString("user_name"));
                    cm.setText(rs.getString("comment_text"));
                    cm.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    comments.add(cm);
                }
            }
        }
        return comments;
    }


    public List<Post> getAllPosts() throws Exception {
        String sql = """
            SELECT p.*, u.full_name as user_name
            FROM posts p 
            JOIN user u ON p.user_id = u.id 
            ORDER BY p.created_at DESC
        """;

        List<Post> posts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUserName(rs.getString("user_name"));
                post.setCaption(rs.getString("caption"));
                post.setImagePath(rs.getString("image_path"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setLikesCount(rs.getInt("likes_count"));
                post.setCommentsCount(rs.getInt("comments_count"));

                posts.add(post);
            }
        }

        return posts;
    }

    public boolean hasUserLikedPost(int postId, int userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, postId);
            pst.setInt(2, userId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void likePost(int postId, int userId) throws Exception {
        Connection conn = getConnection();
        conn.setAutoCommit(false);

        try {
            // Add like
            String insertLike = "INSERT INTO post_likes (post_id, user_id) VALUES (?, ?)";
            try (PreparedStatement pst1 = conn.prepareStatement(insertLike)) {
                pst1.setInt(1, postId);
                pst1.setInt(2, userId);
                pst1.executeUpdate();
            }

            // Update like count
            String updateCount = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?";
            try (PreparedStatement pst2 = conn.prepareStatement(updateCount)) {
                pst2.setInt(1, postId);
                pst2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    public void unlikePost(int postId, int userId) throws Exception {
        Connection conn = getConnection();
        conn.setAutoCommit(false);

        try {
            // Remove like
            String deleteLike = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement pst1 = conn.prepareStatement(deleteLike)) {
                pst1.setInt(1, postId);
                pst1.setInt(2, userId);
                pst1.executeUpdate();
            }

            // Update like count
            String updateCount = "UPDATE posts SET likes_count = likes_count - 1 WHERE id = ?";
            try (PreparedStatement pst2 = conn.prepareStatement(updateCount)) {
                pst2.setInt(1, postId);
                pst2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    public void addComment(int postId, int userId, String commentText) throws Exception {
        Connection conn = getConnection();
        conn.setAutoCommit(false);

        try {
            // Add comment
            String insertComment = "INSERT INTO post_comments (post_id, user_id, comment_text) VALUES (?, ?, ?)";
            try (PreparedStatement pst1 = conn.prepareStatement(insertComment)) {
                pst1.setInt(1, postId);
                pst1.setInt(2, userId);
                pst1.setString(3, commentText);
                pst1.executeUpdate();
            }

            // Update comment count
            String updateCount = "UPDATE posts SET comments_count = comments_count + 1 WHERE id = ?";
            try (PreparedStatement pst2 = conn.prepareStatement(updateCount)) {
                pst2.setInt(1, postId);
                pst2.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
}

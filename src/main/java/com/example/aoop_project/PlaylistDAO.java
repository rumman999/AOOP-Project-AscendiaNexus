package com.example.aoop_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private Connection getConn() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:4306/java_user_database","root","");
    }

    public List<String> findAllUrls() throws Exception {
        String sql = "SELECT url FROM playlists ORDER BY created_at DESC";
        try (var conn = getConn();
             var st = conn.createStatement();
             var rs = st.executeQuery(sql)) {
            List<String> urls = new ArrayList<>();
            while (rs.next()) urls.add(rs.getString("url"));
            return urls;
        }
    }

    public void create(String url, int adminId) throws Exception {
        String sql = "INSERT INTO playlists (url, created_by) VALUES (?, ?)";
        try (var conn = getConn();
             var pst = conn.prepareStatement(sql)) {
            pst.setString(1, url);
            pst.setInt(2, adminId);
            pst.executeUpdate();
        }
    }
}

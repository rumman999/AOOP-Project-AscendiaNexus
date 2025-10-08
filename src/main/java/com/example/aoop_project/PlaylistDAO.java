package com.example.aoop_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:4306/java_user_database","root","");
    }

    public List<Playlist> findAll() throws Exception {
        String sql = "SELECT name, url FROM playlists ORDER BY created_at DESC";
        try (var conn = getConn();
             var st = conn.createStatement();
             var rs = st.executeQuery(sql)) {
            List<Playlist> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Playlist(rs.getString("name"), rs.getString("url")));
            }
            return list;
        }
    }

    public void create(String name, String url, int adminId) throws Exception {
        String sql = "INSERT INTO playlists (name, url, created_by) VALUES (?,?,?)";
        try (var conn = getConn();
             var pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, url);
            pst.setInt(3, adminId);
            pst.executeUpdate();
        }
    }
}

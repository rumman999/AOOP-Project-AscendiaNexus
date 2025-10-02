package com.example.aoop_project;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    private static final String URL  = "jdbc:mysql://localhost:4306/java_user_database";
    private static final String USER = "root";
    private static final String PASS = "";

    // 1) Establish a DB connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // 2) Create a new job record
    public int create(Job job) throws SQLException {
        String sql = "INSERT INTO job(poster_id,title,description,location,salary_range) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, job.getPosterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getLocation());
            ps.setString(5, job.getSalaryRange());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return (rs.next() ? rs.getInt(1) : -1);
        }
    }

    // 3) Read all jobs, ordered by most recent
    public List<Job> findAll() throws SQLException {
        String sql = "SELECT * FROM job ORDER BY posted_at DESC";
        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setPosterId(rs.getInt("poster_id"));
                j.setTitle(rs.getString("title"));
                j.setDescription(rs.getString("description"));
                j.setLocation(rs.getString("location"));
                j.setSalaryRange(rs.getString("salary_range"));
                Timestamp ts = rs.getTimestamp("posted_at");
                j.setPostedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
                list.add(j);
            }
        }
        return list;
    }

    // 4) Update an existing job
    public boolean update(Job job) throws SQLException {
        String sql = "UPDATE job SET title=?, description=?, location=?, salary_range=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, job.getTitle());
            ps.setString(2, job.getDescription());
            ps.setString(3, job.getLocation());
            ps.setString(4, job.getSalaryRange());
            ps.setInt(5, job.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // 5) Delete a job by ID
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM job WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

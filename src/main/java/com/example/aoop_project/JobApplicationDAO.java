package com.example.aoop_project;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobApplicationDAO {
    private static final String URL  = "jdbc:mysql://localhost:4306/java_user_database";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    public void create(JobApplication application) throws SQLException {
        String sql = "INSERT INTO job_applications (job_id, applicant_id, applied_at, status, cover_letter) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, application.getJobId());
            pst.setInt(2, application.getApplicantId());
            pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(4, "pending");
            pst.setString(5, application.getCoverLetter());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) application.setId(rs.getInt(1));
            }
        }
    }

    public List<JobApplication> findByJobId(int jobId) throws SQLException {
        String sql =
                "SELECT ja.*, j.title AS job_title, u.full_name AS applicant_name, u.email AS applicant_email " +
                        "FROM job_applications ja " +
                        "JOIN job j ON ja.job_id = j.id " +
                        "JOIN `user` u ON ja.applicant_id = u.id " +
                        "WHERE ja.job_id = ? " +
                        "ORDER BY ja.applied_at DESC";
        List<JobApplication> applications = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, jobId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) applications.add(mapResultSetToApplication(rs));
            }
        }
        return applications;
    }

    public List<JobApplication> findByApplicantId(int applicantId) throws SQLException {
        String sql =
                "SELECT ja.*, j.title AS job_title, u.full_name AS applicant_name, u.email AS applicant_email " +
                        "FROM job_applications ja " +
                        "JOIN job j ON ja.job_id = j.id " +
                        "JOIN `user` u ON ja.applicant_id = u.id " +
                        "WHERE ja.applicant_id = ? " +
                        "ORDER BY ja.applied_at DESC";
        List<JobApplication> applications = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, applicantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) applications.add(mapResultSetToApplication(rs));
            }
        }
        return applications;
    }

    public boolean hasUserApplied(int jobId, int applicantId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM job_applications WHERE job_id = ? AND applicant_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, jobId);
            pst.setInt(2, applicantId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void updateStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE job_applications SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, applicationId);
            pst.executeUpdate();
        }
    }

    private JobApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        JobApplication app = new JobApplication();
        app.setId(rs.getInt("id"));
        app.setJobId(rs.getInt("job_id"));
        app.setApplicantId(rs.getInt("applicant_id"));
        app.setStatus(rs.getString("status"));
        app.setCoverLetter(rs.getString("cover_letter"));
        app.setJobTitle(rs.getString("job_title"));
        app.setApplicantName(rs.getString("applicant_name"));
        app.setApplicantEmail(rs.getString("applicant_email"));
        Timestamp ts = rs.getTimestamp("applied_at");
        if (ts != null) app.setAppliedAt(ts.toLocalDateTime());
        return app;
    }
}

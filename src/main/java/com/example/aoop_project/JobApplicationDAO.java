package com.example.aoop_project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique file names

public class JobApplicationDAO {
    private static final String URL = "jdbc:mysql://10.15.4.66:4306/java_user_database";
    private static final String USER = "root";
    private static final String PASS = "";
    // Define a directory for CV storage
    private static final String CV_STORAGE_DIR = "cv_uploads";

    public JobApplicationDAO() {
        // Ensure the CV storage directory exists
        Path path = Paths.get(CV_STORAGE_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Failed to create CV storage directory: " + e.getMessage());
                // Handle this error appropriately in a real application
            }
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    // Method to save the CV file and return its path
    public String saveCvFile(File cvFile) throws IOException {
        if (cvFile == null) {
            return null;
        }
        String fileName = UUID.randomUUID().toString() + "_" + cvFile.getName();
        Path destination = Paths.get(CV_STORAGE_DIR, fileName);
        Files.copy(cvFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    public void create(JobApplication application) throws SQLException {
        // Updated SQL to include cv_path
        String sql = "INSERT INTO job_applications (job_id, applicant_id, applied_at, status, cover_letter, cv_path) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, application.getJobId());
            pst.setInt(2, application.getApplicantId());
            pst.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(4, "pending");
            pst.setString(5, application.getCoverLetter());
            pst.setString(6, application.getCvPath()); // Set CV path

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    application.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<JobApplication> findByJobId(int jobId) throws SQLException {
        // Updated SQL to select cv_path
        String sql = """
            SELECT ja.*, j.title as job_title, u.full_name as applicant_name, u.email as applicant_email
            FROM job_applications ja
            JOIN jobs j ON ja.job_id = j.id
            JOIN user u ON ja.applicant_id = u.id
            WHERE ja.job_id = ?
            ORDER BY ja.applied_at DESC
            """;

        List<JobApplication> applications = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, jobId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToApplication(rs));
                }
            }
        }
        return applications;
    }

    public List<JobApplication> findByApplicantId(int applicantId) throws SQLException {
        // Updated SQL to select cv_path
        String sql = """
            SELECT ja.*, j.title as job_title, u.full_name as applicant_name, u.email as applicant_email
            FROM job_applications ja
            JOIN jobs j ON ja.job_id = j.id
            JOIN user u ON ja.applicant_id = u.id
            WHERE ja.applicant_id = ?
            ORDER BY ja.applied_at DESC
            """;

        List<JobApplication> applications = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, applicantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSetToApplication(rs));
                }
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
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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
        app.setCvPath(rs.getString("cv_path")); // Retrieve CV path
        app.setJobTitle(rs.getString("job_title"));
        app.setApplicantName(rs.getString("applicant_name"));
        app.setApplicantEmail(rs.getString("applicant_email"));

        Timestamp timestamp = rs.getTimestamp("applied_at");
        if (timestamp != null) {
            app.setAppliedAt(timestamp.toLocalDateTime());
        }

        return app;
    }
}
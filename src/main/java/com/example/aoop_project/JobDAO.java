package com.example.aoop_project;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    private static final String URL = "jdbc:mysql://localhost:4306/java_user_database";
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

    public void create(Job job) throws SQLException {
        String sql = "INSERT INTO jobs (poster_id, title, company, posted_by, description, location, salary_range, tech_stack, job_type, requirements, posted_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, job.getPosterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getCompany());      // NEW
            ps.setString(4, job.getPostedBy());     // NEW
            ps.setString(5, job.getDescription());
            ps.setString(6, job.getLocation());
            ps.setString(7, job.getSalaryRange());
            ps.setString(8, job.getTechStack());
            ps.setString(9, job.getJobType());
            ps.setString(10, job.getRequirements());
            ps.setTimestamp(11, Timestamp.valueOf(job.getPostedAt()));
            ps.executeUpdate();

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    job.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Job> findAll() throws SQLException {
        String sql = "SELECT * FROM jobs ORDER BY posted_at DESC";
        List<Job> jobs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        }
        return jobs;
    }

    public List<Job> findByPosterId(int posterId) throws SQLException {
        String sql = "SELECT * FROM jobs WHERE poster_id = ? ORDER BY posted_at DESC";
        List<Job> jobs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, posterId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        }
        return jobs;
    }

    public Job findById(int id) throws SQLException {
        String sql = "SELECT * FROM jobs WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToJob(rs);
                }
            }
        }
        return null;
    }

    public void update(Job job) throws SQLException {
        String sql = "UPDATE jobs SET title = ?, company = ?, posted_by = ?, description = ?, location = ?, salary_range = ?, tech_stack = ?, job_type = ?, requirements = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, job.getTitle());
            pst.setString(2, job.getCompany());      // NEW
            pst.setString(3, job.getPostedBy());     // NEW
            pst.setString(4, job.getDescription());
            pst.setString(5, job.getLocation());
            pst.setString(6, job.getSalaryRange());
            pst.setString(7, job.getTechStack());
            pst.setString(8, job.getJobType());
            pst.setString(9, job.getRequirements());
            pst.setInt(10, job.getId());

            pst.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM jobs WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<Job> searchJobs(String keyword, String jobType, String location, String techStack) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String keywordPattern = "%" + keyword.trim() + "%";
            params.add(keywordPattern);
            params.add(keywordPattern);
        }

        if (jobType != null && !jobType.trim().isEmpty()) {
            sql.append(" AND job_type LIKE ?");
            params.add("%" + jobType.trim() + "%");
        }

        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location.trim() + "%");
        }

        if (techStack != null && !techStack.trim().isEmpty()) {
            sql.append(" AND tech_stack LIKE ?");
            params.add("%" + techStack.trim() + "%");
        }

        sql.append(" ORDER BY posted_at DESC");

        List<Job> jobs = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        }
        return jobs;
    }

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setTitle(rs.getString("title"));
        job.setCompany(rs.getString("company"));       // NEW
        job.setPostedBy(rs.getString("posted_by"));    // NEW
        job.setDescription(rs.getString("description"));
        job.setLocation(rs.getString("location"));
        job.setSalaryRange(rs.getString("salary_range"));
        job.setTechStack(rs.getString("tech_stack"));
        job.setJobType(rs.getString("job_type"));
        job.setRequirements(rs.getString("requirements"));
        job.setPostedAt(rs.getTimestamp("posted_at").toLocalDateTime());
        return job;
    }

}

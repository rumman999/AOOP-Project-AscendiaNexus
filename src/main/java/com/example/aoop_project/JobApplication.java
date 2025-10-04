package com.example.aoop_project;

import java.time.LocalDateTime;

public class JobApplication {
    private int id;
    private int jobId;
    private int applicantId;
    private LocalDateTime appliedAt;
    private String status;
    private String coverLetter;

    // Additional fields for display purposes
    private String jobTitle;
    private String applicantName;
    private String applicantEmail;

    // Constructors
    public JobApplication() {}

    public JobApplication(int id, int jobId, int applicantId, LocalDateTime appliedAt,
                          String status, String coverLetter) {
        this.id = id;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.appliedAt = appliedAt;
        this.status = status;
        this.coverLetter = coverLetter;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getApplicantId() { return applicantId; }
    public void setApplicantId(int applicantId) { this.applicantId = applicantId; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
}

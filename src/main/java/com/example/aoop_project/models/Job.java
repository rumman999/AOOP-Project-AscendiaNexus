package com.example.aoop_project.models;

import java.time.LocalDateTime;

public class Job {
    private int id;
    private int posterId;
    private String title;
    private String company;       // NEW
    private String postedBy;      // NEW
    private String description;
    private String location;
    private String salaryRange;
    private String techStack;
    private String jobType;
    private String requirements;
    private LocalDateTime postedAt;

    // Constructors
    public Job() {}

    public Job(int id, int posterId, String title, String company, String postedBy, String description,
               String location, String salaryRange, String techStack, String jobType,
               String requirements, LocalDateTime postedAt) {
        this.id = id;
        this.posterId = posterId;
        this.title = title;
        this.company = company;         // NEW
        this.postedBy = postedBy;       // NEW
        this.description = description;
        this.location = location;
        this.salaryRange = salaryRange;
        this.techStack = techStack;
        this.jobType = jobType;
        this.requirements = requirements;
        this.postedAt = postedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPosterId() { return posterId; }
    public void setPosterId(int posterId) { this.posterId = posterId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }          // NEW
    public void setCompany(String company) { this.company = company; }  // NEW

    public String getPostedBy() { return postedBy; }        // NEW
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; } // NEW

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", posterId=" + posterId +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +       // NEW
                ", postedBy='" + postedBy + '\'' +     // NEW
                ", location='" + location + '\'' +
                ", jobType='" + jobType + '\'' +
                ", postedAt=" + postedAt +
                '}';
    }
}

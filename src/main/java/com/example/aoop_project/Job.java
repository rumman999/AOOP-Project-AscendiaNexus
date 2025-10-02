package com.example.aoop_project;

import java.time.LocalDateTime;

public class Job {
    private int id;
    private int posterId;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private LocalDateTime postedAt;
    // getters and setters


    public void setId(int id) {
        this.id = id;
    }

    public void setPosterId(int posterId) {
        this.posterId = posterId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public int getId() {
        return id;
    }

    public int getPosterId() {
        return posterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }
}

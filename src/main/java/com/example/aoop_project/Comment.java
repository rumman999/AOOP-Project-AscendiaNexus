package com.example.aoop_project;

import java.time.LocalDateTime;

public class Comment {
    private String userName;
    private String text;
    private LocalDateTime createdAt;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

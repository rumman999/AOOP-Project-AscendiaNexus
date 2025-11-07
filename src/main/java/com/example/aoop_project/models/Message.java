package com.example.aoop_project.models;

public class Message {
    private int senderId;
    private Integer receiverId; // nullable for group or broadcast
    private Integer groupId;    // nullable for private or broadcast
    private String text;
    private String senderName;
    private long timestamp;

    public Message(int senderId, String senderName, Integer receiverId, Integer groupId, String text, long timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Integer getReceiverId() { return receiverId; }
    public Integer getGroupId() { return groupId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
}
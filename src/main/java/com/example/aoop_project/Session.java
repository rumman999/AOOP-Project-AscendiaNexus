package com.example.aoop_project;

public class Session {
    private static int loggedInUserId = 0;
    private static String loggedInUserEmail;
    private static String loggedInUserName;
    private static String loggedInUserPassword;
    private static String pic="";
    private static int profileToViewId = -1; // -1 means no specific profile is set

    // --- UPDATED: Fields to automatically open a chat window ---
    private static int chatTargetId = -1;
    private static String chatTargetName;
    // ---------------------------------------------------------

    private static String s;

    // ===== User ID =====
    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    // ===== User Email =====
    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }
    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    // ===== User Name =====
    public static void setLoggedInUserName(String name) {
        loggedInUserName = name;
    }
    public static String getLoggedInUserName() {
        return loggedInUserName;
    }

    public static String getPic(){ return pic; }

    // ===== Profile to View =====
    public static void setProfileToViewId(int id) {
        profileToViewId = id;
    }
    public static int getProfileToViewId() {
        return profileToViewId;
    }

    // ===== UPDATED: Chat Target =====
    public static void setChatTarget(int id, String name) {
        chatTargetId = id;
        chatTargetName = name;
    }
    public static int getChatTargetId() {
        return chatTargetId;
    }
    public static String getChatTargetName() {
        return chatTargetName;
    }
    public static void clearChatTarget() {
        chatTargetId = -1;
        chatTargetName = null;
    }
    // =============================

    // ===== Clear session =====
    public static void clear() {
        loggedInUserId = 0;          // reset to default
        loggedInUserEmail = null;
        loggedInUserName = null;
        profileToViewId = -1; // Reset on logout
        clearChatTarget(); // Reset on logout
    }

    public static void setLoggedInUserType(String accountType) {
        s=accountType;
    }
    public static String getLoggedInUserType(){
        return s;
    }
}
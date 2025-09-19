package com.example.aoop_project;

public class Session {
    private static int loggedInUserId;        // ✅ new field for user ID
    private static String loggedInUserEmail;
    private static String loggedInUserName;
    private static String loggedInUserPassword;

    // ===== User ID =====
    public static void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    // ===== Email =====
    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    // ===== Full Name =====
    public static void setLoggedInUserName(String name) {
        loggedInUserName = name;
    }

    public static String getLoggedInUserName() {
        return loggedInUserName;
    }

    public static String getLoggedInUserPassword(){
        return loggedInUserPassword;
    }

    public static void setLoggedInUserPassword(String pass){
        loggedInUserPassword=pass;
    }

    // ===== Clear session =====
    public static void clear() {
        loggedInUserId = 0;          // reset to default
        loggedInUserEmail = null;
        loggedInUserName = null;
    }
}

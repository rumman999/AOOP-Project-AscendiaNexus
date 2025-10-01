package com.example.aoop_project;

public class Session {
    private static int loggedInUserId;        // ✅ new field for user ID
    private static String loggedInUserEmail;
    private static String loggedInUserName;
    private static String loggedInUserPassword;
    private static String pic="";
    private static String s;
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

    public static void setPic(String s){  pic = s; }

    public static String getPic(){ return pic; }

    // ===== Clear session =====
    public static void clear() {
        loggedInUserId = 0;          // reset to default
        loggedInUserEmail = null;
        loggedInUserName = null;
    }

    public static void setLoggedInUserType(String accountType) {
        s=accountType;
    }

    public static String getLoggedInUserType(){
        return s;
    }
}

package com.example.aoop_project;

public class Session {
    private static String loggedInUserEmail;
    private static String loggedInUserName; // ✅ new field for full_name

    public static void setLoggedInUserEmail(String email) {
        loggedInUserEmail = email;
    }

    public static String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

    public static void setLoggedInUserName(String name) {   // ✅ setter
        loggedInUserName = name;
    }

    public static String getLoggedInUserName() {           // ✅ getter
        return loggedInUserName;
    }

    public static void clear() {
        loggedInUserEmail = null;
        loggedInUserName = null; // ✅ clear name too
    }
}

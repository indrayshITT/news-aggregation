package com.newsaggregation.util;

public class UserSession {
	private static int id;
	private static boolean loggedIn = false;
    private static String username;
    private static boolean isAdmin;

    public static void setUser(int userId, String uname, boolean admin) {
    	id = userId;
        loggedIn = true;
        username = uname;
        isAdmin = admin;
    }
    
    public static int getUserId() {
    	return id;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static String getUsername() {
        return username;
    }

    public static void logout() {
        loggedIn = false;
        username = null;
        isAdmin = false;
    }
}

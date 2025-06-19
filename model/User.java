package com.newsaggregation.model;

import java.sql.Timestamp;

public class User {
	private final int id;
    private final String username;
    private final String email;
    private final String password;
    private final int roleId;
    private final Timestamp lastViewedNotificationsAt;

    public User(int id, String username, String email, String password, int roleId, Timestamp lastViewedNotificationsAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.lastViewedNotificationsAt = lastViewedNotificationsAt;
    }
    
    public User(String username, String email, String password, int roleId) {
        this(0, username, email, password, roleId, null);
    }

    public User(String username, String email, String password, int roleId, Timestamp lastViewedNotificationsAt) {
        this(0, username, email, password, roleId, lastViewedNotificationsAt);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRoleId() {
        return roleId;
    }
    
    public Timestamp getLastViewedNotificationsAt() {
    	return lastViewedNotificationsAt;
    }
}

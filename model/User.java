package com.newsaggregation.model;

import java.sql.Timestamp;

public class User {
	private  int id;
    private  String username;
    private  String email;
    private  String password;
    private  int roleId;
    private  Timestamp lastViewedNotificationsAt;

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

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public void setLastViewedNotificationsAt(Timestamp lastViewedNotificationsAt) {
		this.lastViewedNotificationsAt = lastViewedNotificationsAt;
	}
    
    
}

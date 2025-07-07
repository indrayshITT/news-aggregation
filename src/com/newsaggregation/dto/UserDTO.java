package com.newsaggregation.dto;

import org.json.JSONObject;

public class UserDTO {
	private final String username;
    private final String email;
    private final String password;

    public UserDTO(String username, String password) {
        this(username, null, password);
    }

    public UserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String toSignupJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("email", email);
            obj.put("password", password);
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong while preparing your signup information.");
            return null;
        }
    }

    public String toLoginJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong while preparing your login information.");
            return null;
        }
    }

    public String getUsername() {
        return username;
    }
}

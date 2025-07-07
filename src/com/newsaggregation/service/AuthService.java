package com.newsaggregation.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.dto.UserDTO;
import com.newsaggregation.util.UserSession;

public class AuthService {

    public boolean register(UserDTO user) {
        try {
            String response = APIService.send("/api/auth/signup", "POST", user.toSignupJson());
            return handleApiResponse(response, "Signup");
        } catch (Exception e) {
            System.out.println("Signup failed. Please try again later.");
            return false;
        }
    }

    public boolean authenticate(UserDTO user) {
        try {
            String response = APIService.send("/api/auth/login", "POST", user.toLoginJson());
            JSONObject result = new JSONObject(response);

            if (response.contains("Login successful.")) {
                extractLoginResponse(result, user.getUsername());
                return true;
            } else {
                System.out.println("Login failed. " + result.optString("message", "Invalid credentials."));
                return false;
            }
        } catch (Exception e) {
            System.out.println("Login failed due to a system error. Please try again.");
            return false;
        }
    }

    private boolean handleApiResponse(String response, String context) {
        try {
            JSONObject json = new JSONObject(response);
            System.out.println(json.optString("message", context + " completed."));
            return true;
        } catch (Exception e) {
            System.out.println(context + " failed. Invalid response from server.");
            return false;
        }
    }

    private void extractLoginResponse(JSONObject result, String username) throws JSONException {
        int userId = result.getInt("userId");
        boolean isAdmin = result.getBoolean("isAdmin");
        UserSession.setUser(userId, username, isAdmin);
    }
}

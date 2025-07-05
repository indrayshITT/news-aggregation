package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class AuthClient {
	public static void signup(Scanner sc) throws JSONException {
        System.out.println("\n-- User Signup --");
        String username = InputUtil.readString(sc, "Enter username: ");
        String email = InputUtil.readString(sc, "Enter email: ");
        String password = InputUtil.readString(sc, "Enter password: ");

        JSONObject payload = new JSONObject();
        payload.put("username", username);
        payload.put("email", email);
        payload.put("password", password);

        String response = APIService.send("/api/auth/signup", "POST", payload.toString());
        System.out.println(response);
    }

    public static void login(Scanner sc) throws JSONException {
        System.out.println("\n-- Login --");
        String username = InputUtil.readString(sc, "Enter username: ");
        String password = InputUtil.readString(sc, "Enter password: ");

        JSONObject payload = new JSONObject();
        payload.put("username", username);
        payload.put("password", password);
        
        String result = APIService.send("/api/auth/login", "POST", payload.toString());
        JSONObject jsonObject = new JSONObject (result);
        if (result.contains("Login successful.")) {
            boolean isAdmin = jsonObject.getBoolean("isAdmin");
            UserSession.setUser(jsonObject.getInt("userId"), username, isAdmin);
        } else {
            System.out.println("Login failed: " + result);
        }
    }
}

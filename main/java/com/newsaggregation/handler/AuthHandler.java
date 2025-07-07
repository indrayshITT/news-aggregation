package com.newsaggregation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsaggregation.model.User;
import com.newsaggregation.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthHandler {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    public void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader(); PrintWriter out = resp.getWriter()) {
            User userDetail = gson.fromJson(reader, User.class);

            User user = new User(
                userDetail.getUsername(),
                userDetail.getEmail(),
                userDetail.getPassword(),
                2 // default role: user
            );

            userService.register(user);
            out.println("User registered successfully.");
        } catch (Exception e) {
            resp.getWriter().println("Signup failed: " + e.getMessage());
        }
    }

    public void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedReader reader = req.getReader(); PrintWriter out = resp.getWriter()) {
            User userDetail = gson.fromJson(reader, User.class);

            try {
                User user = userService.login(userDetail.getUsername(), userDetail.getPassword());
                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Login successful. Welcome, " + user.getUsername() + "!");
                responseJson.addProperty("userId", user.getId());
                responseJson.addProperty("isAdmin", user.getRoleId() == 1);

                out.println(responseJson.toString());

            } catch (Exception e) {
                JsonObject errorJson = new JsonObject();
                errorJson.addProperty("status", "error");
                errorJson.addProperty("message", "Login failed: " + e.getMessage());
                out.println(errorJson.toString());
            }
        } catch (Exception e) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", "Login failed: " + e.getMessage());
            resp.getWriter().println(errorJson.toString());
        }
    }

    public void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.getWriter().println("Logged out successfully.");
    }
}

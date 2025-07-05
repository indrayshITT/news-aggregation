package com.newsaggregation.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsaggregation.model.User;
import com.newsaggregation.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet{
	private final UserService userService = new UserService();
	private final Gson gson = new Gson();

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String action = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        if (action == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("Invalid auth route.");
            return;
        }

        switch (action) {
            case "/signup" -> handleSignup(req, out);
            case "/login" -> handleLogin(req, req.getSession(), out);
            case "/logout" -> {
                req.getSession().invalidate();
                out.println("Logged out successfully.");
            }
            default -> {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Invalid auth action: " + action);
            }
        }
    }

    private void handleSignup(HttpServletRequest req, PrintWriter out) {
        try {
            BufferedReader reader = req.getReader();
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
            out.println("Signup failed: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpSession session, PrintWriter out) {
    	Gson gson = new Gson();

        try (BufferedReader reader = req.getReader()) {
            User userDetail = gson.fromJson(reader, User.class);

            try {
                User user = userService.login(userDetail.getUsername(), userDetail.getPassword());
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
            out.println(errorJson.toString());
        }
    }
}

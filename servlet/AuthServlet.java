package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        if (action == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("Invalid auth route.");
            return;
        }

        switch (action) {
            case "/signup":
                handleSignup(req, out);
                break;
            case "/login":
                handleLogin(req, req.getSession(), out);
                break;
            case "/logout":
                req.getSession().invalidate();
                out.println("Logged out successfully.");
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Invalid auth action: " + action);
        }
    }

	private void handleSignup(HttpServletRequest req, PrintWriter out) {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        int roleId = 2; // default role (user)

        try {
            User user = new User(username, email, password, roleId);
            userService.register(user);
            out.println("User registered successfully.");
        } catch (Exception e) {
            out.println("Signup failed: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpSession session, PrintWriter out) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userService.login(username, password);
            session.setAttribute("user", user);
            out.println("Login successful. Welcome, " + user.getUsername() + "!");
        } catch (Exception e) {
            out.println("Login failed: " + e.getMessage());
        }
    }
}

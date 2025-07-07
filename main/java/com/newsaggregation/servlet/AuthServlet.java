package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import com.newsaggregation.handler.AuthHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final AuthHandler authHandler;

    public AuthServlet() {
        this.authHandler = new AuthHandler();
    }

    protected AuthServlet(AuthHandler handler) {
        this.authHandler = handler;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        if (action == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("Invalid auth route.");
            return;
        }

        switch (action) {
            case "/signup" -> authHandler.handleSignup(req, resp);
            case "/login" -> authHandler.handleLogin(req, resp);
            case "/logout" -> authHandler.handleLogout(req, resp);
            default -> {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Invalid auth action: " + action);
            }
        }
    }
}

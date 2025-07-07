package com.newsaggregation.servlet;

import com.newsaggregation.handler.NotificationHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/notifications")
public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final NotificationHandler handler;

    public NotificationServlet() {
        this.handler = new NotificationHandler();
    }

    protected NotificationServlet(NotificationHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handleGet(req, resp);
    }
}

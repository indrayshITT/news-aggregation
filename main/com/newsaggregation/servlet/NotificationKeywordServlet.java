package com.newsaggregation.servlet;

import com.newsaggregation.handler.NotificationKeywordHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/keywords")
public class NotificationKeywordServlet extends HttpServlet {
	private final NotificationKeywordHandler handler;

    public NotificationKeywordServlet() {
        this.handler = new NotificationKeywordHandler();
    }

    // For unit testing
    protected NotificationKeywordServlet(NotificationKeywordHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handleGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handlePost(req, resp);
    }
}

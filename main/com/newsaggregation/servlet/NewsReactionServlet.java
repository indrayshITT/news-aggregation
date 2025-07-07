package com.newsaggregation.servlet;

import java.io.IOException;

import com.newsaggregation.handler.NewsReactionHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/news/reaction")
public class NewsReactionServlet extends HttpServlet {
	private final NewsReactionHandler handler;

    public NewsReactionServlet() {
        this.handler = new NewsReactionHandler();
    }

    // Constructor for testing
    protected NewsReactionServlet(NewsReactionHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handlePost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handleGet(req, resp);
    }
}

package com.newsaggregation.servlet;

import com.newsaggregation.handler.NewsHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/news/*")
public class NewsServlet extends HttpServlet {
	 private final NewsHandler newsHandler;

	    public NewsServlet() {
	        this.newsHandler = new NewsHandler();
	    }

	    // Constructor for test injection
	    protected NewsServlet(NewsHandler handler) {
	        this.newsHandler = handler;
	    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            newsHandler.handleToggleVisibility(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        newsHandler.handleGetRequests(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}

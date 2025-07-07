package com.newsaggregation.servlet;

import com.newsaggregation.handler.SavedArticleHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/saved-articles")
public class SavedArticleServlet extends HttpServlet {
	private final SavedArticleHandler handler;

    public SavedArticleServlet() {
        this.handler = new SavedArticleHandler();
    }

    // Constructor for test injection
    protected SavedArticleServlet(SavedArticleHandler handler) {
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handler.handleDelete(req, resp);
    }
}

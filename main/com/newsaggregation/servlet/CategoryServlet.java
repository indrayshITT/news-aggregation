package com.newsaggregation.servlet;

import java.io.IOException;

import com.newsaggregation.handler.CategoryHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/categories/*")
public class CategoryServlet extends HttpServlet {
	private final CategoryHandler categoryHandler;

    public CategoryServlet() {
        this.categoryHandler = new CategoryHandler();
    }

    protected CategoryServlet(CategoryHandler handler) {
        this.categoryHandler = handler;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            categoryHandler.handlePatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        categoryHandler.handleGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        categoryHandler.handlePost(req, resp);
    }
}

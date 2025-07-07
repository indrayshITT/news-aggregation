package com.newsaggregation.servlet;

import com.newsaggregation.handler.UserCategoryHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "UserCategoryServlet", urlPatterns = {"/api/category/user"})
public class UserCategoryServlet extends HttpServlet {
	private final UserCategoryHandler handler;

    public UserCategoryServlet() {
        this.handler = new UserCategoryHandler();
    }

    protected UserCategoryServlet(UserCategoryHandler handler) {
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

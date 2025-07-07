package com.newsaggregation.servlet;

import java.io.IOException;

import com.newsaggregation.handler.ExternalServerHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/external-servers/*")
public class ExternalServerServlet extends HttpServlet {

	private final ExternalServerHandler handler;

    public ExternalServerServlet() {
        this.handler = new ExternalServerHandler();
    }

    // For unit testing
    protected ExternalServerServlet(ExternalServerHandler handler) {
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

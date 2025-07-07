package com.newsaggregation.servlet;

import java.io.IOException;

import com.newsaggregation.handler.BlockedKeywordHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/blocked-keywords")
public class BlockedKeywordServlet extends HttpServlet {

	private final BlockedKeywordHandler keywordHandler;

    public BlockedKeywordServlet() {
        this.keywordHandler = new BlockedKeywordHandler();
    }

    protected BlockedKeywordServlet(BlockedKeywordHandler keywordHandler) {
        this.keywordHandler = keywordHandler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        keywordHandler.handleGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        keywordHandler.handlePost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        keywordHandler.handleDelete(req, resp);
    }
}

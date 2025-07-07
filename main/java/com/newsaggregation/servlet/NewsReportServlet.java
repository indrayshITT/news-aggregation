package com.newsaggregation.servlet;

import java.io.IOException;

import com.newsaggregation.handler.NewsReportHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/news/report")
public class NewsReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final NewsReportHandler handler;

    public NewsReportServlet() {
        this.handler = new NewsReportHandler();
    }

    protected NewsReportServlet(NewsReportHandler handler) {
        this.handler = handler;
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handler.handleReport(req, resp);
	}
}

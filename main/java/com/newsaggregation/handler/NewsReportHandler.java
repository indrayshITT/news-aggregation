package com.newsaggregation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.newsaggregation.service.NewsReportService;
import com.newsaggregation.service.NewsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NewsReportHandler {

	private final NewsReportService newsReportService = new NewsReportService();
	private final NewsService newsService = new NewsService();

	public void handleReport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JsonObject responseJson = new JsonObject();

		try (BufferedReader reader = req.getReader()) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONObject json = new JSONObject(sb.toString());
			int userId = json.getInt("userId");
			int newsId = json.getInt("newsId");
			String reason = json.getString("reason");

			boolean success = newsReportService.report(userId, newsId, reason);
			newsService.autoHideIfReportedOverThreshold(newsId);

			if (success) {
				responseJson.addProperty("status", "success");
				responseJson.addProperty("message", "Report submitted successfully.");
			} else {
				responseJson.addProperty("status", "error");
				responseJson.addProperty("message", "Failed to submit report.");
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseJson.addProperty("status", "error");
			responseJson.addProperty("message", "Exception: " + e.getMessage());
		}

		out.println(responseJson);
	}
}

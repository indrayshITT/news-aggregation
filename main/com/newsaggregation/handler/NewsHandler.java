package com.newsaggregation.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newsaggregation.model.News;
import com.newsaggregation.service.NewsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class NewsHandler {
    private final NewsService newsService = new NewsService();

    public void handleGetRequests(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            List<News> results;

            switch (path == null ? "/search" : path) {
                case "/search" -> {
                    String[] keywordArray = req.getParameterValues("keyword");
                    results = newsService.searchNewsByKeywordsSortedByLikes(keywordArray);
                    out.println(convertNewsListToJson(results));
                }
                case "/today" -> {
                    results = newsService.getNewsSavedTodaySortedByLikes();
                    out.println(convertNewsListToJson(results));
                }
                case "/date-range" -> {
                    LocalDate start = LocalDate.parse(req.getParameter("start"));
                    LocalDate end = LocalDate.parse(req.getParameter("end"));
                    results = newsService.getNewsByDateRangeSortedByLikes(start, end);
                    out.println(convertNewsListToJson(results));
                }
                case "/date-range/category" -> {
                    LocalDate start = LocalDate.parse(req.getParameter("start"));
                    LocalDate end = LocalDate.parse(req.getParameter("end"));
                    int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                    results = newsService.getNewsByDateRangeAndCategorySortedByLikes(start, end, categoryId);
                    out.println(convertNewsListToJson(results));
                }
                case "/reported-news" -> {
                    List<News> reportedNews = newsService.getReportedNews();
                    JSONArray jsonArray = new JSONArray();
                    for (News news : reportedNews) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", news.getId());
                        obj.put("title", news.getTitle());
                        obj.put("isHidden", news.getIsHidden());
                        jsonArray.put(obj);
                    }
                    out.println(jsonArray.toString());
                }
                default -> {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = new JsonObject();
                    error.addProperty("status", "error");
                    error.addProperty("message", "Invalid news endpoint.");
                    out.println(error);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", e.getMessage());
            out.println(error);
        }
    }

    public void handleToggleVisibility(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();

        if (!"/toggle-visibility".equals(path)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"status\":\"error\",\"message\":\"Invalid endpoint\"}");
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            JsonObject body = JsonParser.parseReader(reader).getAsJsonObject();
            int newsId = body.get("newsId").getAsInt();
            boolean hide = body.get("hide").getAsBoolean();

            boolean success = newsService.setNewsHidden(newsId, hide);
            JsonObject res = new JsonObject();
            res.addProperty("status", success ? "success" : "error");
            res.addProperty("message", success ? "News updated successfully" : "Failed to update news");
            out.println(res);
        } catch (Exception e) {
            resp.setStatus(500);
            JsonObject res = new JsonObject();
            res.addProperty("status", "error");
            res.addProperty("message", e.getMessage());
            out.println(res);
        }
    }

    private JsonArray convertNewsListToJson(List<News> newsList) {
        JsonArray jsonResults = new JsonArray();

        for (News news : newsList) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", news.getId());
            obj.addProperty("title", news.getTitle());
            obj.addProperty("description", news.getContent());
            obj.addProperty("url", news.getUrl());
            obj.addProperty("source", news.getSource());
            obj.addProperty("date", news.getDate().toString());

            JsonArray categories = new JsonArray();
            for (String category : news.getCategories()) {
                categories.add(category);
            }
            obj.add("categories", categories);

            jsonResults.add(obj);
        }

        return jsonResults;
    }
}

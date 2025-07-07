package com.newsaggregation.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.News;
import com.newsaggregation.service.SavedArticleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SavedArticleHandler {
    private final SavedArticleService savedArticleService = new SavedArticleService();

    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            List<News> list = savedArticleService.getAllByUser(userId);

            JsonArray articles = new JsonArray();
            for (News news : list) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", news.getId());
                obj.addProperty("title", news.getTitle());
                obj.addProperty("description", news.getContent());
                obj.addProperty("url", news.getUrl());
                obj.addProperty("source", news.getSource());
                obj.addProperty("date", news.getDate().toString());

                JsonArray categoryArray = new JsonArray();
                for (String cat : news.getCategories()) {
                    categoryArray.add(cat);
                }
                obj.add("categories", categoryArray);

                articles.add(obj);
            }

            json.addProperty("status", "success");
            json.add("articles", articles);

        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error fetching saved articles: " + e.getMessage());
        }

        out.println(json.toString());
    }

    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            savedArticleService.save(userId, newsId);

            json.addProperty("status", "success");
            json.addProperty("message", "Article saved successfully.");
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error saving article: " + e.getMessage());
        }

        out.println(json.toString());
    }

    public void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject json = new JsonObject();

        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            savedArticleService.delete(userId, newsId);

            json.addProperty("status", "success");
            json.addProperty("message", "Article deleted successfully.");
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error deleting article: " + e.getMessage());
        }

        out.println(json.toString());
    }
}

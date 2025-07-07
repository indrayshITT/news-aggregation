package com.newsaggregation.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.service.NotificationKeywordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class NotificationKeywordHandler {
    private final NotificationKeywordService keywordService = new NotificationKeywordService();
    private final Gson gson = new Gson();

    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject response = new JsonObject();

        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            List<String> keywords = keywordService.getKeywordsByUser(userId);

            JsonArray keywordArray = new JsonArray();
            for (String keyword : keywords) {
                keywordArray.add(keyword);
            }

            response.addProperty("status", "success");
            response.add("keywords", keywordArray);
        } catch (Exception e) {
            response.addProperty("status", "error");
            response.addProperty("message", "Error: " + e.getMessage());
        }

        out.println(response);
    }

    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try (BufferedReader reader = req.getReader()) {
            JsonObject request = gson.fromJson(reader, JsonObject.class);
            String action = request.get("action").getAsString();
            int userId = request.get("userId").getAsInt();

            switch (action.toLowerCase()) {
                case "add" -> {
                    String keyword = request.get("keyword").getAsString();
                    keywordService.addKeyword(userId, keyword);
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Keyword added successfully.");
                }
                case "update" -> {
                    String oldKeyword = request.get("oldKeyword").getAsString();
                    String newKeyword = request.get("newKeyword").getAsString();
                    keywordService.updateKeyword(userId, oldKeyword, newKeyword);
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Keyword updated successfully.");
                }
                case "delete" -> {
                    String keyword = request.get("keyword").getAsString();
                    keywordService.deleteKeyword(userId, keyword);
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Keyword deleted successfully.");
                }
                default -> {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Invalid action.");
                }
            }
        } catch (Exception e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
        }

        out.println(jsonResponse);
    }
}

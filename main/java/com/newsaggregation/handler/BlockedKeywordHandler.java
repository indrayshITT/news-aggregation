package com.newsaggregation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newsaggregation.service.BlockedKeywordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlockedKeywordHandler {
    private final BlockedKeywordService keywordService = new BlockedKeywordService();

    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            List<String> keywords = keywordService.getAllBlockedKeywords();
            JsonArray array = new JsonArray();
            for (String keyword : keywords) {
                array.add(keyword);
            }

            JsonObject json = new JsonObject();
            json.addProperty("status", "success");
            json.add("keywords", array);
            out.println(json);
        } catch (Exception e) {
            sendError(resp, 500, "Failed to fetch blocked keywords", e.getMessage());
        }
    }

    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (BufferedReader reader = req.getReader(); PrintWriter out = resp.getWriter()) {
            String keyword = JsonParser.parseReader(reader).getAsJsonObject().get("keyword").getAsString();
            keywordService.addBlockedKeyword(keyword);

            JsonObject json = new JsonObject();
            json.addProperty("status", "success");
            json.addProperty("message", "Keyword blocked successfully");
            out.println(json);
        } catch (Exception e) {
            sendError(resp, 400, "Failed to block keyword", e.getMessage());
        }
    }

    public void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (BufferedReader reader = req.getReader(); PrintWriter out = resp.getWriter()) {
            String keyword = JsonParser.parseReader(reader).getAsJsonObject().get("keyword").getAsString();
            keywordService.removeBlockedKeyword(keyword);

            JsonObject json = new JsonObject();
            json.addProperty("status", "success");
            json.addProperty("message", "Keyword unblocked successfully");
            out.println(json);
        } catch (Exception e) {
            sendError(resp, 400, "Failed to unblock keyword", e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message, String details) throws IOException {
        resp.setStatus(statusCode);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status", "error");
        errorJson.addProperty("message", message + ": " + details);
        resp.getWriter().println(errorJson.toString());
    }
}

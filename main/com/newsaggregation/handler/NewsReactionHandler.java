package com.newsaggregation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsaggregation.model.User;
import com.newsaggregation.service.NewsReactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class NewsReactionHandler {
    private final NewsReactionService reactionService = new NewsReactionService();
    private final Gson gson = new Gson();

    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "User must be logged in.");
            out.println(error.toString());
            return;
        }

        try (BufferedReader reader = req.getReader()) {
            JsonObject body = gson.fromJson(reader, JsonObject.class);
            int newsId = body.get("newsId").getAsInt();
            String reaction = body.get("reaction").getAsString();

            if (!reaction.equalsIgnoreCase("like") && !reaction.equalsIgnoreCase("dislike")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Invalid reaction. Use 'like' or 'dislike'.");
                out.println(error.toString());
                return;
            }

            User user = (User) session.getAttribute("user");
            reactionService.react(user.getId(), newsId, reaction);

            JsonObject success = new JsonObject();
            success.addProperty("status", "success");
            success.addProperty("message", "Reaction recorded.");
            out.println(success.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Error: " + e.getMessage());
            out.println(error.toString());
        }
    }

    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            Map<String, Integer> summary = reactionService.getReactionSummary(newsId);

            JsonObject result = new JsonObject();
            result.addProperty("likes", summary.getOrDefault("like", 0));
            result.addProperty("dislikes", summary.getOrDefault("dislike", 0));

            out.println(result.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Error: " + e.getMessage());
            out.println(error.toString());
        }
    }
}

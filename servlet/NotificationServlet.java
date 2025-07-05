package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.News;
import com.newsaggregation.model.User;
import com.newsaggregation.service.NotificationService;
import com.newsaggregation.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/notifications")
public class NotificationServlet extends HttpServlet {
	private final NotificationService notificationService = new NotificationService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "You must be logged in to view notifications.");
            out.println(error.toString());
            return;
        }

        User user = (User) session.getAttribute("user");
        Timestamp lastViewed = user.getLastViewedNotificationsAt();
        Timestamp now = Timestamp.from(Instant.now());

        try {
            List<News> newsList = notificationService.getConsoleNotifications(user.getId(), lastViewed, now);
            JsonArray notifications = new JsonArray();

            for (News news : newsList) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", news.getId());
                obj.addProperty("title", news.getTitle());
                obj.addProperty("description", news.getContent());
                obj.addProperty("url", news.getUrl());
                obj.addProperty("source", news.getSource());
                obj.addProperty("date", news.getDate().toString());
                notifications.add(obj);
            }

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "success");
            responseJson.add("notifications", notifications);

            out.println(responseJson.toString());

            UserService userService = new UserService();
            userService.updateLastViewedTime(user.getId(), now);

        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Error fetching notifications: " + e.getMessage());
            out.println(error.toString());
        }
    }
}

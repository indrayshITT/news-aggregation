package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

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
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.println("You must be logged in to view notifications.");
            return;
        }

        User user = (User) session.getAttribute("user");
        Timestamp lastViewed = user.getLastViewedNotificationsAt();
        Timestamp now = Timestamp.from(Instant.now());

        try {
            List<News> newsList = notificationService.getConsoleNotifications(user.getId(), lastViewed, now);

            if (newsList.isEmpty()) {
                out.println("No new notifications since your last visit.");
            } else {
                for (News news : newsList) {
                    out.println("- " + news.getTitle() + " (" + news.getDate() + ")\n" + news.getUrl() + "\n");
                }
            }

            UserService userService = new UserService();
            userService.updateLastViewedTime(user.getId(), now);

        } catch (Exception e) {
            out.println("Error fetching notifications: " + e.getMessage());
        }
    }
}

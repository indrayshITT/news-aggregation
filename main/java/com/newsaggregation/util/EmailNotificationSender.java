package com.newsaggregation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NotificationDAO;
import com.newsaggregation.dao.UserDAO;
import com.newsaggregation.model.News;
import com.newsaggregation.model.User;

public class EmailNotificationSender {
	
	public static void sendNewsEmails(List<News> fetchedNews) {
        NotificationDAO notificationDAO = null;
        UserDAO userDAO = null;

        try {
            System.out.println("[EmailNotificationSender] Total fetched news articles: " + fetchedNews.size());

            notificationDAO = new NotificationDAO(new MySQLDatabaseConnection());
            userDAO = new UserDAO(new MySQLDatabaseConnection());

            List<User> users = userDAO.getAllUsers();
            System.out.println("[EmailNotificationSender] Total users found: " + users.size());

            for (User user : users) {
                List<News> filteredNews = notificationDAO.getFilteredNewsForUser(user.getId(), fetchedNews);
                System.out.println("[EmailNotificationSender] User: " + user.getEmail() + ", Matched news: " + filteredNews.size());

                if (filteredNews.isEmpty()) continue;

                Map<String, List<News>> newsByCategory = new HashMap<>();
                for (News news : filteredNews) {
                    for (String category : news.getCategories()) {
                        newsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(news);
                    }
                }

                StringBuilder emailBody = new StringBuilder();
                emailBody.append("Dear ").append(user.getUsername()).append(",\n\nHere are your latest news updates:\n\n");

                for (Map.Entry<String, List<News>> entry : newsByCategory.entrySet()) {
                    emailBody.append("Category: ").append(entry.getKey()).append("\n");
                    for (News news : entry.getValue()) {
                        emailBody.append("- ").append(news.getTitle()).append("\n  Description: ").append(news.getContent())
                        .append("\n  Source: ").append(news.getSource()).append("\n  URL: ").append(news.getUrl()).append("\n\n");
                    }
                }

                emailBody.append("\nRegards,\nNews Aggregation");

                System.out.println("[EmailNotificationSender] Sending email to: " + user.getEmail());
                System.out.println("[EmailNotificationSender] Email content preview:\n" + emailBody);
                EmailUtil.send(user.getEmail(), "Your News Digest", emailBody.toString());
            }

        } catch (Exception e) {
            System.err.println("[Error] Sending email failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (notificationDAO != null) notificationDAO.close();
                if (userDAO != null) userDAO.close();
            } catch (Exception e) {
                System.err.println("[Error] Failed to close resources: " + e.getMessage());
            }
        }
    }
}

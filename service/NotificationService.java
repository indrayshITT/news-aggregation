package com.newsaggregation.service;

import java.sql.Timestamp;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NotificationDAO;
import com.newsaggregation.model.News;

public class NotificationService {
	private final DatabaseConnection dbConnection;

    public NotificationService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }

    public List<News> getConsoleNotifications(int userId, Timestamp from, Timestamp to) throws Exception {
        try {
            NotificationDAO notificationDAO = new NotificationDAO(dbConnection);
            List<News> newsList = notificationDAO.getNewsForConsoleNotification(userId, from, to);
            notificationDAO.close();
            return newsList;
        } catch (Exception e) {
            throw new Exception("Failed to fetch console notifications: " + e.getMessage());
        }
    }
}

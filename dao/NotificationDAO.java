package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.News;

public class NotificationDAO {
	private final Connection connection;

    public NotificationDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public List<News> getNewsForConsoleNotification(int userId, Timestamp from, Timestamp to) throws SQLException {
    	 if (!hasAnyEnabledCategories(userId)) {
	        return getAllNewsBetween(from, to);
	    } else {
	        return getNotificationByCategoryPreference(userId, from, to);
	    }
    }
    
    private boolean hasAnyEnabledCategories(int userId) throws SQLException {
        String sql = "SELECT 1 FROM user_category_keywords WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    private List<News> getAllNewsBetween(Timestamp from, Timestamp to) throws SQLException {
        List<News> list = new ArrayList<>();
        String sql = "SELECT * FROM news WHERE date > ? AND date <= ? ORDER BY date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new News(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("url"),
                    rs.getString("source"),
                    rs.getTimestamp("date")
                ));
            }
        }
        return list;
    }

    
    private List<News> getNotificationByCategoryPreference(int userId, Timestamp from, Timestamp to) throws SQLException {
    	List<News> result = new ArrayList<>();
        String sql = """
            SELECT DISTINCT n.*
            FROM news n
            JOIN news_category nc ON n.id = nc.news_id
            JOIN user_category_keywords uck ON nc.category_id = uck.category_id
            WHERE uck.user_id = ?
              AND n.date > ? AND n.date <= ?
              AND (
                n.title LIKE CONCAT('%', uck.keyword, '%')
                OR n.content LIKE CONCAT('%', uck.keyword, '%')
              )
            ORDER BY n.date DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, from);
            stmt.setTimestamp(3, to);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new News(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("url"),
                    rs.getString("source"),
                    rs.getTimestamp("date")
                ));
            }
        }
        return result;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

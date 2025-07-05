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
        boolean hasCatPrefs = hasAnyEnabledCategories(userId);
        boolean hasGlobalKeywords = hasAnyEnabledGlobalKeywords(userId);

        List<News> result = new ArrayList<>();
        if (!hasCatPrefs && !hasGlobalKeywords) {
            return getAllNewsBetween(from, to);
        }
        if (hasCatPrefs) result.addAll(getNotificationByCategoryPreference(userId, from, to));
        if (hasGlobalKeywords) result.addAll(getNotificationByGlobalKeywords(userId, from, to));
        return result;
    }

    private boolean hasAnyEnabledCategories(int userId) throws SQLException {
        String sql = "SELECT 1 FROM user_categories WHERE user_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean hasAnyEnabledGlobalKeywords(int userId) throws SQLException {
        String sql = "SELECT 1 FROM notification_keywords WHERE user_id = ? AND enabled = true LIMIT 1";
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
                    rs.getString("description"),
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
            JOIN news_categories nc ON n.id = nc.news_id
            JOIN user_categories uck ON nc.category_id = uck.category_id
            WHERE uck.user_id = ?
              AND n.date > ? AND n.date <= ?
              AND (
                n.title LIKE CONCAT('%', uck.keyword, '%')
                OR n.description LIKE CONCAT('%', uck.keyword, '%')
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
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getString("source"),
                    rs.getTimestamp("date")
                ));
            }
        }
        return result;
    }

    private List<News> getNotificationByGlobalKeywords(int userId, Timestamp from, Timestamp to) throws SQLException {
        List<News> matchedNews = new ArrayList<>();

        String keywordSql = "SELECT keyword FROM notification_keywords WHERE user_id = ? AND enabled = true";
        List<String> keywords = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(keywordSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keywords.add(rs.getString("keyword"));
                }
            }
        }

        if (keywords.isEmpty()) return matchedNews;

        StringBuilder sql = new StringBuilder("SELECT * FROM news WHERE date > ? AND date <= ? AND (");
        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("LOWER(title) LIKE ? OR LOWER(description) LIKE ?");
        }
        sql.append(") ORDER BY date DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            int index = 3;
            for (String keyword : keywords) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                stmt.setString(index++, pattern);
                stmt.setString(index++, pattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matchedNews.add(new News(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("url"),
                        rs.getString("source"),
                        rs.getTimestamp("date")
                    ));
                }
            }
        }

        return matchedNews;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

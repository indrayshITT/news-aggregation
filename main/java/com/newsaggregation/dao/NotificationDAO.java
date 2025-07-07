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

    public List<News> getFilteredNewsForUser(int userId, List<News> fetchedNews) throws SQLException {
        List<News> result = new ArrayList<>();
        boolean hasCatPrefs = hasAnyEnabledCategories(userId);
        boolean hasGlobalKeywords = hasAnyEnabledGlobalKeywords(userId);

        if (!hasCatPrefs && !hasGlobalKeywords) return fetchedNews;

        for (News news : fetchedNews) {
            if (isBlocked(news)) continue;

            boolean matched = false;

            if (hasCatPrefs && matchesCategoryKeyword(userId, news)) matched = true;
            if (!matched && hasGlobalKeywords && matchesGlobalKeyword(userId, news)) matched = true;

            if (matched) result.add(news);
        }
        return result;
    }

    private boolean isBlocked(News news) throws SQLException {
        String sql = "SELECT 1 FROM blocked_keywords WHERE ? LIKE CONCAT('%', keyword, '%') OR ? LIKE CONCAT('%', keyword, '%')";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setString(1, news.getTitle() != null ? news.getTitle() : "");
        	selectStatement.setString(2, news.getContent() != null ? news.getContent() : "");
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean matchesCategoryKeyword(int userId, News news) throws SQLException {
        String sql = "SELECT uc.keyword FROM user_categories uc " +
                     "JOIN news_categories nc ON uc.category_id = nc.category_id " +
                     "JOIN categories c ON c.id = nc.category_id " +
                     "WHERE uc.user_id = ? AND uc.enabled = true AND nc.news_id = ? AND c.is_hidden = false";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
        	selectStatement.setInt(2, news.getId());
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    String keyword = resultSet.getString("keyword");
                    if (containsIgnoreCase(news.getTitle(), keyword) || containsIgnoreCase(news.getContent(), keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean matchesGlobalKeyword(int userId, News news) throws SQLException {
        String sql = "SELECT keyword FROM notification_keywords WHERE user_id = ? AND enabled = true";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    String keyword = resultSet.getString("keyword");
                    if (containsIgnoreCase(news.getTitle(), keyword) || containsIgnoreCase(news.getContent(), keyword)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        if (text == null || keyword == null) return false;
        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean hasAnyEnabledCategories(int userId) throws SQLException {
        String sql = "SELECT 1 FROM user_categories WHERE user_id = ? LIMIT 1";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            return resultSet.next();
        }
    }

    private boolean hasAnyEnabledGlobalKeywords(int userId) throws SQLException {
        String sql = "SELECT 1 FROM notification_keywords WHERE user_id = ? AND enabled = true LIMIT 1";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            return resultSet.next();
        }
    }

    private List<News> getAllNewsBetween(Timestamp from, Timestamp to) throws SQLException {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.* FROM news n " +
                     "LEFT JOIN news_categories nc ON n.id = nc.news_id " +
                     "LEFT JOIN categories c ON nc.category_id = c.id " +
                     "WHERE n.date > ? AND n.date <= ? " +
                     "AND (c.is_hidden IS NULL OR c.is_hidden = false) " +
                     "AND NOT EXISTS (SELECT 1 FROM blocked_keywords bk WHERE n.title LIKE CONCAT('%', bk.keyword, '%') OR n.description LIKE CONCAT('%', bk.keyword, '%')) " +
                     "ORDER BY n.date DESC";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setTimestamp(1, from);
        	selectStatement.setTimestamp(2, to);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                list.add(new News(
                	resultSet.getInt("id"),
                	resultSet.getString("title"),
                	resultSet.getString("description"),
                	resultSet.getString("url"),
                	resultSet.getString("source"),
                	resultSet.getTimestamp("date")
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
            JOIN categories c ON c.id = nc.category_id
            WHERE uck.user_id = ?
              AND c.is_hidden = false
              AND n.date > ? AND n.date <= ?
              AND (
                n.title LIKE CONCAT('%', uck.keyword, '%')
                OR n.description LIKE CONCAT('%', uck.keyword, '%')
              )
              AND NOT EXISTS (
                SELECT 1 FROM blocked_keywords bk
                WHERE n.title LIKE CONCAT('%', bk.keyword, '%')
                   OR n.description LIKE CONCAT('%', bk.keyword, '%')
              )
            ORDER BY n.date DESC
        """;

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
        	selectStatement.setTimestamp(2, from);
        	selectStatement.setTimestamp(3, to);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new News(
                	resultSet.getInt("id"),
                	resultSet.getString("title"),
                	resultSet.getString("description"),
                	resultSet.getString("url"),
                	resultSet.getString("source"),
                	resultSet.getTimestamp("date")
                ));
            }
        }
        return result;
    }

    private List<News> getNotificationByGlobalKeywords(int userId, Timestamp from, Timestamp to) throws SQLException {
        List<News> matchedNews = new ArrayList<>();

        String keywordSql = "SELECT keyword FROM notification_keywords WHERE user_id = ? AND enabled = true";
        List<String> keywords = new ArrayList<>();

        try (PreparedStatement selectStatement = connection.prepareStatement(keywordSql)) {
        	selectStatement.setInt(1, userId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    keywords.add(resultSet.getString("keyword"));
                }
            }
        }

        if (keywords.isEmpty()) return matchedNews;

        StringBuilder sql = new StringBuilder("SELECT n.* FROM news n " +
                "LEFT JOIN news_categories nc ON n.id = nc.news_id " +
                "LEFT JOIN categories c ON nc.category_id = c.id " +
                "WHERE n.date > ? AND n.date <= ? AND (c.is_hidden IS NULL OR c.is_hidden = false) AND (");

        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) sql.append(" OR ");
            sql.append("LOWER(n.title) LIKE ? OR LOWER(n.description) LIKE ?");
        }

        sql.append(") AND NOT EXISTS ( " +
                "SELECT 1 FROM blocked_keywords bk " +
                "WHERE n.title LIKE CONCAT('%', bk.keyword, '%') " +
                "OR n.description LIKE CONCAT('%', bk.keyword, '%') " +
                ") ORDER BY n.date DESC");

        try (PreparedStatement selectStatement = connection.prepareStatement(sql.toString())) {
        	selectStatement.setTimestamp(1, from);
        	selectStatement.setTimestamp(2, to);
            int index = 3;
            for (String keyword : keywords) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                selectStatement.setString(index++, pattern);
                selectStatement.setString(index++, pattern);
            }

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    matchedNews.add(new News(
                    	resultSet.getInt("id"),
                    	resultSet.getString("title"),
                    	resultSet.getString("description"),
                    	resultSet.getString("url"),
                    	resultSet.getString("source"),
                    	resultSet.getTimestamp("date")
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

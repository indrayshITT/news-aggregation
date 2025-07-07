package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.News;

public class NewsDAO {
	private static final int REPORT_THRESHOLD = 5;
	private final Connection connection;

    public NewsDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public boolean exists(String url) throws Exception {
        String sql = "SELECT id FROM news WHERE url = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setString(1, url);
            ResultSet resultSet = selectStatement.executeQuery();
            return resultSet.next();
        }
    }

    public int save(News news) throws Exception {
        String sql = "INSERT INTO news (title, description, source, url, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        	insertStatement.setString(1, news.getTitle());
        	insertStatement.setString(2, news.getContent());
        	insertStatement.setString(3, news.getSource());
        	insertStatement.setString(4, news.getUrl());
        	insertStatement.setTimestamp(5, news.getDate());
        	insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        throw new SQLException("Failed to insert news");
    }
    
    public boolean setHidden(int newsId, boolean hide) throws SQLException {
        String sql = "UPDATE news SET is_hidden = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setBoolean(1, hide);
        	updateStatement.setInt(2, newsId);
            return updateStatement.executeUpdate() > 0;
        }
    }
    
    public void autoHideIfReportedOverThreshold(int newsId) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM news_reports WHERE news_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(countSql)) {
        	selectStatement.setInt(1, newsId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) >= REPORT_THRESHOLD) {
                setHidden(newsId, true);
            }
        }
    }

    public List<String> getCategoriesForNews(int newsId) throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT c.name FROM categories c JOIN news_categories nc ON c.id = nc.category_id WHERE nc.news_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, newsId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        }
        return categories;
    }

    public List<News> getNewsSortedByLikes() throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                     "FROM news n " +
                     "LEFT JOIN news_reactions r ON n.id = r.news_id " +
                     "LEFT JOIN news_categories nc ON n.id = nc.news_id " +
                     "LEFT JOIN categories c ON nc.category_id = c.id " +
                     "WHERE (c.is_hidden IS NULL OR c.is_hidden = false) " +
                     "AND NOT EXISTS (SELECT 1 FROM blocked_keywords bk WHERE n.title LIKE CONCAT('%', bk.keyword, '%') OR n.description LIKE CONCAT('%', bk.keyword, '%')) " +
                     "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapNewsWithCategories(resultSet));
            }
        }
        return list;
    }

    public List<News> searchNewsByKeywords(String[] keywords) throws Exception {
        List<News> results = new ArrayList<>();

        StringBuilder keywordCondition = new StringBuilder();
        for (int i = 0; i < keywords.length; i++) {
            keywordCondition.append("(LOWER(n.title) LIKE ? OR LOWER(n.description) LIKE ?)");
            if (i < keywords.length - 1) {
                keywordCondition.append(" OR ");
            }
        }

        String sql = String.format("""
            SELECT n.*, (
                SELECT COUNT(*) FROM news_reactions r 
                WHERE r.news_id = n.id AND r.reaction = 'LIKE'
            ) AS like_count
            FROM news n
            LEFT JOIN news_categories nc ON n.id = nc.news_id
            LEFT JOIN categories c ON nc.category_id = c.id
            WHERE (%s)
            AND (c.is_hidden IS NULL OR c.is_hidden = false)
            AND NOT EXISTS (
                SELECT 1 FROM blocked_keywords bk 
                WHERE n.title LIKE CONCAT('%%', bk.keyword, '%%') 
                OR n.description LIKE CONCAT('%%', bk.keyword, '%%')
            )
            ORDER BY like_count DESC
        """, keywordCondition);

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
            int index = 1;
            for (String word : keywords) {
                String pattern = "%" + word.trim().toLowerCase() + "%";
                selectStatement.setString(index++, pattern);
                selectStatement.setString(index++, pattern);
            }

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(mapNewsWithCategories(resultSet));
                }
            }
        }

        return results;
    }

    public List<News> getNewsByDate(LocalDate date) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "LEFT JOIN news_categories nc ON n.id = nc.news_id " +
                "LEFT JOIN categories c ON nc.category_id = c.id " +
                "WHERE DATE(n.date) = ? " +
                "AND (c.is_hidden IS NULL OR c.is_hidden = false) " +
                "AND NOT EXISTS (SELECT 1 FROM blocked_keywords bk WHERE n.title LIKE CONCAT('%', bk.keyword, '%') OR n.description LIKE CONCAT('%', bk.keyword, '%')) " +
                "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                list.add(mapNewsWithCategories(resultSet));
            }
        }
        return list;
    }

    public List<News> getNewsByDateRange(LocalDate from, LocalDate to) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "LEFT JOIN news_categories nc ON n.id = nc.news_id " +
                "LEFT JOIN categories c ON nc.category_id = c.id " +
                "WHERE DATE(n.date) BETWEEN ? AND ? " +
                "AND (c.is_hidden IS NULL OR c.is_hidden = false) " +
                "AND NOT EXISTS (SELECT 1 FROM blocked_keywords bk WHERE n.title LIKE CONCAT('%', bk.keyword, '%') OR n.description LIKE CONCAT('%', bk.keyword, '%')) " +
                "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setDate(1, Date.valueOf(from));
        	selectStatement.setDate(2, Date.valueOf(to));
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                list.add(mapNewsWithCategories(resultSet));
            }
        }
        return list;
    }

    public List<News> getNewsByDateRangeAndCategory(LocalDate from, LocalDate to, int categoryId) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n " +
                "JOIN news_categories nc ON n.id = nc.news_id " +
                "LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "JOIN categories c ON nc.category_id = c.id " +
                "WHERE DATE(n.date) BETWEEN ? AND ? AND nc.category_id = ? " +
                "AND c.is_hidden = false " +
                "AND NOT EXISTS (SELECT 1 FROM blocked_keywords bk WHERE n.title LIKE CONCAT('%', bk.keyword, '%') OR n.description LIKE CONCAT('%', bk.keyword, '%')) " +
                "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setDate(1, Date.valueOf(from));
        	selectStatement.setDate(2, Date.valueOf(to));
        	selectStatement.setInt(3, categoryId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                list.add(mapNewsWithCategories(resultSet));
            }
        }
        return list;
    }

    public int getLatestNewsArticleId() {
        String query = "SELECT id FROM news ORDER BY id DESC LIMIT 1";

        try {
            PreparedStatement selectStatement = connection.prepareStatement(query);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getOrInsertCategoryId(String categoryType) {
        String selectQuery = "SELECT id FROM categories WHERE name = ?";
        String insertQuery = "INSERT INTO categories (name) VALUES (?)";

        try {
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            	selectStatement.setString(1, categoryType);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            	insertStatement.setString(1, categoryType);
                int affectedRows = insertStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean insertNewsCategoryMapping(int newsId, int categoryId) {
        String insertQuery = "INSERT INTO news_categories (news_id, category_id) VALUES (?, ?)";

        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
        	insertStatement.setInt(1, newsId);
        	insertStatement.setInt(2, categoryId);

        	insertStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Mapping already exists for news_id=" + newsId + " and category_id=" + categoryId);
                return true;
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }
    
    public List<News> getReportedNews() throws SQLException {
        List<News> reportedNewsList = new ArrayList<>();

        String sql = "SELECT n.id, n.title, n.is_hidden " +
                     "FROM news n " +
                     "JOIN news_reports r ON n.id = r.news_id";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet resultSet = selectStatement.executeQuery()) {

            while (resultSet.next()) {
                News news = new News(
                	resultSet.getInt("id"),
                	resultSet.getString("title"),
                	resultSet.getBoolean("is_hidden")
                );
                reportedNewsList.add(news);
            }
        }

        return reportedNewsList;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private News mapNewsWithCategories(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String url = rs.getString("url");
        String source = rs.getString("source");
        Timestamp date = rs.getTimestamp("date");
        News news = new News(id, title, description, url, source, date);
        news.setCategories(getCategoriesForNews(id));
        return news;
    }
}

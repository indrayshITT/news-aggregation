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
	private final Connection connection;

    public NewsDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public boolean exists(String url) throws Exception {
        String sql = "SELECT id FROM news WHERE url = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public int save(News news) throws Exception {
        String sql = "INSERT INTO news (title, description, source, url, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, news.getTitle());
            stmt.setString(2, news.getContent());
            stmt.setString(3, news.getSource());
            stmt.setString(4, news.getUrl());
            stmt.setTimestamp(5, news.getDate());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert news");
    }

    public List<String> getCategoriesForNews(int newsId) throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT c.name FROM categories c JOIN news_categories nc ON c.id = nc.category_id WHERE nc.news_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
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

    public List<News> getNewsSortedByLikes() throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                     "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                     "GROUP BY n.id ORDER BY like_count DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapNewsWithCategories(rs));
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
        
        String sql = """
            SELECT n.*, (
                SELECT COUNT(*) FROM news_reactions r 
                WHERE r.news_id = n.id AND r.reaction = 'LIKE'
            ) AS like_count
            FROM news n
            WHERE %s
            ORDER BY like_count DESC
            """.formatted(keywordCondition);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (String word : keywords) {
                String pattern = "%" + word.trim().toLowerCase() + "%";
                stmt.setString(index++, pattern);
                stmt.setString(index++, pattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapNewsWithCategories(rs));
                }
            }
        }

        return results;
    }

    public List<News> getNewsByDate(LocalDate date) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "WHERE DATE(n.date) = ? GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapNewsWithCategories(rs));
            }
        }
        return list;
    }

    public List<News> getNewsByDateRange(LocalDate from, LocalDate to) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "WHERE DATE(n.date) BETWEEN ? AND ? GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapNewsWithCategories(rs));
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
                "WHERE DATE(n.date) BETWEEN ? AND ? AND nc.category_id = ? " +
                "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setInt(3, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapNewsWithCategories(rs));
            }
        }
        return list;
    }

    public int getLatestNewsArticleId() {
        String query = "SELECT id FROM news ORDER BY id DESC LIMIT 1";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
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
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setString(1, categoryType);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, categoryType);
                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
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

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, newsId);
            stmt.setInt(2, categoryId);

            stmt.executeUpdate();
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
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

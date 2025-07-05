package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.UserCategory;

public class UserCategoryDAO {
	private final Connection connection;

    public UserCategoryDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }
    
    public Map<Integer, Map<String, List<String>>> getUserCategoryKeywordMap() throws SQLException {
        Map<Integer, Map<String, List<String>>> userMap = new HashMap<>();

        String sql = "SELECT uck.user_id, c.name AS category_name, uck.keyword " +
                     "FROM user_categories uck " +
                     "JOIN categories c ON uck.category_id = c.id ";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String category = rs.getString("category_name");
                String keyword = rs.getString("keyword");

                userMap.putIfAbsent(userId, new HashMap<>());
                Map<String, List<String>> categoryMap = userMap.get(userId);

                categoryMap.putIfAbsent(category, new ArrayList<>());
                categoryMap.get(category).add(keyword);
            }
        }
        return userMap;
    }


    public void enableCategoryWithKeywords(int userId, int categoryId, List<String> keywords) throws SQLException {
        String deleteSQL = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, categoryId);
            deleteStmt.executeUpdate();
        }

        String insertSQL = "INSERT INTO user_categories (user_id, category_id, keyword) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
            for (String keyword : keywords) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, categoryId);
                insertStmt.setString(3, keyword.trim());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    public void disableCategory(int userId, int categoryId) throws SQLException {
        String sql = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    public List<UserCategory> getAllByUserId(int userId) throws SQLException {
        List<UserCategory> list = new ArrayList<>();
        String sql = "SELECT c.id AS category_id, c.name, uck.user_id AS is_enabled " +
                     "FROM categories c LEFT JOIN (" +
                     "  SELECT DISTINCT category_id, user_id FROM user_categories WHERE user_id = ?" +
                     ") uck ON c.id = uck.category_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean enabled = rs.getObject("is_enabled") != null;
                list.add(new UserCategory(
                    userId,
                    rs.getInt("category_id"),
                    enabled
                ));
            }
        }
        return list;
    }

    public List<String> getKeywordsByUserAndCategory(int userId, int categoryId) throws SQLException {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM user_categories WHERE user_id = ? AND category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                keywords.add(rs.getString("keyword"));
            }
        }
        return keywords;
    }

    public void addKeyword(int userId, int categoryId, String keyword) throws SQLException {
        String sql = "INSERT INTO user_categories (user_id, category_id, keyword) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setString(3, keyword);
            stmt.executeUpdate();
        }
    }

    public void updateKeyword(int userId, int categoryId, String oldKeyword, String newKeyword) throws SQLException {
        String sql = "UPDATE user_categories SET keyword = ? WHERE user_id = ? AND category_id = ? AND keyword = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newKeyword);
            stmt.setInt(2, userId);
            stmt.setInt(3, categoryId);
            stmt.setString(4, oldKeyword);
            stmt.executeUpdate();
        }
    }

    public void deleteKeyword(int userId, int categoryId, String keyword) throws SQLException {
        String sql = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ? AND keyword = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setString(3, keyword);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

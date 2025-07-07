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

        try (PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet resultSet = selectStatement.executeQuery()) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String category = resultSet.getString("category_name");
                String keyword = resultSet.getString("keyword");

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
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)) {
        	deleteStatement.setInt(1, userId);
        	deleteStatement.setInt(2, categoryId);
        	deleteStatement.executeUpdate();
        }

        String insertSQL = "INSERT INTO user_categories (user_id, category_id, keyword) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
            for (String keyword : keywords) {
            	insertStatement.setInt(1, userId);
            	insertStatement.setInt(2, categoryId);
            	insertStatement.setString(3, keyword.trim());
            	insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    public void disableCategory(int userId, int categoryId) throws SQLException {
        String sql = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
        	deleteStatement.setInt(1, userId);
        	deleteStatement.setInt(2, categoryId);
        	deleteStatement.executeUpdate();
        }
    }

    public List<UserCategory> getAllByUserId(int userId) throws SQLException {
        List<UserCategory> list = new ArrayList<>();
        String sql = "SELECT c.id AS category_id, c.name, uck.user_id AS is_enabled " +
                     "FROM categories c LEFT JOIN (" +
                     "  SELECT DISTINCT category_id, user_id FROM user_categories WHERE user_id = ?" +
                     ") uck ON c.id = uck.category_id";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                boolean enabled = resultSet.getObject("is_enabled") != null;
                list.add(new UserCategory(
                    userId,
                    resultSet.getInt("category_id"),
                    enabled
                ));
            }
        }
        return list;
    }

    public List<String> getKeywordsByUserAndCategory(int userId, int categoryId) throws SQLException {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM user_categories WHERE user_id = ? AND category_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
        	selectStatement.setInt(2, categoryId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                keywords.add(resultSet.getString("keyword"));
            }
        }
        return keywords;
    }

    public void addKeyword(int userId, int categoryId, String keyword) throws SQLException {
        String sql = "INSERT INTO user_categories (user_id, category_id, keyword) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setInt(1, userId);
        	insertStatement.setInt(2, categoryId);
        	insertStatement.setString(3, keyword);
        	insertStatement.executeUpdate();
        }
    }

    public void updateKeyword(int userId, int categoryId, String oldKeyword, String newKeyword) throws SQLException {
        String sql = "UPDATE user_categories SET keyword = ? WHERE user_id = ? AND category_id = ? AND keyword = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setString(1, newKeyword);
        	updateStatement.setInt(2, userId);
        	updateStatement.setInt(3, categoryId);
        	updateStatement.setString(4, oldKeyword);
        	updateStatement.executeUpdate();
        }
    }

    public void deleteKeyword(int userId, int categoryId, String keyword) throws SQLException {
        String sql = "DELETE FROM user_categories WHERE user_id = ? AND category_id = ? AND keyword = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
        	deleteStatement.setInt(1, userId);
        	deleteStatement.setInt(2, categoryId);
        	deleteStatement.setString(3, keyword);
        	deleteStatement.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

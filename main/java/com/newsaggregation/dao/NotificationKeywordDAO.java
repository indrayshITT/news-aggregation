package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;

public class NotificationKeywordDAO {
	private final Connection connection;

    public NotificationKeywordDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public List<String> getKeywordsByUser(int userId) throws SQLException {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM notification_keywords WHERE user_id = ? AND enabled = true";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                keywords.add(resultSet.getString("keyword"));
            }
        }
        return keywords;
    }

    public void addKeyword(int userId, String keyword) throws SQLException {
        String sql = "INSERT INTO notification_keywords (user_id, keyword, enabled) VALUES (?, ?, true)";
        try (PreparedStatement insertstatement = connection.prepareStatement(sql)) {
        	insertstatement.setInt(1, userId);
        	insertstatement.setString(2, keyword);
        	insertstatement.executeUpdate();
        }
    }

    public void updateKeyword(int userId, String oldKeyword, String newKeyword) throws SQLException {
        String sql = "UPDATE notification_keywords SET keyword = ? WHERE user_id = ? AND keyword = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setString(1, newKeyword);
        	updateStatement.setInt(2, userId);
        	updateStatement.setString(3, oldKeyword);
        	updateStatement.executeUpdate();
        }
    }

    public void deleteKeyword(int userId, String keyword) throws SQLException {
        String sql = "DELETE FROM notification_keywords WHERE user_id = ? AND keyword = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
        	deleteStatement.setInt(1, userId);
        	deleteStatement.setString(2, keyword);
        	deleteStatement.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

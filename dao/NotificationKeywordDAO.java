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

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                keywords.add(rs.getString("keyword"));
            }
        }
        return keywords;
    }

    public void addKeyword(int userId, String keyword) throws SQLException {
        String sql = "INSERT INTO notification_keywords (user_id, keyword, enabled) VALUES (?, ?, true)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, keyword);
            stmt.executeUpdate();
        }
    }

    public void updateKeyword(int userId, String oldKeyword, String newKeyword) throws SQLException {
        String sql = "UPDATE notification_keywords SET keyword = ? WHERE user_id = ? AND keyword = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newKeyword);
            stmt.setInt(2, userId);
            stmt.setString(3, oldKeyword);
            stmt.executeUpdate();
        }
    }

    public void deleteKeyword(int userId, String keyword) throws SQLException {
        String sql = "DELETE FROM notification_keywords WHERE user_id = ? AND keyword = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, keyword);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

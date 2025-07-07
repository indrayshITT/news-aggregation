package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;

public class BlockedKeywordDAO {
	private final Connection connection;

    public BlockedKeywordDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public void addBlockedKeyword(String keyword) throws SQLException {
        String sql = "INSERT IGNORE INTO blocked_keywords (keyword) VALUES (?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setString(1, keyword);
        	insertStatement.executeUpdate();
        }
    }

    public void removeBlockedKeyword(String keyword) throws SQLException {
        String sql = "DELETE FROM blocked_keywords WHERE keyword = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
        	deleteStatement.setString(1, keyword);
        	deleteStatement.executeUpdate();
        }
    }

    public List<String> getAllBlockedKeywords() throws SQLException {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM blocked_keywords";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                keywords.add(resultSet.getString("keyword"));
            }
        }
        return keywords;
    }

    public boolean isKeywordBlocked(String text) throws SQLException {
        String sql = "SELECT 1 FROM blocked_keywords WHERE ? LIKE CONCAT('%', keyword, '%')";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setString(1, text);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

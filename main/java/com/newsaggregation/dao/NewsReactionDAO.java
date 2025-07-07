package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.newsaggregation.config.DatabaseConnection;

public class NewsReactionDAO {
	private final Connection connection;

    public NewsReactionDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }
	
	public void react(int userId, int newsId, String reaction) throws Exception {
        String sql = """
            INSERT INTO news_reactions (user_id, news_id, reaction)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE reaction = ?
        """;
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setInt(1, userId);
        	insertStatement.setInt(2, newsId);
        	insertStatement.setString(3, reaction);
        	insertStatement.setString(4, reaction);
        	insertStatement.executeUpdate();
        }
    }

    public Map<String, Integer> getReactionCounts(int newsId) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT reaction, COUNT(*) FROM news_reactions WHERE news_id = ? GROUP BY reaction";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, newsId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                map.put(resultSet.getString(1), resultSet.getInt(2));
            }
        }
        return map;
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

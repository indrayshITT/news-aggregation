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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            stmt.setString(3, reaction);
            stmt.setString(4, reaction);
            stmt.executeUpdate();
        }
    }

    public Map<String, Integer> getReactionCounts(int newsId) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT reaction, COUNT(*) FROM news_reactions WHERE news_id = ? GROUP BY reaction";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newsId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
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

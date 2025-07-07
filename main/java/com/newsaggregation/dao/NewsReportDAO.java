package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.newsaggregation.config.DatabaseConnection;

public class NewsReportDAO {
	private final Connection connection;

    public NewsReportDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public boolean saveReport(int userId, int newsId, String reason) throws SQLException {
        String sql = "INSERT INTO news_reports (user_id, news_id, reason) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setInt(1, userId);
        	insertStatement.setInt(2, newsId);
        	insertStatement.setString(3, reason);
            return insertStatement.executeUpdate() > 0;
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.SavedArticle;

public class SavedArticleDAO {
	private final Connection connection;

    public SavedArticleDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public List<SavedArticle> getAllByUserId(int userId) throws SQLException {
        List<SavedArticle> list = new ArrayList<>();
        String sql = "SELECT * FROM saved_articles WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new SavedArticle(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("news_id")
                ));
            }
        }
        return list;
    }

    public void save(int userId, int newsId) throws SQLException {
        String sql = "INSERT INTO saved_articles (user_id, news_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            stmt.executeUpdate();
        }
    }

    public void delete(int userId, int newsId) throws SQLException {
        String sql = "DELETE FROM saved_articles WHERE user_id = ? AND news_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, newsId);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

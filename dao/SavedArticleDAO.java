package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.News;
import com.newsaggregation.model.SavedArticle;

public class SavedArticleDAO {
	private final Connection connection;

    public SavedArticleDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public List<News> getAllByUserId(int userId) throws SQLException {
        List<News> list = new ArrayList<>();
        String sql = """
            SELECT n.id, n.title, n.description, n.source, n.url, n.date
            FROM saved_articles sa
            JOIN news n ON sa.news_id = n.id
            WHERE sa.user_id = ?
            ORDER BY n.date DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                News news = new News(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getString("source"),
                    rs.getTimestamp("date")
                );

                news.setCategories(getCategoriesByNewsId(news.getId()));
                list.add(news);
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
    
    private List<String> getCategoriesByNewsId(int newsId) throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = """
            SELECT c.name
            FROM categories c
            JOIN news_categories nc ON c.id = nc.category_id
            WHERE nc.news_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }
}

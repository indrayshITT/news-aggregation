package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.News;

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

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                News news = new News(
                	resultSet.getInt("id"),
                	resultSet.getString("title"),
                	resultSet.getString("description"),
                	resultSet.getString("url"),
                	resultSet.getString("source"),
                	resultSet.getTimestamp("date")
                );

                news.setCategories(getCategoriesByNewsId(news.getId()));
                list.add(news);
            }
        }
        return list;
    }

    public void save(int userId, int newsId) throws SQLException {
        String sql = "INSERT INTO saved_articles (user_id, news_id) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setInt(1, userId);
        	insertStatement.setInt(2, newsId);
        	insertStatement.executeUpdate();
        }
    }

    public void delete(int userId, int newsId) throws SQLException {
        String sql = "DELETE FROM saved_articles WHERE user_id = ? AND news_id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
        	deleteStatement.setInt(1, userId);
        	deleteStatement.setInt(2, newsId);
        	deleteStatement.executeUpdate();
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

        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, newsId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        }
        return categories;
    }
}

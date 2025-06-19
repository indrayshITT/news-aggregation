package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.News;

public class NewsDAO {
	private final Connection connection;

    public NewsDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }
    
    public boolean exists(String url) throws Exception {
        String sql = "SELECT id FROM news WHERE url = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public int save(News news) throws Exception {
        String sql = "INSERT INTO news (title, description, source, url, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, news.getTitle());
            stmt.setString(2, news.getContent());
            stmt.setString(3, news.getSource());
            stmt.setString(4, news.getUrl());
            stmt.setTimestamp(5, news.getDate());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to insert news");
    }

	public List<News> getNewsSortedByLikes() throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                     "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                     "GROUP BY n.id ORDER BY like_count DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String title = rs.getString("title");
            	String description = rs.getString("description");
            	String url = rs.getString("url");
            	String source = rs.getString("source");
            	Timestamp date = rs.getTimestamp("date");
                News news = new News(id, title, description, url, source, date);
                list.add(news);
            }
        }
        return list;
    }

    public List<News> searchNewsByKeywords(String[] keywords) throws Exception {
        List<News> results = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id WHERE ");

        for (int i = 0; i < keywords.length; i++) {
            query.append("(n.title LIKE ? OR n.content LIKE ?)");
            if (i < keywords.length - 1) query.append(" AND ");
        }

        query.append(" GROUP BY n.id ORDER BY like_count DESC");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            int index = 1;
            for (String word : keywords) {
                stmt.setString(index++, "%" + word + "%");
                stmt.setString(index++, "%" + word + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String title = rs.getString("title");
            	String description = rs.getString("description");
            	String url = rs.getString("url");
            	String source = rs.getString("source");
            	Timestamp date = rs.getTimestamp("date");
                News news = new News(id, title, description, url, source, date);
                results.add(news);
            }
        }
        return results;
    }

    public List<News> getNewsByDate(LocalDate date) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "WHERE DATE(n.date) = ? GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String title = rs.getString("title");
            	String description = rs.getString("description");
            	String url = rs.getString("url");
            	String source = rs.getString("source");
            	Timestamp savedDate = rs.getTimestamp("date");
                News news = new News(id, title, description, url, source, savedDate);
                list.add(news);
            }
        }
        return list;
    }

    public List<News> getNewsByDateRange(LocalDate from, LocalDate to) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "WHERE DATE(n.date) BETWEEN ? AND ? GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String title = rs.getString("title");
            	String description = rs.getString("description");
            	String url = rs.getString("url");
            	String source = rs.getString("source");
            	Timestamp date = rs.getTimestamp("date");
                News news = new News(id, title, description, url, source, date);
                list.add(news);
            }
        }
        return list;
    }
    
    public List<News> getNewsByDateRangeAndCategory(LocalDate from, LocalDate to, int categoryId) throws Exception {
        List<News> list = new ArrayList<>();
        String sql = "SELECT n.*, COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS like_count " +
                "FROM news n " +
                "JOIN news_category nc ON n.id = nc.news_id " +
                "LEFT JOIN news_reactions r ON n.id = r.news_id " +
                "WHERE DATE(n.date) BETWEEN ? AND ? AND nc.category_id = ? " +
                "GROUP BY n.id ORDER BY like_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setInt(3, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int id = rs.getInt("id");
            	String title = rs.getString("title");
            	String description = rs.getString("description");
            	String url = rs.getString("url");
            	String source = rs.getString("source");
            	Timestamp date = rs.getTimestamp("date");
                News news = new News(id, title, description, url, source, date);
                list.add(news);
            }
        }
        return list;
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

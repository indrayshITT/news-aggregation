package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.Category;

public class CategoryDAO {
	private final Connection connection;

    public CategoryDAO(DatabaseConnection dbConnection) throws Exception {
        this.connection = dbConnection.getConnection();
    }
    
    public String getNameById(int catId) throws SQLException {
    	String query = "SELECT name FROM categories WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, catId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("name");
        }
		return "";
    }
    
    public List<Category> getAll() throws Exception {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }
    public void saveNewsCategory(int newsId, String categoryName) throws Exception {
        int categoryId = getOrCreateCategoryId(categoryName);
        String sql = "INSERT IGNORE INTO news_categories (news_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    public int getOrCreateCategoryId(String name) throws Exception {
        String query = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }

        String insert = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Could not create or fetch category");
    }

    public List<Integer> getCategoryIdsForNews(int newsId) throws Exception {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT category_id FROM news_categories WHERE news_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newsId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("category_id"));
            }
        }
        return ids;
    }


    public void addCategory(String name) throws Exception {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

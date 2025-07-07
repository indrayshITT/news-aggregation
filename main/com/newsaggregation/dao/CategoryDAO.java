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
        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
        	selectStatement.setInt(1, catId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("name");
        }
		return "";
    }
    
    public List<Category> getAll() throws Exception {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                list.add(new Category(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getBoolean("is_hidden")));
            }
        }
        return list;
    }
    public void saveNewsCategory(int newsId, String categoryName) throws Exception {
        int categoryId = getOrCreateCategoryId(categoryName);
        String sql = "INSERT IGNORE INTO news_categories (news_id, category_id) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setInt(1, newsId);
        	insertStatement.setInt(2, categoryId);
        	insertStatement.executeUpdate();
        }
    }

    public int getOrCreateCategoryId(String name) throws Exception {
        String query = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
        	selectStatement.setString(1, name);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("id");
        }

        String insert = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
        	insertStatement.setString(1, name);
        	insertStatement.executeUpdate();
            ResultSet resultSet = insertStatement.getGeneratedKeys();
            if (resultSet.next()) return resultSet.getInt(1);
        }
        throw new SQLException("Could not create or fetch category");
    }

    public List<Integer> getCategoryIdsForNews(int newsId) throws Exception {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT category_id FROM news_categories WHERE news_id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setInt(1, newsId);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                ids.add(resultSet.getInt("category_id"));
            }
        }
        return ids;
    }


    public void addCategory(String name) throws Exception {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setString(1, name);
        	insertStatement.executeUpdate();
        }
    }
    
    public boolean toggleVisibility(int categoryId) throws SQLException {
        String query = "UPDATE categories SET is_hidden = NOT is_hidden WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
        	updateStatement.setInt(1, categoryId);
            return updateStatement.executeUpdate() > 0;
        }
    }

    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

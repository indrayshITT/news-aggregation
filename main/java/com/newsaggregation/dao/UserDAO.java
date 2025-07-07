package com.newsaggregation.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.model.User;

import com.newsaggregation.config.DatabaseConnection;

public class UserDAO {
	private final Connection connection;

    public UserDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public void register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, role_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
        	insertStatement.setString(1, user.getUsername());
        	insertStatement.setString(2, user.getEmail());
        	insertStatement.setString(3, user.getPassword());
        	insertStatement.setInt(4, user.getRoleId());
            insertStatement.executeUpdate();
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setString(1, username);
        	selectStatement.setString(2, password);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                	resultSet.getInt("id"),
                	resultSet.getString("username"),
                	resultSet.getString("email"),
                	resultSet.getString("password"),
                	resultSet.getInt("role_id"),
                	resultSet.getTimestamp("last_viewed_notifications_at")
                );
            } else {
                throw new SQLException("Invalid username or password");
            }
        }
    }

    public void updateLastViewedTime(int userId, Timestamp time) throws SQLException {
        String sql = "UPDATE users SET last_viewed_notifications_at = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setTimestamp(1, time);
            updateStatement.setInt(2, userId);
            updateStatement.executeUpdate();
        }
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, username, email, password, role_id, last_viewed_notifications_at FROM users WHERE role_id = 2";

        try (PreparedStatement selectStatement = connection.prepareStatement(sql);
             ResultSet resultSet = selectStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User(
                	resultSet.getInt("id"),
                	resultSet.getString("username"),
                	resultSet.getString("email"),
                	resultSet.getString("password"),
                	resultSet.getInt("role_id"),
                	resultSet.getTimestamp("last_viewed_notifications_at")
                );
                users.add(user);
            }
        }

        return users;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

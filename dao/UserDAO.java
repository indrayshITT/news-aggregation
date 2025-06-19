package com.newsaggregation.dao;

import java.sql.*;

import com.newsaggregation.model.User;

import com.newsaggregation.config.DatabaseConnection;

public class UserDAO {
	private final Connection connection;

    public UserDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public void register(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, role_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getRoleId());
            stmt.executeUpdate();
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("role_id"),
                    rs.getTimestamp("last_viewed_notifications_at")
                );
            } else {
                throw new SQLException("Invalid username or password");
            }
        }
    }

    public void updateLastViewedTime(int userId, Timestamp time) throws SQLException {
        String sql = "UPDATE users SET last_viewed_notification_time = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, time);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

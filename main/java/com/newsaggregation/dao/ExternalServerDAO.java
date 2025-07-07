package com.newsaggregation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.model.ExternalServer;

public class ExternalServerDAO {
	private final Connection connection;

    public ExternalServerDAO(DatabaseConnection dbConnection) throws SQLException, ClassNotFoundException {
        this.connection = dbConnection.getConnection();
    }

    public List<ExternalServer> getAll() throws SQLException {
        List<ExternalServer> list = new ArrayList<>();
        String sql = "SELECT id, name, api_key, active, api_url, last_accessed, secondary_url FROM external_servers";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                ExternalServer server = new ExternalServer(
                	resultSet.getInt("id"),
                	resultSet.getString("name"),
                	resultSet.getString("api_key"),
                	resultSet.getBoolean("active"),
                	resultSet.getString("api_url"),
                	resultSet.getTimestamp("last_accessed"),
                	resultSet.getString("secondary_url")
                );
                list.add(server);
            }
        }
        return list;
    }
    
    public List<ExternalServer> getAllActive() throws SQLException {
        List<ExternalServer> list = new ArrayList<>();
        String sql = "SELECT id, name, api_key, active, api_url, last_accessed, secondary_url FROM external_servers WHERE active = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(sql)) {
        	selectStatement.setBoolean(1, true);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                ExternalServer server = new ExternalServer(
                	resultSet.getInt("id"),
                	resultSet.getString("name"),
                	resultSet.getString("api_key"),
                	resultSet.getBoolean("active"),
                	resultSet.getString("api_url"),
                	resultSet.getTimestamp("last_accessed"),
                	resultSet.getString("secondary_url")
                );
                list.add(server);
            }
        }
        return list;
    }

    public void updateApiKey(int id, String apiKey) throws SQLException {
        String sql = "UPDATE external_servers SET api_key = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setString(1, apiKey);
        	updateStatement.setInt(2, id);
        	updateStatement.executeUpdate();
        }
    }
    
    public void updateLastAccessed(int serverId) throws SQLException {
        String sql = "UPDATE external_servers SET last_accessed = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
        	updateStatement.setInt(1, serverId);
        	updateStatement.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

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
        String sql = "SELECT id, name, api_key, active, api_url, last_accessed FROM external_servers";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExternalServer server = new ExternalServer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("api_key"),
                    rs.getBoolean("active"),
                    rs.getString("api_url"),
                    rs.getTimestamp("last_accessed")
                );
                list.add(server);
            }
        }
        return list;
    }
    
    public List<ExternalServer> getAllActive() throws SQLException {
        List<ExternalServer> list = new ArrayList<>();
        String sql = "SELECT id, name, api_key, active, api_url, last_accessed FROM external_servers WHERE active = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        	stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExternalServer server = new ExternalServer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    null,
                    rs.getBoolean("active"),
                    rs.getString("api_url"),
                    rs.getTimestamp("last_accessed")
                );
                list.add(server);
            }
        }
        return list;
    }

    public void updateApiKey(int id, String apiKey) throws SQLException {
        String sql = "UPDATE external_servers SET api_key = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, apiKey);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

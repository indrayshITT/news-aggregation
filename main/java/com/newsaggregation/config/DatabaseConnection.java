package com.newsaggregation.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {
	Connection getConnection() throws SQLException, ClassNotFoundException;
	void closeConnection(Connection connection) throws SQLException;
}
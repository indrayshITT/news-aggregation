package com.newsaggregation.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabaseConnection implements DatabaseConnection{
	private static final String DB_URL = "jdbc:mysql://localhost:3306/news_aggregation";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "Indr@ys#@875";

	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		return connection;
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}
}
package com.newsaggregation.service;

import com.newsaggregation.model.User;

import java.sql.Timestamp;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.UserDAO;

public class UserService {
	private final DatabaseConnection dbConnection;

    public UserService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
    
    public UserService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void register(User user) throws Exception {
        try {
            UserDAO userDAO = new UserDAO(dbConnection);
            userDAO.register(user);
            userDAO.close();
        } catch (Exception e) {
            throw new Exception("Registration failed: " + e.getMessage());
        }
    }

    public User login(String username, String password) throws Exception {
        try {
            UserDAO userDAO = new UserDAO(dbConnection);
            User user = userDAO.login(username, password);
            userDAO.close();
            return user;
        } catch (Exception e) {
            throw new Exception("Login failed: " + e.getMessage());
        }
    }

    public void updateLastViewedTime(int userId, Timestamp timestamp) throws Exception {
        try {
            UserDAO userDAO = new UserDAO(dbConnection);
            userDAO.updateLastViewedTime(userId, timestamp);
            userDAO.close();
        } catch (Exception e) {
            throw new Exception("Failed to update last viewed time: " + e.getMessage());
        }
    }
}

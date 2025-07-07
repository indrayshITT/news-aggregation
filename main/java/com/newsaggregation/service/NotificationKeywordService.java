package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NotificationKeywordDAO;

public class NotificationKeywordService {
	private final DatabaseConnection dbConnection;

    public NotificationKeywordService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
    
    public NotificationKeywordService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<String> getKeywordsByUser(int userId) throws Exception {
        NotificationKeywordDAO dao = new NotificationKeywordDAO(dbConnection);
        try {
            return dao.getKeywordsByUser(userId);
        } finally {
            dao.close();
        }
    }

    public void addKeyword(int userId, String keyword) throws Exception {
        NotificationKeywordDAO dao = new NotificationKeywordDAO(dbConnection);
        try {
            dao.addKeyword(userId, keyword);
        } finally {
            dao.close();
        }
    }

    public void updateKeyword(int userId, String oldKeyword, String newKeyword) throws Exception {
        NotificationKeywordDAO dao = new NotificationKeywordDAO(dbConnection);
        try {
            dao.updateKeyword(userId, oldKeyword, newKeyword);
        } finally {
            dao.close();
        }
    }

    public void deleteKeyword(int userId, String keyword) throws Exception {
        NotificationKeywordDAO dao = new NotificationKeywordDAO(dbConnection);
        try {
            dao.deleteKeyword(userId, keyword);
        } finally {
            dao.close();
        }
    }
}

package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.SavedArticleDAO;
import com.newsaggregation.model.News;

public class SavedArticleService {
	private final DatabaseConnection dbConnection;

    public SavedArticleService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
    
    public SavedArticleService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<News> getAllByUser(int userId) throws Exception {
        SavedArticleDAO dao = new SavedArticleDAO(dbConnection);
        try {
            return dao.getAllByUserId(userId);
        } finally {
            dao.close();
        }
    }

    public void save(int userId, int newsId) throws Exception {
        SavedArticleDAO dao = new SavedArticleDAO(dbConnection);
        try {
            dao.save(userId, newsId);
        } finally {
            dao.close();
        }
    }

    public void delete(int userId, int newsId) throws Exception {
        SavedArticleDAO dao = new SavedArticleDAO(dbConnection);
        try {
            dao.delete(userId, newsId);
        } finally {
            dao.close();
        }
    }
}

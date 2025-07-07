package com.newsaggregation.service;

import java.util.Map;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NewsReactionDAO;

public class NewsReactionService {
	private final DatabaseConnection dbConnection;

    public NewsReactionService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
    
    public NewsReactionService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void react(int userId, int newsId, String reaction) throws Exception {
        NewsReactionDAO dao = new NewsReactionDAO(dbConnection);
        try {
            dao.react(userId, newsId, reaction);
        } finally {
            dao.close();
        }
    }

    public Map<String, Integer> getReactionSummary(int newsId) throws Exception {
        NewsReactionDAO dao = new NewsReactionDAO(dbConnection);
        try {
            return dao.getReactionCounts(newsId);
        } finally {
            dao.close();
        }
    }
}

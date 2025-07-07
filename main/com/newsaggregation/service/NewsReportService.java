package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NewsReportDAO;

public class NewsReportService {
	private final DatabaseConnection dbConnection;

    public NewsReportService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
    
    public NewsReportService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public boolean report(int userId, int newsId, String reason) throws Exception {
    	NewsReportDAO reportDAO = new NewsReportDAO(dbConnection);
    	try {
    		return reportDAO.saveReport(userId, newsId, reason);
    	} finally {
    		reportDAO.close();
    	}
    }
}

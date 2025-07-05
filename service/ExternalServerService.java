package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.ExternalServerDAO;
import com.newsaggregation.dao.NewsDAO;
import com.newsaggregation.model.ExternalServer;
import com.newsaggregation.model.News;

public class ExternalServerService {
	private final DatabaseConnection dbConnection;

    public ExternalServerService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }

    public List<ExternalServer> getAll() throws Exception {
        ExternalServerDAO dao = new ExternalServerDAO(dbConnection);
        try {
            return dao.getAll();
        } finally {
            dao.close();
        }
    }

    public List<ExternalServer> getAllDetails() throws Exception {
        ExternalServerDAO dao = new ExternalServerDAO(dbConnection);
        try {
            return dao.getAll();
        } finally {
            dao.close();
        }
    }

    public void updateApiKey(int id, String apiKey) throws Exception {
        ExternalServerDAO dao = new ExternalServerDAO(dbConnection);
        try {
            dao.updateApiKey(id, apiKey);
        } finally {
            dao.close();
        }
    }
    
    public void saveDataFromApiToDB(List<News> apiData) throws Exception {
    	NewsDAO newsDAO = new NewsDAO(dbConnection);
    	for (News newsData: apiData) {
    		newsDAO.save(newsData);
    		int newsId = newsDAO.getLatestNewsArticleId();
    		for (News data: apiData) {
    			for (String categoryType: data.getCategories()) {
    				int categoryId = newsDAO.getOrInsertCategoryId(categoryType);
    				boolean mappingAdded = newsDAO.insertNewsCategoryMapping(newsId, categoryId);
    				if (!mappingAdded)
    					System.out.println("Failed in adding newsID: " + newsId + " with categoryID: " + categoryId);
    			}
    		}    		
    	}
    }
}

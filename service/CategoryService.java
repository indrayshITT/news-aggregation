package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.CategoryDAO;

public class CategoryService {
	private final DatabaseConnection dbConnection;

    public CategoryService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }

    public void addCategory(String name) throws Exception {
        CategoryDAO dao = new CategoryDAO(dbConnection);
        try {
            dao.addCategory(name);
        } finally {
            dao.close();
        }
    }
}

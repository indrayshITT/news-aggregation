package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.CategoryDAO;
import com.newsaggregation.model.Category;

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
    
    public List<Category> getAllCategories() throws Exception {
        CategoryDAO dao = new CategoryDAO(new MySQLDatabaseConnection());
        List<Category> list = dao.getAll();
        dao.close();
        return list;
    }
}

package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.ExternalServerDAO;
import com.newsaggregation.model.ExternalServer;

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

    public ExternalServer getById(int id) throws Exception {
        ExternalServerDAO dao = new ExternalServerDAO(dbConnection);
        try {
            return dao.getById(id);
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
}

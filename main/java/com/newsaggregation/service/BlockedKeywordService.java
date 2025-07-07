package com.newsaggregation.service;

import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.BlockedKeywordDAO;

public class BlockedKeywordService {

	private final DatabaseConnection dbConnection;
	
	public BlockedKeywordService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }
	
	public BlockedKeywordService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
	
	public void addBlockedKeyword(String keyword) throws Exception {
    	BlockedKeywordDAO dao = new BlockedKeywordDAO(dbConnection);
        try {
            dao.addBlockedKeyword(keyword);
        } finally {
        	dao.close();
        }
    }

    public void removeBlockedKeyword(String keyword) throws Exception {
    	BlockedKeywordDAO dao = new BlockedKeywordDAO(dbConnection);
        try {
            dao.removeBlockedKeyword(keyword);
        } finally {
        	dao.close();
        }
    }

    public List<String> getAllBlockedKeywords() throws Exception {
    	BlockedKeywordDAO dao = new BlockedKeywordDAO(dbConnection);
        try {
            return dao.getAllBlockedKeywords();
        } finally {
        	dao.close();
        }
    }
}

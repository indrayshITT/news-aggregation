package com.newsaggregation.service;

import java.time.LocalDate;
import java.util.List;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.NewsDAO;
import com.newsaggregation.model.News;

public class NewsService {
	private final DatabaseConnection dbConnection;

    public NewsService() {
        this.dbConnection = new MySQLDatabaseConnection();
    }

    public List<News> searchNewsByKeywordsSortedByLikes(String[] keywords) throws Exception {
        NewsDAO dao = new NewsDAO(dbConnection);
        try {
            return dao.searchNewsByKeywords(keywords);
        } finally {
            dao.close();
        }
    }

    public List<News> getNewsSavedTodaySortedByLikes() throws Exception {
        NewsDAO dao = new NewsDAO(dbConnection);
        try {
            return dao.getNewsByDate(LocalDate.now());
        } finally {
            dao.close();
        }
    }

    public List<News> getNewsByDateRangeSortedByLikes(LocalDate start, LocalDate end) throws Exception {
        NewsDAO dao = new NewsDAO(dbConnection);
        try {
            return dao.getNewsByDateRange(start, end);
        } finally {
            dao.close();
        }
    }

    public List<News> getNewsByDateRangeAndCategorySortedByLikes(LocalDate start, LocalDate end, int categoryId) throws Exception {
        NewsDAO dao = new NewsDAO(dbConnection);
        try {
            return dao.getNewsByDateRangeAndCategory(start, end, categoryId);
        } finally {
            dao.close();
        }
    }
}

package com.newsaggregation.scheduler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.newsaggregation.config.MySQLDatabaseConnection;
import com.newsaggregation.dao.CategoryDAO;
import com.newsaggregation.dao.ExternalServerDAO;
import com.newsaggregation.dao.NewsDAO;
import com.newsaggregation.ingestion.ExternalNewsApi;
import com.newsaggregation.ingestion.NewsApi;
import com.newsaggregation.ingestion.TheNewsApi;
import com.newsaggregation.model.ExternalServer;
import com.newsaggregation.model.News;
import com.newsaggregation.util.EmailNotificationSender;

public class NewsFetcherScheduler {
	private static ScheduledExecutorService scheduler;

    public static void start(long interval, TimeUnit unit) {
        if (scheduler != null && !scheduler.isShutdown()) return;

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[Scheduler] Fetching news at " + Timestamp.from(Instant.now()));
                fetchAndStoreNews();
            } catch (Exception e) {
                System.err.println("[Scheduler] Error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, interval, unit);
    }

    public static void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("[Scheduler] Scheduler stopped.");
        }
    }

    private static void fetchAndStoreNews() {
        ExternalServerDAO externalServerDAO = null;
        NewsDAO newsDAO = null;
        CategoryDAO categoryDAO = null;
        List<News> savedNews = new ArrayList<>();

        try {
            externalServerDAO = new ExternalServerDAO(new MySQLDatabaseConnection());
            newsDAO = new NewsDAO(new MySQLDatabaseConnection());
            categoryDAO = new CategoryDAO(new MySQLDatabaseConnection());

            for (ExternalServer server : externalServerDAO.getAllActive()) {
                ExternalNewsApi parser = resolveParser(server);
                if (parser == null) continue;

                for (News news : parser.parseExternalApiData()) {
                    if (news.getUrl() == null || news.getUrl().isBlank() || newsDAO.exists(news.getUrl())) continue;

                    int newsId = newsDAO.save(news);
                    news.setId(newsId);

                    for (String category : news.getCategories()) {
                        categoryDAO.saveNewsCategory(newsId, category);
                    }

                    savedNews.add(news);
                }

                externalServerDAO.updateLastAccessed(server.getId());
            }

            if (!savedNews.isEmpty()) {
                EmailNotificationSender.sendNewsEmails(savedNews);
            }

        } catch (Exception e) {
            System.err.println("[Error] Failed to fetch/store news: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (externalServerDAO != null) externalServerDAO.close();
                if (newsDAO != null) newsDAO.close();
                if (categoryDAO != null) categoryDAO.close();
            } catch (Exception e) {
                System.err.println("[Error] Failed to close DAOs: " + e.getMessage());
            }
        }
    }

    private static ExternalNewsApi resolveParser(ExternalServer server) {
        String name = server.getName().toLowerCase();
        if (name.contains("the news api")) {
            return new TheNewsApi(server.getUrl(), server.getApiKey());
        } else if (name.contains("news api")) {
            return new NewsApi(server.getUrl(), server.getSecondaryUrl(), server.getApiKey());
        } else {
            System.err.println("[Scheduler] Unknown server type: " + name);
            return null;
        }
    }
}

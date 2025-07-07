package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newsaggregation.dto.SavedArticleDTO;
import com.newsaggregation.service.SavedArticleService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class SavedArticleClient {
    private final SavedArticleService articleService;
    private final HeadlinesClient headlinesClient;
    private NewsReactionClient reactionClient;

    public SavedArticleClient(HeadlinesClient headlinesClient) {
        this.headlinesClient = headlinesClient;
        this.articleService = new SavedArticleService();
    }
    
    public SavedArticleClient(HeadlinesClient headlinesClient, NewsReactionClient reactionClient) {
        this.articleService = new SavedArticleService();
        this.headlinesClient = headlinesClient;
        this.reactionClient = reactionClient;
    }

    public void setReactionClient(NewsReactionClient reactionClient) {
        this.reactionClient = reactionClient;
    }

    public void show(Scanner sc) {
        boolean keepRunning = true;
        while (keepRunning) {
            System.out.println("\n-- Saved Articles --");
            System.out.println("1. View Saved Articles");
            System.out.println("2. Save Article by ID");
            System.out.println("3. Delete Saved Article");
            System.out.println("4. Back");

            int option = InputUtil.readInt(sc, "Choose option: ");
            switch (option) {
                case 1 -> view(sc);
                case 2 -> save(sc);
                case 3 -> delete(sc);
                case 4 -> keepRunning = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void save(Scanner sc) {
        int newsId = InputUtil.readInt(sc, "Enter the ID of the news to save: ");
        SavedArticleDTO dto = new SavedArticleDTO(UserSession.getUserId(), newsId);
        String message = articleService.saveArticle(dto);
        System.out.println(message);
    }

    private void view(Scanner sc) {
        try {
            JSONObject response = articleService.fetchSavedArticles(UserSession.getUserId());

            if (!"success".equals(response.optString("status"))) {
                System.out.println("Error: " + response.optString("message", "Unable to fetch articles."));
                return;
            }

            JSONArray articles = response.optJSONArray("articles");
            headlinesClient.displayNews(articles, "No saved articles.");

            if (articles != null && articles.length() > 0) {
                reactionClient.showArticleActions(sc);
            }

        } catch (Exception e) {
            System.out.println("An unexpected error occurred while fetching saved articles.");
        }
    }

    private void delete(Scanner sc) {
        int newsId = InputUtil.readInt(sc, "Enter the ID of the article to delete: ");
        SavedArticleDTO dto = new SavedArticleDTO(UserSession.getUserId(), newsId);
        String message = articleService.deleteArticle(dto);
        System.out.println(message);
    }
}

package com.newsaggregation.client;

import java.util.Scanner;

import com.newsaggregation.dto.NewsReactionDTO;
import com.newsaggregation.service.NewsReactionService;
import com.newsaggregation.util.InputUtil;

public class NewsReactionClient {
    private final NewsReactionService reactionService = new NewsReactionService();
    private final NewsReportClient newsReportClient = new NewsReportClient();
    private SavedArticleClient savedArticleClient;

    public void setSavedArticleClient(SavedArticleClient savedArticleClient) {
        this.savedArticleClient = savedArticleClient;
    }

    public void showArticleActions(Scanner sc) {
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("\n-- Article Actions --");
            System.out.println("1. Save Article");
            System.out.println("2. React (Like/Dislike)");
            System.out.println("3. Report");
            System.out.println("4. Back");
            System.out.println("5. Logout");

            int choice = InputUtil.readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> savedArticleClient.save(sc);
                case 2 -> reactToArticle(sc);
                case 3 -> report(sc);
                case 4 -> keepRunning = false;
                case 5 -> {
                    System.out.println("Logging out...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void reactToArticle(Scanner sc) {
        System.out.println("\n-- React to Article --");
        System.out.println("1. Like");
        System.out.println("2. Dislike");
        System.out.println("3. Cancel");

        int option = InputUtil.readInt(sc, "Choose an option: ");
        if (option == 3) return;

        String reaction = null;
        if (option == 1) reaction = "like";
        else if (option == 2) reaction = "dislike";

        if (reaction == null) {
            System.out.println("Invalid reaction option.");
            return;
        }

        int newsId = InputUtil.readInt(sc, "Enter the article ID to react to: ");
        NewsReactionDTO dto = new NewsReactionDTO(newsId, reaction);
        String message = reactionService.sendReaction(dto);
        System.out.println(message);
    }

    private void report(Scanner sc) {
        int newsId = InputUtil.readInt(sc, "Enter the article ID to report: ");
        newsReportClient.reportNews(sc, newsId);
    }
}

package com.newsaggregation.client;

import java.time.LocalDate;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newsaggregation.service.HeadlinesService;
import com.newsaggregation.util.InputUtil;

public class HeadlinesClient {
    private static final String NO_RESULTS = "No headlines found.";
    private final HeadlinesService headlinesService = new HeadlinesService();
    private NewsReactionClient reactionClient;

    public void show(Scanner scanner) {
        boolean keepShowing = true;

        while (keepShowing) {
            showMenu();
            int option = InputUtil.readInt(scanner, "Choose option: ");

            switch (option) {
                case 1 -> fetchToday(scanner);
                case 2 -> fetchByDateRangeAndCategory(scanner);
                case 3 -> keepShowing = false;
                case 4 -> {
                    System.out.println("Logging out...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n-- Headlines --");
        System.out.println("1. Today");
        System.out.println("2. Date range");
        System.out.println("3. Back");
        System.out.println("4. Logout");
    }

    private void fetchToday(Scanner scanner) {
        try {
            JSONArray newsList = headlinesService.getTodayHeadlines();
            displayNews(newsList, NO_RESULTS);

            if (newsList.length() > 0) {
                getReactionClient().showArticleActions(scanner);
            }
        } catch (Exception e) {
            System.out.println("Failed to load today's headlines. Please try again later.");
        }
    }

    private void fetchByDateRangeAndCategory(Scanner scanner) {
        try {
            LocalDate from = InputUtil.readDate(scanner, "Enter start date (YYYY-MM-DD): ");
            LocalDate to = InputUtil.readDate(scanner, "Enter end date (YYYY-MM-DD): ");

            JSONArray categories = headlinesService.getAllCategories();
            System.out.println("\nSelect a category:");
            System.out.println("0. All");
            for (int i = 0; i < categories.length(); i++) {
                JSONObject cat = categories.getJSONObject(i);
                System.out.printf("%d. %s\n", cat.getInt("id"), cat.getString("name"));
            }

            int selectedId = InputUtil.readInt(scanner, "Enter option: ");
            JSONArray newsList = headlinesService.getHeadlinesByDateRange(from, to, selectedId);
            displayNews(newsList, NO_RESULTS);

            if (newsList.length() > 0) {
                getReactionClient().showArticleActions(scanner);
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch headlines for the given date range. Please try again.");
        }
    }

    public void displayNews(JSONArray newsList, String emptyMessage) {
        try {
            if (newsList.length() == 0) {
                System.out.println(emptyMessage);
                return;
            }

            for (int i = 0; i < newsList.length(); i++) {
                JSONObject news = newsList.getJSONObject(i);
                System.out.printf("\n%d. %s\n", news.optInt("id"), news.optString("title", "No title"));
                System.out.println("     Description: " + news.optString("description"));
                System.out.println("     Source: " + news.optString("source", "Unknown"));
                System.out.println("     URL: " + news.optString("url", "N/A"));

                if (news.has("categories")) {
                    JSONArray categories = news.getJSONArray("categories");
                    if (categories.length() > 0) {
                        System.out.print("     Categories: ");
                        for (int j = 0; j < categories.length(); j++) {
                            System.out.print(categories.getString(j));
                            if (j < categories.length() - 1) System.out.print(", ");
                        }
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to display news. Please try again.");
        }
    }

    private NewsReactionClient getReactionClient() {
        if (reactionClient == null) {
            reactionClient = new NewsReactionClient();
            SavedArticleClient savedArticleClient = new SavedArticleClient(this);
            reactionClient.setSavedArticleClient(savedArticleClient);
        }
        return reactionClient;
    }
}

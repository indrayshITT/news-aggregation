package com.newsaggregation.client;

import java.time.LocalDate;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;

public class HeadlinesClient {
	public static void show(Scanner sc) throws JSONException {
        while (true) {
            System.out.println("\n-- Headlines --");
            System.out.println("1. Today");
            System.out.println("2. Date range");
            System.out.println("3. Back");
            System.out.println("4. Logout");
            int option = InputUtil.readInt(sc, "Choose option: ");

            switch (option) {
                case 1 -> fetchToday(sc);
                case 2 -> fetchByDateRangeAndCategory(sc);
                case 3 -> {
                    return;
                }
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void fetchToday(Scanner sc) {
        String response = APIService.send("/api/news/today", "GET", null);
        try {
            JSONArray newsList = new JSONArray(response);
            displayHeadlines(newsList);
            if(newsList.length() > 0) {
            	NewsReactionClient.showArticleActions(sc);
            }
        } catch (JSONException e) {
            System.out.println("Failed to parse response.");
        }
    }

    private static void fetchByDateRangeAndCategory(Scanner sc) throws JSONException {
        LocalDate from = InputUtil.readDate(sc, "Enter start date (YYYY-MM-DD): ");
        LocalDate to = InputUtil.readDate(sc, "Enter end date (YYYY-MM-DD): ");

        String categoryList = APIService.send("/api/categories", "GET", null);
        JSONArray categories = new JSONArray(categoryList);

        System.out.println("\nSelect a category:");
        System.out.println("0. All");
        for (int i = 0; i < categories.length(); i++) {
            JSONObject cat = categories.getJSONObject(i);
            System.out.printf("%d. %s\n", cat.getInt("id"), cat.getString("name"));
        }
        int selectedId = InputUtil.readInt(sc, "Enter option: ");

        String endpoint = selectedId == 0
                ? String.format("/api/news/date-range?start=%s&end=%s", from, to)
                : String.format("/api/news/date-range/category?start=%s&end=%s&categoryId=%d", from, to, selectedId);

        String response = APIService.send(endpoint, "GET", null);
        JSONArray newsList = new JSONArray(response);
        displayHeadlines(newsList);
        if(newsList.length() > 0) {
        	NewsReactionClient.showArticleActions(sc);
        }
    }

    private static void displayHeadlines(JSONArray headlines) throws JSONException {
        if (headlines.length() == 0) {
            System.out.println("No headlines found.");
            return;
        }

        for (int i = 0; i < headlines.length(); i++) {
            JSONObject news = headlines.getJSONObject(i);
            System.out.printf("\n%d. %s\n", i + 1, news.getString("title"));
            System.out.println("   Source: " + news.optString("source"));
            System.out.println("   URL: " + news.optString("url"));

            if (news.has("categories")) {
                JSONArray categories = news.getJSONArray("categories");
                if (categories.length() > 0) {
                    System.out.print("   Categories: ");
                    for (int j = 0; j < categories.length(); j++) {
                        System.out.print(categories.getString(j));
                        if (j < categories.length() - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        }
    }
}

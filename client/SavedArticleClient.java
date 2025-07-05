package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class SavedArticleClient {
	public static void show(Scanner sc) throws JSONException {
        while (true) {
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
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public static void save(Scanner sc) throws JSONException {
        int newsId = InputUtil.readInt(sc, "Enter the ID of the news to save: ");
        String endpoint = "/api/saved-articles?userId=" + UserSession.getUserId() + "&newsId=" + newsId;
        String response = APIService.send(endpoint, "POST", null);

        JSONObject json = new JSONObject(response);
        System.out.println(json.optString("message", "Unable to save article."));
    }

    private static void view(Scanner sc) throws JSONException {
        String endpoint = "/api/saved-articles?userId=" + UserSession.getUserId();
        String response = APIService.send(endpoint, "GET", null);
        JSONObject json = new JSONObject(response);

        if (!json.getString("status").equals("success")) {
            System.out.println("Error: " + json.optString("message", "Unable to fetch articles."));
            return;
        }

        JSONArray articles = json.getJSONArray("articles");
        if (articles.length() == 0) {
            System.out.println("No saved articles.");
            return;
        }

        for (int i = 0; i < articles.length(); i++) {
            JSONObject article = articles.getJSONObject(i);
            System.out.printf("\n%d. %s\n", i + 1, article.getString("title"));
            System.out.println("   Source: " + article.optString("source"));
            System.out.println("   URL: " + article.optString("url"));

            if (article.has("categories")) {
                JSONArray cats = article.getJSONArray("categories");
                if (cats.length() > 0) {
                    System.out.print("   Categories: ");
                    for (int j = 0; j < cats.length(); j++) {
                        System.out.print(cats.getString(j));
                        if (j < cats.length() - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        }

        NewsReactionClient.showArticleActions(sc);
    }

    private static void delete(Scanner sc) throws JSONException {
        int newsId = InputUtil.readInt(sc, "Enter the ID of the article to delete: ");
        String endpoint = "/api/saved-articles?userId=" + UserSession.getUserId() + "&newsId=" + newsId;
        String response = APIService.send(endpoint, "DELETE", null);

        JSONObject json = new JSONObject(response);
        System.out.println(json.optString("message", "Unable to delete article."));
    }
}

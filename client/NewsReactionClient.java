package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;

public class NewsReactionClient {
	public static void showArticleActions(Scanner sc) throws JSONException {
        while (true) {
            System.out.println("\n-- Article Actions --");
            System.out.println("1. Save Article");
            System.out.println("2. React (Like/Dislike)");
            System.out.println("3. Back");
            System.out.println("4. Logout");

            int choice = InputUtil.readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> SavedArticleClient.save(sc);
                case 2 -> reactToArticle(sc);
                case 3 -> { return; }
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

	public static void reactToArticle(Scanner sc) throws JSONException {
	    System.out.println("\n-- React to Article --");
	    System.out.println("1. Like");
	    System.out.println("2. Dislike");
	    System.out.println("3. Cancel");

	    int option = InputUtil.readInt(sc, "Choose an option: ");
	    if (option == 3) return;

	    String reaction = option == 1 ? "like" : option == 2 ? "dislike" : null;
	    if (reaction == null) {
	        System.out.println("Invalid reaction option.");
	        return;
	    }

	    int newsId = InputUtil.readInt(sc, "Enter the article ID to react to: ");

	    String endpoint = "/api/news/reaction";
	    JSONObject payload = new JSONObject();
	    payload.put("newsId", newsId);
	    payload.put("reaction", reaction);

	    String response = APIService.send(endpoint, "POST", payload.toString());
	    JSONObject json = new JSONObject(response);
	    System.out.println(json.optString("message", "Unable to record reaction."));
	}

}

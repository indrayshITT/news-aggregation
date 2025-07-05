package com.newsaggregation.client;

import java.time.LocalDate;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;

public class SearchClient {
	public static void search(Scanner sc) throws JSONException {
        System.out.println("\n-- Search News --");
        String keywords = InputUtil.readString(sc, "Enter keywords to search (comma-separated): ");
        String[] keywordArray = keywords.split(",");

        System.out.println("Do you want to filter by date range? (yes/no): ");
        String filter = sc.nextLine().trim().toLowerCase();

        String endpoint;
        if (filter.equalsIgnoreCase("yes")) {
            LocalDate from = InputUtil.readDate(sc, "Enter start date (YYYY-MM-DD): ");
            LocalDate to = InputUtil.readDate(sc, "Enter end date (YYYY-MM-DD): ");

            StringBuilder url = new StringBuilder("/api/news/search/filter?from=" + from + "&to=" + to);
            for (String word : keywordArray) {
                url.append("&keyword=").append(word.trim());
            }
            endpoint = url.toString();
        } else {
            StringBuilder url = new StringBuilder("/api/news/search?");
            for (String word : keywordArray) {
                url.append("keyword=").append(word.trim()).append("&");
            }
            endpoint = url.substring(0, url.length() - 1);
        }

        String response = APIService.send(endpoint, "GET", null);
        JSONArray results = new JSONArray(response);

        if (results.length() == 0) {
            System.out.println("No news found for the given keywords.");
            return;
        }

        for (int i = 0; i < results.length(); i++) {
            JSONObject news = results.getJSONObject(i);
            System.out.printf("\n%d. %s\n", i + 1, news.getString("title"));
            System.out.println("   Description: " + news.optString("description"));
            System.out.println("   Source: " + news.optString("source"));
            System.out.println("   URL: " + news.optString("url"));
        }
    }
}

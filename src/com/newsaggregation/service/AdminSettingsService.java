package com.newsaggregation.service;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.dto.BlockedKeywordDTO;
import com.newsaggregation.util.InputUtil;

public class AdminSettingsService {

	public void displayBlockedKeywords() {
        try {
            JSONArray keywords = fetchBlockedKeywords();
            if (keywords.length() == 0) {
                System.out.println("No blocked keywords found.");
                return;
            }

            System.out.println("\nBlocked Keywords:");
            for (int i = 0; i < keywords.length(); i++) {
                System.out.println((i + 1) + ". " + keywords.getString(i));
            }
        } catch (Exception e) {
            System.out.println("Unable to fetch blocked keywords. Please try again later.");
        }
    }

    public void handleBlockKeyword(Scanner scanner) {
        try {
            String keyword = InputUtil.readString(scanner, "Enter keyword to block: ");
            String response = sendBlockKeywordRequest(keyword);
            printResponseMessage(response);
        } catch (Exception e) {
            System.out.println("Failed to block the keyword. Please try again.");
        }
    }

    public void handleUnblockKeyword(Scanner scanner) {
        try {
            displayBlockedKeywords();
            String keyword = InputUtil.readString(scanner, "Enter keyword to unblock: ");
            String response = sendUnblockKeywordRequest(keyword);
            printResponseMessage(response);
        } catch (Exception e) {
            System.out.println("Failed to unblock the keyword. Please try again.");
        }
    }

    public void handleToggleNewsVisibility(Scanner scanner) {
        try {
            JSONArray reportedNews = fetchReportedNews();
            if (reportedNews.length() == 0) {
                System.out.println("No reported news found.");
                return;
            }

            displayReportedNews(reportedNews);
            int newsId = InputUtil.readInt(scanner, "Enter the ID of the news to toggle visibility: ");
            boolean hide = InputUtil.readBoolean(scanner, "Do you want to hide this news? (yes/no): ");

            String response = toggleNewsVisibility(newsId, hide);
            printResponseMessage(response);
        } catch (Exception e) {
            System.out.println("Failed to update news visibility. Please try again.");
        }
    }

    public void handleToggleCategoryVisibility(Scanner scanner) {
        try {
            JSONArray categories = fetchAllCategories();
            displayCategories(categories);

            int categoryId = InputUtil.readInt(scanner, "Enter category ID to toggle visibility: ");
            String response = toggleCategoryVisibility(categoryId);
            printResponseMessage(response);
        } catch (Exception e) {
            System.out.println("Failed to update category visibility. Please try again.");
        }
    }

    private JSONArray fetchBlockedKeywords() throws JSONException {
        String response = APIService.send("/api/blocked-keywords", "GET", null);
        JSONObject json = new JSONObject(response);
        return json.getJSONArray("keywords");
    }

    private String sendBlockKeywordRequest(String keyword) throws JSONException {
        BlockedKeywordDTO dto = new BlockedKeywordDTO(keyword);
        return APIService.send("/api/blocked-keywords", "POST", dto.toJson());
    }

    private String sendUnblockKeywordRequest(String keyword) throws JSONException {
        BlockedKeywordDTO dto = new BlockedKeywordDTO(keyword);
        return APIService.send("/api/blocked-keywords", "DELETE", dto.toJson());
    }

    private JSONArray fetchReportedNews() throws JSONException {
        String response = APIService.send("/api/news/reported-news", "GET", null);
        return new JSONArray(response);
    }

    private void displayReportedNews(JSONArray newsList) throws JSONException {
        System.out.println("\nReported News:");
        for (int i = 0; i < newsList.length(); i++) {
            JSONObject news = newsList.getJSONObject(i);
            int id = news.getInt("id");
            String title = news.getString("title");
            boolean isHidden = news.getBoolean("isHidden");

            System.out.printf("%d. (ID: %d) %s [%s]\n", i + 1, id, title, isHidden ? "Hidden" : "Visible");
        }
    }

    private String toggleNewsVisibility(int newsId, boolean hide) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("newsId", newsId);
        request.put("hide", hide);

        return APIService.send("/api/news/toggle-visibility", "PATCH", request.toString());
    }

    private JSONArray fetchAllCategories() throws JSONException {
        String response = APIService.send("/api/categories", "GET", null);
        return new JSONArray(response);
    }

    private void displayCategories(JSONArray categories) throws JSONException {
        System.out.println("\nAvailable Categories:");
        for (int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);
            int id = category.getInt("id");
            String name = category.getString("name");
            boolean isHidden = category.getBoolean("isHidden");

            System.out.printf("%d. %s - %s\n", id, name, isHidden ? "Hidden" : "Visible");
        }
    }

    private String toggleCategoryVisibility(int categoryId) {
        return APIService.send("/api/categories/" + categoryId + "/hide", "PATCH", null);
    }

    private void printResponseMessage(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        System.out.println(json.optString("message"));
    }
}

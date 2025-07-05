package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class UserCategoryClient {
	public static JSONArray getUserCategories() throws JSONException {
        String endpoint = "/api/category/user?user=" + UserSession.getUserId();
        String response = APIService.send(endpoint, "GET", null);
        return new JSONObject(response).getJSONArray("categories");
    }

	public static void handleCategoryChoice(Scanner sc, JSONObject selected, boolean isEnabled) throws JSONException {
        int categoryId = selected.getInt("id");
        String name = selected.getString("name");

        boolean toggle = InputUtil.readBoolean(sc, "Do you want to " + (isEnabled ? "disable" : "enable") + " this category? (yes/no): ");

        if (!toggle) {
            if (isEnabled) {
                boolean manage = InputUtil.readBoolean(sc, "Do you want to manage keywords for this category? (yes/no): ");
                if (manage) manageCategoryKeywords(sc, categoryId, name);
            }
            return;
        }

        JSONObject payload = new JSONObject();
        payload.put("action", "updateCategory");
        payload.put("userId", UserSession.getUserId());
        payload.put("categoryId", categoryId);
        payload.put("enabled", !isEnabled);

        if (!isEnabled) {
            String keywords = InputUtil.readString(sc, "Enter comma-separated keywords for category '" + name + "': ");
            payload.put("keywords", keywords);
        } else {
            payload.put("keywords", "");
        }

        String response = APIService.send("/api/category/user", "POST", payload.toString());
        JSONObject json = new JSONObject(response);
        System.out.println(json.optString("message"));
    }

	private static void manageCategoryKeywords(Scanner sc, int categoryId, String name) throws JSONException {
        while (true) {
            System.out.println("\nManage Keywords for Category: " + name);
            System.out.println("1. Add Keyword");
            System.out.println("2. Update Keyword");
            System.out.println("3. Delete Keyword");
            System.out.println("4. Back");

            int option = InputUtil.readInt(sc, "Choose an option: ");
            if (option == 4) return;

            JSONObject payload = new JSONObject();
            payload.put("userId", UserSession.getUserId());
            payload.put("categoryId", categoryId);

            switch (option) {
                case 1 -> {
                    String keyword = InputUtil.readString(sc, "Enter new keyword to add: ");
                    payload.put("action", "addKeyword");
                    payload.put("keyword", keyword);
                }
                case 2 -> {
                    JSONArray currentKeywords = fetchKeywords(categoryId);
                    if (currentKeywords.length() == 0) {
                        System.out.println("No keywords available to update.");
                        return;
                    }

                    System.out.println("Available Keywords:");
                    for (int i = 0; i < currentKeywords.length(); i++) {
                        System.out.printf("%d. %s\n", i + 1, currentKeywords.getString(i));
                    }
                    int keywordIndex = InputUtil.readInt(sc, "Select keyword number to update: ");
                    if (keywordIndex < 1 || keywordIndex > currentKeywords.length()) {
                        System.out.println("Invalid selection.");
                        return;
                    }

                    String oldKeyword = currentKeywords.getString(keywordIndex - 1);
                    String newKeyword = InputUtil.readString(sc, "Enter new keyword: ");
                    payload.put("action", "updateKeyword");
                    payload.put("oldKeyword", oldKeyword);
                    payload.put("newKeyword", newKeyword);
                }
                case 3 -> {
                    String keyword = InputUtil.readString(sc, "Enter keyword to delete: ");
                    payload.put("action", "deleteKeyword");
                    payload.put("keyword", keyword);
                }
                default -> {
                    System.out.println("Invalid option.");
                    return;
                }
            }

            String response = APIService.send("/api/category/user", "POST", payload.toString());
            JSONObject json = new JSONObject(response);
            System.out.println(json.optString("message"));
        }
    }
	
	private static JSONArray fetchKeywords(int categoryId) {
        try {
            String url = "/api/category/user?user=" + UserSession.getUserId() + "&categoryId=" + categoryId;
            String res = APIService.send(url, "GET", null);
            JSONObject json = new JSONObject(res);
            return json.getJSONArray("keywords");
        } catch (Exception e) {
            System.out.println("Failed to load keywords.");
            return new JSONArray();
        }
    }
}

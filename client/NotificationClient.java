package com.newsaggregation.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.service.APIService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class NotificationClient {
	
	private CategoryClient categoryClient = new CategoryClient();
	
	public void showMenu(Scanner sc) throws JSONException {
        while (true) {
            System.out.printf("\nWelcome to News Application, %s!\n", com.newsaggregation.util.UserSession.getUsername());
            System.out.println("Date: " + LocalDateTime.now().toLocalDate());
            System.out.println("Time:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mma")));
            System.out.println("N O T I F I C A T I O N S");

            System.out.println("1. View Notifications");
            System.out.println("2. Configure Notifications");
            System.out.println("3. Back");
            System.out.println("4. Logout");

            int choice = InputUtil.readInt(sc, "Choose option: ");

            switch (choice) {
                case 1 -> viewNotifications();
                case 2 -> configureNotifications(sc);
                case 3 -> { return; }
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }
	
	public void viewNotifications() throws JSONException {
        String response = APIService.send("/api/notifications", "GET", null);
        JSONObject json = new JSONObject(response);

        if (!json.getString("status").equals("success")) {
            System.out.println("Error: " + json.optString("message"));
            return;
        }

        JSONArray notifications = json.getJSONArray("notifications");
        if (notifications.length() == 0) {
            System.out.println("No new notifications.");
            return;
        }

        System.out.println("\n-- Notifications --");
        for (int i = 0; i < notifications.length(); i++) {
            JSONObject news = notifications.getJSONObject(i);
            System.out.printf("%d. %s\n", i + 1, news.getString("title"));
            System.out.println("   Source: " + news.optString("source"));
            System.out.println("   Published: " + news.optString("date"));
            System.out.println("   URL: " + news.optString("url"));
        }
    }
	
	public void configureNotifications(Scanner sc) throws JSONException {
		while (true) {
            printHeader();
            JSONArray categories = categoryClient.getAllCategories();
            JSONArray userCategoryPrefs = UserCategoryClient.getUserCategories();
            JSONArray userKeywords = fetchGlobalKeywords();

            int keywordOption = printCategoryOptions(categories, userCategoryPrefs, userKeywords);
            int backOption = keywordOption + 1;
            int logoutOption = keywordOption + 2;

            int choice = InputUtil.readInt(sc, "Choose an option: ");

            if (choice == backOption) return;
            if (choice == logoutOption) System.exit(0);
            if (choice == keywordOption) {
                manageKeywords(sc);
                continue;
            }

            if (choice > 0 && choice <= categories.length()) {
                JSONObject selectedCategory = categories.getJSONObject(choice - 1);
                boolean isEnabled = isCategoryEnabled(userCategoryPrefs, selectedCategory.getInt("id"));
                UserCategoryClient.processCategorySelection(sc, selectedCategory, isEnabled);
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
	
	private void printHeader() {
        System.out.printf("\nWelcome to the News Application, %s!\n", UserSession.getUsername());
        System.out.println("Date: " + LocalDateTime.now().toLocalDate());
        System.out.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mma")));
        System.out.println("C O N F I G U R E - N O T I F I C A T I O N S");
    }
	
	private int printCategoryOptions(JSONArray categories, JSONArray userPrefs, JSONArray globalKeywords) throws JSONException {
        int index = 1;
        for (int i = 0; i < categories.length(); i++) {
            JSONObject cat = categories.getJSONObject(i);
            int categoryId = cat.getInt("id");
            String name = cat.getString("name");

            boolean enabled = isCategoryEnabled(userPrefs, categoryId);
            System.out.printf("%d. %s - %s\n", index++, name, enabled ? "Enabled" : "Disabled");
        }
        System.out.printf("%d. Keywords - %s\n", index, globalKeywords.length() > 0 ? "Enabled" : "Disabled");
        System.out.printf("%d. Back\n", index + 1);
        System.out.printf("%d. Logout\n", index + 2);
        return index;
    }
	
	private boolean isCategoryEnabled(JSONArray userPrefs, int categoryId) throws JSONException {
        for (int j = 0; j < userPrefs.length(); j++) {
            JSONObject userPref = userPrefs.getJSONObject(j);
            if (userPref.getInt("categoryId") == categoryId) {
                return userPref.getBoolean("enabled");
            }
        }
        return false;
    }
	
	public void manageKeywords(Scanner sc) throws JSONException {
        while (true) {
            System.out.println("\n--- Keyword Notification Configuration ---");
            System.out.println("1. View Keywords");
            System.out.println("2. Add Keyword");
            System.out.println("3. Update Keyword");
            System.out.println("4. Delete Keyword");
            System.out.println("5. Back");

            int option = InputUtil.readInt(sc, "Choose an option: ");
            switch (option) {
                case 1 -> viewKeywords();
                case 2 -> addKeyword(sc);
                case 3 -> updateKeyword(sc);
                case 4 -> deleteKeyword(sc);
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewKeywords() throws JSONException {
    	JSONArray userKeywords = fetchGlobalKeywords();
        if (userKeywords.length() == 0) {
            System.out.println("No keywords configured.");
        } else {
            System.out.println("Keywords:");
            for (int i = 0; i < userKeywords.length(); i++) {
                System.out.println("- " + userKeywords.getString(i));
            }
        }
    }

    private void addKeyword(Scanner sc) throws JSONException {
        String keyword = InputUtil.readString(sc, "Enter keyword to add: ");
        JSONObject payload = new JSONObject();
        payload.put("userId", UserSession.getUserId());
        payload.put("keyword", keyword);
        payload.put("action", "add");
        String response = APIService.send("/api/keywords", "POST", payload.toString());
        System.out.println(new JSONObject(response).optString("message", "Failed to add keyword."));
    }

    private void updateKeyword(Scanner sc) throws JSONException {
        String oldKeyword = InputUtil.readString(sc, "Enter old keyword: ");
        String newKeyword = InputUtil.readString(sc, "Enter new keyword: ");
        JSONObject payload = new JSONObject();
        payload.put("userId", UserSession.getUserId());
        payload.put("oldKeyword", oldKeyword);
        payload.put("newKeyword", newKeyword);
        payload.put("action", "update");
        String response = APIService.send("/api/keywords", "POST", payload.toString());
        System.out.println(new JSONObject(response).optString("message", "Failed to update keyword."));
    }

    private void deleteKeyword(Scanner sc) throws JSONException {
        String keyword = InputUtil.readString(sc, "Enter keyword to delete: ");
        JSONObject payload = new JSONObject();
        payload.put("userId", UserSession.getUserId());
        payload.put("keyword", keyword);
        payload.put("action", "delete");
        String response = APIService.send("/api/keywords", "POST", payload.toString());
        System.out.println(new JSONObject(response).optString("message", "Failed to delete keyword."));
    }
    
    private JSONArray fetchGlobalKeywords() {
        try {
            String url = "/api/keywords?userId=" + UserSession.getUserId();
            String res = APIService.send(url, "GET", null);
            JSONObject json = new JSONObject(res);
            return json.getJSONArray("keywords");
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}

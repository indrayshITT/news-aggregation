package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.dto.UserCategoryDTO;
import com.newsaggregation.service.UserCategoryService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class UserCategoryClient {
	private static final UserCategoryService service = new UserCategoryService();

    public static JSONArray getUserCategories() {
        try {
            return service.getUserCategories();
        } catch (Exception e) {
            System.out.println("Failed to fetch user categories.");
            return new JSONArray();
        }
    }

    public static void processCategorySelection(Scanner sc, JSONObject selectedCategory, boolean isCurrentlyEnabled) {
        try {
            int categoryId = selectedCategory.getInt("id");
            String name = selectedCategory.getString("name");

            if (askToToggleCategory(sc, isCurrentlyEnabled)) {
                toggleCategory(sc, categoryId, name, isCurrentlyEnabled);
            } else if (isCurrentlyEnabled && askToManageKeywords(sc)) {
                manageKeywordsForCategory(sc, categoryId, name);
            }
        } catch (Exception e) {
            System.out.println("Error while processing the category selection.");
        }
    }

    private static boolean askToToggleCategory(Scanner sc, boolean isEnabled) {
        return InputUtil.readBoolean(sc, "Do you want to " + (isEnabled ? "disable" : "enable") + " this category? (yes/no): ");
    }

    private static boolean askToManageKeywords(Scanner sc) {
        return InputUtil.readBoolean(sc, "Do you want to manage keywords for this category? (yes/no): ");
    }

    private static void toggleCategory(Scanner sc, int categoryId, String name, boolean isCurrentlyEnabled) {
        try {
            String keywords = !isCurrentlyEnabled
                    ? InputUtil.readString(sc, "Enter comma-separated keywords for category '" + name + "': ")
                    : "";

            UserCategoryDTO dto = new UserCategoryDTO(
                    UserSession.getUserId(), categoryId, !isCurrentlyEnabled, keywords, "updateCategory", null, null
            );
            System.out.println(service.updateCategoryPreference(dto));
        } catch (Exception e) {
            System.out.println("Failed to toggle category preference.");
        }
    }

    private static void manageKeywordsForCategory(Scanner sc, int categoryId, String name) {
        while (true) {
            showKeywordManagementMenu(name);
            int option = InputUtil.readInt(sc, "Choose an option: ");
            if (option == 4) return;

            try {
                UserCategoryDTO dto = getKeywordDTOByAction(sc, option, categoryId);
                if (dto != null) {
                    System.out.println(service.updateCategoryPreference(dto));
                }
            } catch (Exception e) {
                System.out.println("An error occurred while processing keyword changes.");
            }
        }
    }

    private static void showKeywordManagementMenu(String name) {
        System.out.println("\nManage Keywords for Category: " + name);
        System.out.println("1. Add Keyword");
        System.out.println("2. Update Keyword");
        System.out.println("3. Delete Keyword");
        System.out.println("4. Back");
    }

    private static UserCategoryDTO getKeywordDTOByAction(Scanner sc, int option, int categoryId) throws JSONException {
        switch (option) {
            case 1:
                return createAddKeywordDTO(sc, categoryId);
            case 2:
                return createUpdateKeywordDTO(sc, categoryId);
            case 3:
                return createDeleteKeywordDTO(sc, categoryId);
            default:
                System.out.println("Invalid option.");
                return null;
        }
    }

    private static UserCategoryDTO createAddKeywordDTO(Scanner sc, int categoryId) {
        String keyword = InputUtil.readString(sc, "Enter new keyword to add: ");
        return new UserCategoryDTO(UserSession.getUserId(), categoryId, true, keyword, "addKeyword", null, null);
    }

    private static UserCategoryDTO createUpdateKeywordDTO(Scanner sc, int categoryId) throws JSONException {
        JSONArray keywords = service.getCategoryKeywords(categoryId);
        if (keywords.length() == 0) {
            System.out.println("No keywords available to update.");
            return null;
        }

        showKeywordList(keywords);
        int index = InputUtil.readInt(sc, "Select keyword number to update: ");
        if (index < 1 || index > keywords.length()) {
            System.out.println("Invalid selection.");
            return null;
        }

        String oldKeyword = keywords.getString(index - 1);
        String newKeyword = InputUtil.readString(sc, "Enter new keyword: ");
        return new UserCategoryDTO(UserSession.getUserId(), categoryId, true, "", "updateKeyword", oldKeyword, newKeyword);
    }

    private static UserCategoryDTO createDeleteKeywordDTO(Scanner sc, int categoryId) {
        String keyword = InputUtil.readString(sc, "Enter keyword to delete: ");
        return new UserCategoryDTO(UserSession.getUserId(), categoryId, true, keyword, "deleteKeyword", null, null);
    }

    private static void showKeywordList(JSONArray keywords) throws JSONException {
        System.out.println("Available Keywords:");
        for (int i = 0; i < keywords.length(); i++) {
            System.out.printf("%d. %s\n", i + 1, keywords.getString(i));
        }
    }
}

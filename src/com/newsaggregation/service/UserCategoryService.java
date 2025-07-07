package com.newsaggregation.service;

import com.newsaggregation.dto.UserCategoryDTO;
import com.newsaggregation.util.UserSession;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserCategoryService {

    public JSONArray getUserCategories() {
        try {
            String endpoint = "/api/category/user?user=" + UserSession.getUserId();
            String response = APIService.send(endpoint, "GET", null);
            return new JSONObject(response).getJSONArray("categories");
        } catch (Exception e) {
            System.out.println("Failed to load user categories.");
            return new JSONArray();
        }
    }

    public JSONArray getCategoryKeywords(int categoryId) {
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

    public String updateCategoryPreference(UserCategoryDTO dto) {
        try {
            String payload = dto.toJson();
            if (payload == null) return "Invalid request payload.";
            String response = APIService.send("/api/category/user", "POST", payload);
            return new JSONObject(response).optString("message", "Operation failed.");
        } catch (Exception e) {
            return "An error occurred while updating category preference.";
        }
    }
}

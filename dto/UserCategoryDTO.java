package com.newsaggregation.dto;

import org.json.JSONObject;

public class UserCategoryDTO {
    private final int userId;
    private final int categoryId;
    private final boolean enabled;
    private final String keywords;
    private final String action;
    private final String oldKeyword;
    private final String newKeyword;

    public UserCategoryDTO(int userId, int categoryId, boolean enabled, String keywords, String action, String oldKeyword, String newKeyword) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.enabled = enabled;
        this.keywords = keywords;
        this.action = action;
        this.oldKeyword = oldKeyword;
        this.newKeyword = newKeyword;
    }

    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("categoryId", categoryId);
            json.put("enabled", enabled);
            json.put("keywords", keywords);
            json.put("action", action);
            if (oldKeyword != null) json.put("oldKeyword", oldKeyword);
            if (newKeyword != null) json.put("newKeyword", newKeyword);
            return json.toString();
        } catch (Exception e) {
            return null;
        }
    }
}

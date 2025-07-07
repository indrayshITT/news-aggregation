package com.newsaggregation.dto;

import org.json.JSONObject;

public class NotificationKeywordDTO {
    private final int userId;
    private final String keyword;
    private final String oldKeyword;
    private final String action;

    public NotificationKeywordDTO(int userId, String keyword, String action) {
        this.userId = userId;
        this.keyword = keyword;
        this.oldKeyword = null;
        this.action = action;
    }

    public NotificationKeywordDTO(int userId, String oldKeyword, String newKeyword, String action) {
        this.userId = userId;
        this.oldKeyword = oldKeyword;
        this.keyword = newKeyword;
        this.action = action;
    }

    public String toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", userId);
            obj.put("action", action);
            if (action.equals("update")) {
                obj.put("oldKeyword", oldKeyword);
                obj.put("newKeyword", keyword);
            } else {
                obj.put("keyword", keyword);
            }
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Failed to prepare keyword configuration data.");
            return null;
        }
    }
}

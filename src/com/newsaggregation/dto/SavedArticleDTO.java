package com.newsaggregation.dto;

import org.json.JSONObject;

public class SavedArticleDTO {
    private final int userId;
    private final int newsId;

    public SavedArticleDTO(int userId, int newsId) {
        this.userId = userId;
        this.newsId = newsId;
    }

    public int getUserId() {
        return userId;
    }

    public int getNewsId() {
        return newsId;
    }

    public String toQueryParam() {
        return String.format("?userId=%d&newsId=%d", userId, newsId);
    }

    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("newsId", newsId);
            return json.toString();
        } catch (Exception e) {
            System.out.println("Failed to convert article data to JSON.");
            return null;
        }
    }
}

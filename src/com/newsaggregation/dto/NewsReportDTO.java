package com.newsaggregation.dto;

import org.json.JSONObject;

public class NewsReportDTO {
    private final int userId;
    private final int newsId;
    private final String reason;

    public NewsReportDTO(int userId, int newsId, String reason) {
        this.userId = userId;
        this.newsId = newsId;
        this.reason = reason;
    }

    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("userId", userId);
            json.put("newsId", newsId);
            json.put("reason", reason);
            return json.toString();
        } catch (Exception e) {
            System.out.println("Failed to prepare report data.");
            return null;
        }
    }
}

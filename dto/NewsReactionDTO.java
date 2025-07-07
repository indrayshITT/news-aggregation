package com.newsaggregation.dto;

import org.json.JSONObject;

public class NewsReactionDTO {
    private final int newsId;
    private final String reaction;

    public NewsReactionDTO(int newsId, String reaction) {
        this.newsId = newsId;
        this.reaction = reaction;
    }

    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("newsId", newsId);
            json.put("reaction", reaction);
            return json.toString();
        } catch (Exception e) {
            System.out.println("Failed to prepare reaction request.");
            return null;
        }
    }

    public int getNewsId() {
        return newsId;
    }

    public String getReaction() {
        return reaction;
    }
}

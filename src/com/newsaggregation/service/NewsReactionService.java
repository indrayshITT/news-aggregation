package com.newsaggregation.service;

import com.newsaggregation.dto.NewsReactionDTO;

import org.json.JSONObject;

public class NewsReactionService {

    public String sendReaction(NewsReactionDTO reaction) {
        String payload = reaction.toJson();
        if (payload == null) return "Invalid reaction data.";

        try {
            String response = APIService.send("/api/news/reaction", "POST", payload);
            JSONObject json = new JSONObject(response);
            return json.optString("message", "No message from server.");
        } catch (Exception e) {
            return "Failed to send reaction. Please try again.";
        }
    }
}

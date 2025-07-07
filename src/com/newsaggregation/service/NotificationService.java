// NotificationService.java
package com.newsaggregation.service;

import com.newsaggregation.dto.NotificationKeywordDTO;
import com.newsaggregation.util.UserSession;
import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationService {

    public JSONArray getNotifications() {
        try {
            String response = APIService.send("/api/notifications", "GET", null);
            JSONObject json = new JSONObject(response);
            if ("success".equals(json.optString("status"))) {
                return json.getJSONArray("notifications");
            } else {
                System.out.println("Failed to fetch notifications: " + json.optString("message"));
                return new JSONArray();
            }
        } catch (Exception e) {
            System.out.println("Error while fetching notifications.");
            return new JSONArray();
        }
    }

    public JSONArray getUserKeywords() {
        try {
            String url = "/api/keywords?userId=" + UserSession.getUserId();
            String response = APIService.send(url, "GET", null);
            JSONObject json = new JSONObject(response);
            return json.getJSONArray("keywords");
        } catch (Exception e) {
            System.out.println("Failed to fetch keywords.");
            return new JSONArray();
        }
    }

    public String configureKeyword(NotificationKeywordDTO keywordDTO) {
        try {
            String payload = keywordDTO.toJson();
            if (payload == null) return "Invalid keyword data.";

            String response = APIService.send("/api/keywords", "POST", payload);
            JSONObject json = new JSONObject(response);
            return json.optString("message", "Keyword operation failed.");
        } catch (Exception e) {
            return "An error occurred while processing your keyword configuration.";
        }
    }
}

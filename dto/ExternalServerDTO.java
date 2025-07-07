package com.newsaggregation.dto;

import org.json.JSONObject;

public class ExternalServerDTO {

	private final int id;
    private final String apiKey;

    public ExternalServerDTO(int id, String apiKey) {
        this.id = id;
        this.apiKey = apiKey;
    }

    public String toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("apiKey", apiKey);
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Failed to prepare server update data.");
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public String getApiKey() {
        return apiKey;
    }
}

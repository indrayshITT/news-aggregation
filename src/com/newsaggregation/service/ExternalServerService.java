package com.newsaggregation.service;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newsaggregation.dto.ExternalServerDTO;

public class ExternalServerService {

	public JSONArray fetchAllServers() {
        try {
            String response = APIService.send("/api/admin/external-servers/list", "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Unable to fetch external servers.");
            return new JSONArray();
        }
    }

    public JSONArray fetchServerDetails() {
        try {
            String response = APIService.send("/api/admin/external-servers/details/", "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Unable to fetch server details.");
            return new JSONArray();
        }
    }

    public String updateServer(ExternalServerDTO server) {
        String payload = server.toJson();
        if (payload == null) return "Invalid server update payload.";

        try {
            String response = APIService.send("/api/admin/external-servers", "POST", payload);
            JSONObject json = new JSONObject(response);
            return json.optString("message", "No response message received.");
        } catch (Exception e) {
            return "Failed to update server. Please try again.";
        }
    }
}

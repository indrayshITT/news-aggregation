package com.newsaggregation.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.newsaggregation.dto.CategoryDTO;

public class CategoryService {
	
	public String addCategory(CategoryDTO category) {
        String payload = category.toJson();
        if (payload == null) {
            return "Failed to prepare category data.";
        }

        try {
            String response = APIService.send("/api/categories", "POST", payload);
            JSONObject json = new JSONObject(new JSONTokener(response));
            return json.optString("message", "No response message received.");
        } catch (Exception e) {
            return "Unable to add category. Please try again later.";
        }
    }

    public JSONArray fetchAllCategories() {
        try {
            String response = APIService.send("/api/categories", "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Failed to fetch categories. Please try again.");
            return new JSONArray();
        }
    }
}

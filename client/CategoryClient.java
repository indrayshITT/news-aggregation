package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;

public class CategoryClient {
	public static void addCategory(Scanner sc) throws JSONException {
        System.out.println("\n-- Add New News Category --");
        String name = InputUtil.readString(sc, "Enter category name: ");

        if (name == null || name.isBlank()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        JSONObject payload = new JSONObject();
        payload.put("name", name);

        String response = APIService.send("/api/admin/category", "POST", payload.toString());

        try {
            JSONObject jsonResponse = new JSONObject(new JSONTokener(response));
            String message = jsonResponse.optString("message");
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Error parsing response: " + response);
        }
    }
	
	public static JSONArray getAllCategories() throws JSONException {
        String response = APIService.send("/api/categories", "GET", null);
        return new JSONArray(response);
    }
}

package com.newsaggregation.service;

import java.time.LocalDate;

import org.json.JSONArray;

public class HeadlinesService {

	public JSONArray getTodayHeadlines() {
        try {
            String response = APIService.send("/api/news/today", "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Unable to fetch today's headlines.");
            return new JSONArray();
        }
    }

    public JSONArray getHeadlinesByDateRange(LocalDate start, LocalDate end, int categoryId) {
        try {
            String endpoint = (categoryId == 0)
                    ? String.format("/api/news/date-range?start=%s&end=%s", start, end)
                    : String.format("/api/news/date-range/category?start=%s&end=%s&categoryId=%d", start, end, categoryId);

            String response = APIService.send(endpoint, "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Unable to fetch news for given range.");
            return new JSONArray();
        }
    }

    public JSONArray getAllCategories() {
        try {
            String response = APIService.send("/api/categories", "GET", null);
            return new JSONArray(response);
        } catch (Exception e) {
            System.out.println("Unable to load categories.");
            return new JSONArray();
        }
    }
}

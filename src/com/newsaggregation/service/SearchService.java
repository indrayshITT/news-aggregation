package com.newsaggregation.service;

import com.newsaggregation.dto.SearchDTO;

import org.json.JSONArray;

public class SearchService {

    public JSONArray searchNews(SearchDTO dto) {
        try {
            StringBuilder url = new StringBuilder();

            if (dto.isDateFilterApplied()) {
                url.append("/api/news/search/filter?from=")
                   .append(dto.getFromDate())
                   .append("&to=")
                   .append(dto.getToDate());
            } else {
                url.append("/api/news/search?");
            }

            for (String keyword : dto.getKeywords()) {
                url.append(dto.isDateFilterApplied() ? "&" : "")
                   .append("keyword=").append(keyword.trim()).append("&");
            }

            String finalUrl = url.toString();
            if (finalUrl.endsWith("&")) {
                finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
            }

            String response = APIService.send(finalUrl, "GET", null);
            return new JSONArray(response);

        } catch (Exception e) {
            System.out.println("Failed to perform news search.");
            return new JSONArray();
        }
    }
}

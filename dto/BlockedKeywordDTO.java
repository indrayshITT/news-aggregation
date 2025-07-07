package com.newsaggregation.dto;

import org.json.JSONObject;

public class BlockedKeywordDTO {
	private final String keyword;

    public BlockedKeywordDTO(String keyword) {
        this.keyword = keyword;
    }

    public String toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("keyword", keyword);
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong while preparing the keyword data.");
            return null;
        }
    }

    public String getKeyword() {
        return keyword;
    }
}

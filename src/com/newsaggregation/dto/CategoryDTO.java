package com.newsaggregation.dto;

import org.json.JSONObject;

public class CategoryDTO {
	private final String name;

    public CategoryDTO(String name) {
        this.name = name;
    }

    public String toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            return obj.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong while preparing category data.");
            return null;
        }
    }

    public String getName() {
        return name;
    }
}

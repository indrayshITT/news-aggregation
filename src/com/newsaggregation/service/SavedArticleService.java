package com.newsaggregation.service;

import com.newsaggregation.dto.SavedArticleDTO;

import org.json.JSONException;
import org.json.JSONObject;

public class SavedArticleService {

	public String saveArticle(SavedArticleDTO dto) {
        try {
            String url = "/api/saved-articles" + dto.toQueryParam();
            String response = APIService.send(url, "POST", null);
            JSONObject json = new JSONObject(response);

            return json.optString("message", "Unable to save article.");
        } catch (Exception e) {
            return "An error occurred while saving the article. Please try again.";
        }
    }

    public JSONObject fetchSavedArticles(int userId) throws JSONException {
        JSONObject result = new JSONObject();
        try {
            String url = "/api/saved-articles?userId=" + userId;
            String response = APIService.send(url, "GET", null);
            return new JSONObject(response);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "Unable to fetch saved articles. Please try again.");
            return result;
        }
    }

    public String deleteArticle(SavedArticleDTO dto) {
        try {
            String url = "/api/saved-articles" + dto.toQueryParam();
            String response = APIService.send(url, "DELETE", null);
            JSONObject json = new JSONObject(response);

            return json.optString("message", "Unable to delete article.");
        } catch (Exception e) {
            return "An error occurred while deleting the article. Please try again.";
        }
    }
}

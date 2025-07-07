package com.newsaggregation.ingestion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsaggregation.model.News;

public class TheNewsApi implements ExternalNewsApi {
	private final String apiUrl;
    private final String apiKey;

    public TheNewsApi(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<News> parseExternalApiData() {
        List<News> newsList = new ArrayList<>();
        try {
            JsonNode root = fetchJson(apiUrl);
            JsonNode dataArray = root.path("data");

            for (JsonNode item : dataArray) {
                News news = new News();
                news.setTitle(item.path("title").asText(null));
                news.setContent(item.path("description").asText(null));
                news.setUrl(item.path("url").asText(null));
                news.setSource(item.path("source").asText(null));
                news.setDate(Timestamp.valueOf(LocalDateTime.now()));

                List<String> categories = new ArrayList<>();
                JsonNode categoryArray = item.path("categories");
                if (categoryArray.isArray()) {
                    for (JsonNode cat : categoryArray) {
                        String catText = cat.asText();
                        if (catText != null && !catText.isBlank()) categories.add(catText);
                    }
                }
                if (categories.isEmpty()) categories.add("general");

                news.setCategories(categories);
                newsList.add(news);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }

    private JsonNode fetchJson(String baseUrl) throws Exception {
        String fullUrl = baseUrl.contains("?") ? baseUrl + "&api_token=" + apiKey : baseUrl + "?api_token=" + apiKey;
        URL url = URI.create(fullUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
        }

        return new ObjectMapper().readTree(sb.toString());
    }

}

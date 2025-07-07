package com.newsaggregation.ingestion;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newsaggregation.model.News;

public class NewsApi implements ExternalNewsApi {
	private final String headlinesUrl;
    private final String sourcesUrl;
    private final String apiKey;

    public NewsApi(String headlinesUrl, String sourcesUrl, String apiKey) {
        this.headlinesUrl = headlinesUrl;
        this.sourcesUrl = sourcesUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<News> parseExternalApiData() {
        List<News> result = new ArrayList<>();

        try {
            JsonNode sourcesRoot = fetchJson(sourcesUrl);
            JsonNode articlesRoot = fetchJson(headlinesUrl);

            Map<String, List<String>> sourceCategoryMap = new HashMap<>();
            JsonNode sourcesArray = sourcesRoot.path("sources");

            for (JsonNode source : sourcesArray) {
                String id = source.path("id").asText(null);
                String category = source.path("category").asText(null);

                if (id != null && category != null) {
                    sourceCategoryMap.put(id, Collections.singletonList(category));
                }
            }

            JsonNode articles = articlesRoot.path("articles");
            for (JsonNode articleNode : articles) {
                News article = new News();
                article.setTitle(articleNode.path("title").asText(null));
                article.setContent(articleNode.path("description").asText(null));
                article.setUrl(articleNode.path("url").asText(null));
                article.setSource(articleNode.path("source").path("name").asText(null));
                article.setDate(Timestamp.valueOf(LocalDateTime.now()));

                String sourceId = articleNode.path("source").path("id").asText(null);
                List<String> categories = sourceCategoryMap.getOrDefault(sourceId, List.of("general"));
                article.setCategories(categories);

                result.add(article);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private JsonNode fetchJson(String baseUrl) throws Exception {
        String fullUrl = baseUrl.contains("?") ? baseUrl + "&apiKey=" + apiKey : baseUrl + "?apiKey=" + apiKey;
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

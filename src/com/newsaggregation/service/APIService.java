package com.newsaggregation.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class APIService {
	private static final String BASE_URL = "http://localhost:8080/NA-Server";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String sessionCookie;

    public static String send(String endpoint, String method, String jsonBody) {
        try {
            if (jsonBody == null || jsonBody.isEmpty()) {
                jsonBody = "{}";
            }

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

            if (sessionCookie != null) {
                builder.header("Cookie", sessionCookie);
            }

            switch (method.toUpperCase()) {
                case "POST", "PUT", "DELETE", "PATCH" -> builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(jsonBody));
                case "GET" -> builder.GET();
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            List<String> setCookie = response.headers().allValues("Set-Cookie");
            for (String cookie : setCookie) {
                if (cookie.startsWith("JSESSIONID")) {
                    sessionCookie = cookie.split(";", 2)[0];
                }
            }

            return response.body();

        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
            return "{}";
        }
    }
}

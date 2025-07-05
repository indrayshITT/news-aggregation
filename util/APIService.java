package com.newsaggregation.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class APIService {
	private static final String BASE_URL = "http://localhost:8080/NA-Server";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    // Store session cookie (e.g., JSESSIONID)
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

            // 👇 Add cookie if sessionCookie is present
            if (sessionCookie != null) {
                builder.header("Cookie", sessionCookie);
            }

            // Set HTTP method
            switch (method.toUpperCase()) {
                case "POST", "PUT", "DELETE" -> builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(jsonBody));
                case "GET" -> builder.GET();
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // 👇 Capture session cookie from login response
            List<String> setCookie = response.headers().allValues("Set-Cookie");
            for (String cookie : setCookie) {
                if (cookie.startsWith("JSESSIONID")) {
                    sessionCookie = cookie.split(";", 2)[0]; // e.g., JSESSIONID=xyz...
                }
            }

            return response.body();

        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
            return "{}";
        }
    }
}

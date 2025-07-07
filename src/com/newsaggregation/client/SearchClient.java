package com.newsaggregation.client;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;

import com.newsaggregation.dto.SearchDTO;
import com.newsaggregation.service.SearchService;
import com.newsaggregation.util.InputUtil;

public class SearchClient {
	private final SearchService searchService = new SearchService();
    private final HeadlinesClient headlinesClient = new HeadlinesClient();
    private final NewsReactionClient reactionClient = new NewsReactionClient();

    public void search(Scanner sc) {
        try {
            System.out.println("\n-- Search News --");
            String input = InputUtil.readString(sc, "Enter keywords to search (comma-separated): ");
            List<String> keywords = Arrays.asList(input.split(","));

            boolean applyDateFilter = InputUtil.readBoolean(sc, "Do you want to filter by date range? (yes/no): ");

            SearchDTO dto = applyDateFilter
                    ? new SearchDTO(keywords, InputUtil.readDate(sc, "Enter start date (YYYY-MM-DD): "), InputUtil.readDate(sc, "Enter end date (YYYY-MM-DD): "))
                    : new SearchDTO(keywords);

            JSONArray results = searchService.searchNews(dto);
            headlinesClient.displayNews(results, "No news found for the given keywords.");

            if (results.length() > 0) {
                reactionClient.showArticleActions(sc);
            }

        } catch (Exception e) {
            System.out.println("Something went wrong while searching. Please try again.");
        }
    }
}

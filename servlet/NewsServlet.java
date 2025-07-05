package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.News;
import com.newsaggregation.service.NewsService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/news/*")
public class NewsServlet extends HttpServlet {
	private final NewsService newsService = new NewsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();
        resp.setContentType("application/json");

        try {
            List<News> results = null;

            if (path == null || "/search".equals(path)) {
                String[] keywordArray = req.getParameterValues("keyword");
                results = newsService.searchNewsByKeywordsSortedByLikes(keywordArray);

            } else if ("/today".equals(path)) {
                results = newsService.getNewsSavedTodaySortedByLikes();

            } else if ("/date-range".equals(path)) {
                LocalDate start = LocalDate.parse(req.getParameter("start"));
                LocalDate end = LocalDate.parse(req.getParameter("end"));
                results = newsService.getNewsByDateRangeSortedByLikes(start, end);

            } else if ("/date-range/category".equals(path)) {
                LocalDate start = LocalDate.parse(req.getParameter("start"));
                LocalDate end = LocalDate.parse(req.getParameter("end"));
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                results = newsService.getNewsByDateRangeAndCategorySortedByLikes(start, end, categoryId);

            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Invalid news endpoint.");
                out.println(error);
                return;
            }

            JsonArray jsonResults = new JsonArray();
            for (News news : results) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", news.getId());
                obj.addProperty("title", news.getTitle());
                obj.addProperty("description", news.getContent());
                obj.addProperty("url", news.getUrl());
                obj.addProperty("source", news.getSource());
                obj.addProperty("date", news.getDate().toString());

                JsonArray categoryArray = new JsonArray();
                for (String category : news.getCategories()) {
                    categoryArray.add(category);
                }
                obj.add("categories", categoryArray);

                jsonResults.add(obj);
            }
            out.println(jsonResults);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", e.getMessage());
            out.println(error);
        }
    }
}

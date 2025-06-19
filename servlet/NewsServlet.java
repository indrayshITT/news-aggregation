package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import com.newsaggregation.model.News;
import com.newsaggregation.service.NewsService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/news/*")
public class NewsServlet extends HttpServlet {
	private final NewsService newsService = new NewsService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();
        resp.setContentType("text/plain");

        try {
            if (path == null || "/search".equals(path)) {
                String[] keywordArray = req.getParameterValues("keyword");
                List<News> results = newsService.searchNewsByKeywordsSortedByLikes(keywordArray);
                displayNews(results, out);

            } else if ("/today".equals(path)) {
                List<News> today = newsService.getNewsSavedTodaySortedByLikes();
                displayNews(today, out);

            } else if ("/date-range".equals(path)) {
                LocalDate start = LocalDate.parse(req.getParameter("start"));
                LocalDate end = LocalDate.parse(req.getParameter("end"));
                List<News> dateFiltered = newsService.getNewsByDateRangeSortedByLikes(start, end);
                displayNews(dateFiltered, out);

            } else if ("/date-range/category".equals(path)) {
                LocalDate start = LocalDate.parse(req.getParameter("start"));
                LocalDate end = LocalDate.parse(req.getParameter("end"));
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                List<News> filtered = newsService.getNewsByDateRangeAndCategorySortedByLikes(start, end, categoryId);
                displayNews(filtered, out);

            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Invalid news endpoint.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error: " + e.getMessage());
        }
    }

    private void displayNews(List<News> list, PrintWriter out) {
        for (News news : list) {
            out.println("- " + news.getTitle() + "\n" + news.getUrl() + "\n");
        }
    }
}

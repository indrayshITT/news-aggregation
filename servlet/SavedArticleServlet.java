package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.newsaggregation.model.SavedArticle;
import com.newsaggregation.service.SavedArticleService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/saved-articles")
public class SavedArticleServlet extends HttpServlet {
	private final SavedArticleService savedArticleService = new SavedArticleService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            List<SavedArticle> list = savedArticleService.getAllByUser(userId);
            for (SavedArticle article : list) {
                out.println("Saved News ID: " + article.getNewsId());
            }
        } catch (Exception e) {
            out.println("Error fetching saved articles: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            savedArticleService.save(userId, newsId);
            out.println("Article saved successfully.");
        } catch (Exception e) {
            out.println("Error saving article: " + e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            savedArticleService.delete(userId, newsId);
            out.println("Article deleted successfully.");
        } catch (Exception e) {
            out.println("Error deleting article: " + e.getMessage());
        }
    }
}

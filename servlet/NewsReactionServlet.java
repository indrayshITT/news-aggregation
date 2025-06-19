package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import com.newsaggregation.model.User;
import com.newsaggregation.service.NewsReactionService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/news/reaction")
public class NewsReactionServlet extends HttpServlet {
	private final NewsReactionService reactionService = new NewsReactionService();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("User must be logged in.");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        try {
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            String reaction = req.getParameter("reaction");

            if (!reaction.equalsIgnoreCase("like") && !reaction.equalsIgnoreCase("dislike")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Invalid reaction. Use 'like' or 'dislike'.");
                return;
            }

            reactionService.react(userId, newsId, reaction);
            out.println("Reaction recorded.");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        try {
            int newsId = Integer.parseInt(req.getParameter("newsId"));
            Map<String, Integer> summary = reactionService.getReactionSummary(newsId);
            out.println("Likes: " + summary.getOrDefault("like", 0));
            out.println("Dislikes: " + summary.getOrDefault("dislike", 0));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error: " + e.getMessage());
        }
    }
}

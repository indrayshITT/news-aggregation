package com.newsaggregation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.newsaggregation.model.ExternalServer;
import com.newsaggregation.service.ExternalServerService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExternalServerServlet extends HttpServlet {
	private final ExternalServerService service = new ExternalServerService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();

        try {
            if (path == null || "/list".equals(path)) {
                List<ExternalServer> servers = service.getAll();
                for (ExternalServer s : servers) {
                    out.println(s.getId() + ". " + s.getName() + " - " + s.isActive() + " - last accessed: " + s.getLastAccessed());
                }
            } else if (path.startsWith("/details/")) {
                int id = Integer.parseInt(path.substring("/details/".length()));
                ExternalServer server = service.getById(id);
                if (server != null) {
                    out.println(server.getId() + ". " + server.getName() + " - API Key: " + server.getApiKey());
                } else {
                    out.println("External server not found.");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("Invalid external server route.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String apiKey = req.getParameter("apiKey");
            service.updateApiKey(id, apiKey);
            out.println("API Key updated successfully for server ID: " + id);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error updating API key: " + e.getMessage());
        }
    }
}

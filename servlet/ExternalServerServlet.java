package com.newsaggregation.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.ExternalServer;
import com.newsaggregation.service.ExternalServerService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/admin/external-servers/*")
public class ExternalServerServlet extends HttpServlet {
	private final ExternalServerService service = new ExternalServerService();
    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String path = req.getPathInfo();

        try {
            if (path == null || "/list".equals(path)) {
                List<ExternalServer> servers = service.getAll();
                JsonArray arr = new JsonArray();
                for (ExternalServer s : servers) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", s.getId());
                    obj.addProperty("name", s.getName());
                    obj.addProperty("active", s.isActive());
                    obj.addProperty("lastAccessed", s.getLastAccessed() != null ? s.getLastAccessed().toString() : "N/A");
                    arr.add(obj);
                }
                out.println(gson.toJson(arr));

            } else if (path.startsWith("/details/")) {
                List<ExternalServer> servers = service.getAll();
                JsonArray arr = new JsonArray();
                for (ExternalServer s : servers) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", s.getId());
                    obj.addProperty("name", s.getName());
                    obj.addProperty("api_key", s.getApiKey());
                    arr.add(obj);
                }
                out.println(gson.toJson(arr));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject err = new JsonObject();
                err.addProperty("status", "error");
                err.addProperty("message", "Invalid external server route.");
                out.println(err.toString());
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject err = new JsonObject();
            err.addProperty("status", "error");
            err.addProperty("message", e.getMessage());
            out.println(err.toString());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();

        try (BufferedReader reader = req.getReader()) {
            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);
            int id = requestJson.get("id").getAsInt();
            String apiKey = requestJson.get("apiKey").getAsString();

            service.updateApiKey(id, apiKey);

            JsonObject success = new JsonObject();
            success.addProperty("status", "success");
            success.addProperty("message", "API Key updated successfully for server ID: " + id);
            out.println(success.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Error updating API key: " + e.getMessage());
            out.println(error.toString());
        }
    }
}

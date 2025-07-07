package com.newsaggregation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.Category;
import com.newsaggregation.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CategoryHandler {
    private final CategoryService categoryService = new CategoryService();
    private final Gson gson = new Gson();

    public void handleGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            JsonArray jsonArray = new JsonArray();
            for (Category category : categoryService.getAllCategories()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", category.getId());
                obj.addProperty("name", category.getName());
                obj.addProperty("isHidden", category.getIsHidden());
                jsonArray.add(obj);
            }
            out.println(jsonArray.toString());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch categories: " + e.getMessage());
        }
    }

    public void handlePost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (BufferedReader reader = req.getReader(); PrintWriter out = resp.getWriter()) {
            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);
            String name = requestJson.has("name") ? requestJson.get("name").getAsString() : null;

            if (name == null || name.isBlank()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category name is required.");
                return;
            }

            categoryService.addCategory(name);

            JsonObject success = new JsonObject();
            success.addProperty("status", "success");
            success.addProperty("message", "Category '" + name + "' added successfully.");
            out.println(success.toString());

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding category: " + e.getMessage());
        }
    }

    public void handlePatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            String path = req.getPathInfo();

            if (path == null || !path.matches("/\\d+/hide")) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid path.");
                return;
            }

            int categoryId = Integer.parseInt(path.split("/")[1]);
            boolean updated = categoryService.toggleCategoryVisibility(categoryId);

            JsonObject json = new JsonObject();
            if (updated) {
                json.addProperty("status", "success");
                json.addProperty("message", "Category visibility updated.");
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to update visibility.");
            }
            out.print(json);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating visibility: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", message);
        resp.getWriter().println(error.toString());
    }
}

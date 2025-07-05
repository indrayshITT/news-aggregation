package com.newsaggregation.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.service.CategoryService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {
	private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");

        try {
            var categories = categoryService.getAllCategories();

            JsonArray jsonArray = new JsonArray();
            for (var category : categories) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", category.getId());
                obj.addProperty("name", category.getName());
                jsonArray.add(obj);
            }

            out.println(jsonArray.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Failed to fetch categories: " + e.getMessage());
            out.println(error.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();

        try (BufferedReader reader = req.getReader()) {
            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);
            String name = requestJson.has("name") ? requestJson.get("name").getAsString() : null;

            if (name == null || name.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "Category name is required.");
                out.println(error.toString());
                return;
            }

            categoryService.addCategory(name);

            JsonObject success = new JsonObject();
            success.addProperty("status", "success");
            success.addProperty("message", "Category '" + name + "' added successfully.");
            out.println(success.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Error adding category: " + e.getMessage());
            out.println(error.toString());
        }
    }
}

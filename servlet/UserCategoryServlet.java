package com.newsaggregation.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsaggregation.model.UserCategory;
import com.newsaggregation.service.UserCategoryService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UserCategoryServlet", urlPatterns = {"/api/category/user"})
public class UserCategoryServlet extends HttpServlet {
	private final UserCategoryService userCategoryService = new UserCategoryService();
    private final Gson gson = new Gson();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            BufferedReader reader = req.getReader();
            JsonObject requestBody = gson.fromJson(reader, JsonObject.class);

            String action = requestBody.get("action").getAsString();
            int userId = requestBody.get("userId").getAsInt();
            int categoryId = requestBody.get("categoryId").getAsInt();

            if ("updateCategory".equalsIgnoreCase(action)) {
                boolean enabled = requestBody.get("enabled").getAsBoolean();
                List<String> keywords = requestBody.has("keywords") && !requestBody.get("keywords").getAsString().isBlank()
                        ? Arrays.asList(requestBody.get("keywords").getAsString().split(","))
                        : List.of();
                userCategoryService.setCategoryWithKeywords(userId, categoryId, enabled, keywords);
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Category preference and keywords updated.");

            } else if ("addKeyword".equalsIgnoreCase(action)) {
                String keyword = requestBody.get("keyword").getAsString();
                userCategoryService.addKeyword(userId, categoryId, keyword);
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Keyword added.");

            } else if ("updateKeyword".equalsIgnoreCase(action)) {
                String oldKeyword = requestBody.get("oldKeyword").getAsString();
                String newKeyword = requestBody.get("newKeyword").getAsString();
                userCategoryService.updateKeyword(userId, categoryId, oldKeyword, newKeyword);
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Keyword updated.");

            } else if ("deleteKeyword".equalsIgnoreCase(action)) {
                String keyword = requestBody.get("keyword").getAsString();
                userCategoryService.deleteKeyword(userId, categoryId, keyword);
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Keyword deleted.");

            } else {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Invalid action parameter.");
            }

        } catch (Exception e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
        }

        out.println(jsonResponse);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JsonObject responseJson = new JsonObject();

        try {
            int userId = Integer.parseInt(req.getParameter("user"));
            if (req.getParameter("categoryId") != null) {
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                List<String> keywords = userCategoryService.getKeywords(userId, categoryId);
                JsonArray keywordArray = new JsonArray();
                for (String keyword : keywords) {
                    keywordArray.add(keyword);
                }
                responseJson.addProperty("status", "success");
                responseJson.add("keywords", keywordArray);
            } else {
                List<UserCategory> categories = userCategoryService.getUserCategories(userId);
                JsonArray categoryArray = new JsonArray();
                for (UserCategory uc : categories) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("categoryId", uc.getCategoryId());
                    obj.addProperty("enabled", uc.isEnabled());
                    categoryArray.add(obj);
                }
                responseJson.addProperty("status", "success");
                responseJson.add("categories", categoryArray);
            }
        } catch (Exception e) {
            responseJson.addProperty("status", "error");
            responseJson.addProperty("message", "Error: " + e.getMessage());
        }

        out.println(responseJson);
    }
}

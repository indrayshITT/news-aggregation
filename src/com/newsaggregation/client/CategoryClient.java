package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;

import com.newsaggregation.dto.CategoryDTO;
import com.newsaggregation.service.CategoryService;
import com.newsaggregation.util.InputUtil;

public class CategoryClient {
	private final CategoryService categoryService;

    public CategoryClient() {
        this.categoryService = new CategoryService();
    }

    public void addCategory(Scanner scanner) {
        System.out.println("\n-- Add New News Category --");

        String name = InputUtil.readString(scanner, "Enter category name: ");
        if (name == null || name.isBlank()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        CategoryDTO category = new CategoryDTO(name);
        String message = categoryService.addCategory(category);
        System.out.println(message);
    }

    public JSONArray getAllCategories() {
        return categoryService.fetchAllCategories();
    }
}

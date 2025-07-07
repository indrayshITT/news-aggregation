package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newsaggregation.dto.ExternalServerDTO;
import com.newsaggregation.service.ExternalServerService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class ExternalServerClient {

    private final ExternalServerService serverService;
    private final CategoryClient categoryClient;
    private final AdminSettingsClient adminSettingsClient;

    public ExternalServerClient() {
        this.serverService = new ExternalServerService();
        this.categoryClient = new CategoryClient();
        this.adminSettingsClient = new AdminSettingsClient();
    }

    public void showMenu(Scanner scanner) {
        boolean show = true;

        while (show) {
            displayMenu();
            int choice = InputUtil.readInt(scanner, "Choose an option: ");

            switch (choice) {
                case 1 -> viewServerList();
                case 2 -> viewServerDetails();
                case 3 -> updateServer(scanner);
                case 4 -> categoryClient.addCategory(scanner);
                case 5 -> adminSettingsClient.showAdminOptions(scanner);
                case 6 -> {
                    UserSession.logout();
                    System.out.println("You have been logged out.");
                    show = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n-- External Server Management --");
        System.out.println("1. View list of external servers and status");
        System.out.println("2. View external server details");
        System.out.println("3. Update external server API key");
        System.out.println("4. Add new category");
        System.out.println("5. Admin Settings (Block Keywords / Hide News / Hide Categories)");
        System.out.println("6. Logout");
    }

    private void viewServerList() {
        try {
            JSONArray servers = serverService.fetchAllServers();
            if (servers.length() == 0) {
                System.out.println("No external servers found.");
                return;
            }

            System.out.println("\nExternal Servers:");
            for (int i = 0; i < servers.length(); i++) {
                JSONObject server = servers.getJSONObject(i);
                System.out.printf("%d. %s - %s - Last Accessed: %s\n",
                        server.getInt("id"),
                        server.getString("name"),
                        server.getBoolean("active") ? "Active" : "Inactive",
                        server.optString("lastAccessed", "Never"));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving server list. Please try again.");
        }
    }

    private void viewServerDetails() {
        try {
            JSONArray servers = serverService.fetchServerDetails();
            if (servers.length() == 0) {
                System.out.println("No server details found.");
                return;
            }

            System.out.println("\nExternal Server Details:");
            for (int i = 0; i < servers.length(); i++) {
                JSONObject server = servers.getJSONObject(i);
                System.out.printf("%d. %s - API Key: %s\n",
                        server.getInt("id"),
                        server.getString("name"),
                        server.getString("api_key"));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving server details. Please try again.");
        }
    }

    private void updateServer(Scanner scanner) {
        try {
            int id = InputUtil.readInt(scanner, "Enter the external server ID: ");
            String newKey = InputUtil.readString(scanner, "Enter the updated API key: ");

            if (newKey == null || newKey.isBlank()) {
                System.out.println("API key cannot be empty.");
                return;
            }

            ExternalServerDTO dto = new ExternalServerDTO(id, newKey);
            String message = serverService.updateServer(dto);
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Failed to update server. Please check the input and try again.");
        }
    }
}

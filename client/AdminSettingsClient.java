package com.newsaggregation.client;

import java.util.Scanner;

import com.newsaggregation.service.AdminSettingsService;
import com.newsaggregation.util.InputUtil;

public class AdminSettingsClient {
	
	public void showAdminOptions(Scanner scanner) {
        AdminSettingsService service = new AdminSettingsService();
        boolean continueMenu = true;

        while (continueMenu) {
            displayMenu();
            int choice = InputUtil.readInt(scanner, "Choose an option: ");
            continueMenu = handleUserChoice(choice, scanner, service);
        }
    }

    private void displayMenu() {
        System.out.println("\n-- Admin Settings --");
        System.out.println("1. View blocked keywords");
        System.out.println("2. Block new keyword");
        System.out.println("3. Unblock keyword");
        System.out.println("4. Hide/Unhide news by ID");
        System.out.println("5. Hide/Unhide category");
        System.out.println("6. Back");
    }

    private boolean handleUserChoice(int choice, Scanner scanner, AdminSettingsService service) {
        try {
            switch (choice) {
                case 1 -> service.displayBlockedKeywords();
                case 2 -> service.handleBlockKeyword(scanner);
                case 3 -> service.handleUnblockKeyword(scanner);
                case 4 -> service.handleToggleNewsVisibility(scanner);
                case 5 -> service.handleToggleCategoryVisibility(scanner);
                case 6 -> {
                    return false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong. Please try again.");
        }
        return true;
    }
}

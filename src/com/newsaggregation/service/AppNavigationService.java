package com.newsaggregation.service;

import java.util.Scanner;

import com.newsaggregation.client.*;
import com.newsaggregation.factory.ClientFactory;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class AppNavigationService {

    private final AuthClient authClient = new AuthClient();
    private final ExternalServerClient externalServerClient = new ExternalServerClient();
    ClientFactory factory = new ClientFactory();
    private final UserMenu userMenu = new UserMenu(factory);

    public void startApplication() {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            showMainMenu();
            int choice = InputUtil.readInt(scanner, "Choose an option: ");
            keepRunning = processMainMenuChoice(choice, scanner);
        }

        scanner.close();
    }

    private void showMainMenu() {
        System.out.println("\n=== Welcome to the News Aggregation System ===");
        System.out.println("1. Login");
        System.out.println("2. Sign up");
        System.out.println("3. Exit");
    }

    private boolean processMainMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1 -> {
                boolean loggedIn = authClient.login(scanner);
                if (loggedIn) routeToUserMenu(scanner);
            }
            case 2 -> authClient.signup(scanner);
            case 3 -> {
                System.out.println("Goodbye!");
                return false;
            }
            default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }
        return true;
    }

    private void routeToUserMenu(Scanner scanner) {
        if (UserSession.isAdmin()) {
            externalServerClient.showMenu(scanner);
        } else {
            userMenu.show(scanner);
        }
    }
}

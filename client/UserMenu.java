package com.newsaggregation.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.newsaggregation.factory.ClientFactory;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class UserMenu {
	private final HeadlinesClient headlinesClient;
    private final SavedArticleClient savedArticleClient;
    private final SearchClient searchClient;
    private final NotificationClient notificationClient;

    public UserMenu(ClientFactory factory) {
        this.headlinesClient = factory.getHeadlinesClient();
        this.savedArticleClient = factory.getSavedArticleClient();
        this.searchClient = factory.getSearchClient();
        this.notificationClient = factory.getNotificationClient();
    }

    public void show(Scanner sc) {
        boolean keepRunning = true;

        while (keepRunning) {
            printHeader();
            int choice = InputUtil.readInt(sc, "Choose an option: ");
            keepRunning = handleUserChoice(sc, choice);
        }
    }

    private void printHeader() {
        System.out.printf("\nWelcome to the News Application, %s!\n", UserSession.getUsername());
        System.out.println("Date: " + LocalDateTime.now().toLocalDate());
        System.out.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mma")));
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Headlines");
        System.out.println("2. Saved Articles");
        System.out.println("3. Search");
        System.out.println("4. Notifications");
        System.out.println("5. Logout");
    }

    private boolean handleUserChoice(Scanner sc, int choice) {
        try {
            switch (choice) {
                case 1 -> headlinesClient.show(sc);
                case 2 -> savedArticleClient.show(sc);
                case 3 -> searchClient.search(sc);
                case 4 -> notificationClient.showMenu(sc);
                case 5 -> {
                    UserSession.logout();
                    System.out.println("You have been logged out.");
                    return false;
                }
                default -> System.out.println("Invalid option.");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong while processing your request.");
        }
        return true;
    }
}

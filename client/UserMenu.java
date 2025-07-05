package com.newsaggregation.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.JSONException;

import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class UserMenu {
	public static void show(Scanner sc) throws JSONException {
        while (true) {
            System.out.printf("\nWelcome to the News Application, %s!\n", UserSession.getUsername());
            System.out.println("Date: " + LocalDateTime.now().toLocalDate());
            System.out.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("h:mma")));

            System.out.println("Please choose the options below:");
            System.out.println("1. Headlines");
            System.out.println("2. Saved Articles");
            System.out.println("3. Search");
            System.out.println("4. Notifications");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            int choice = InputUtil.readInt(sc);
            switch (choice) {
                case 1 -> HeadlinesClient.show(sc);
                case 2 -> SavedArticleClient.show(sc);
                case 3 -> SearchClient.search(sc);
                case 4 -> NotificationClient.showMenu(sc);
                case 5 -> {
                    UserSession.logout();
                    System.out.println("You have been logged out.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}

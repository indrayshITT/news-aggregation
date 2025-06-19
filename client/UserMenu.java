package com.newsaggregation.client;

import java.util.Scanner;

import com.newsaggregation.util.UserSession;

public class UserMenu {
	public static void show(Scanner sc) {
        while (true) {
            System.out.println("\n-- User Dashboard --");
            System.out.println("1. Headlines");
            System.out.println("2. Saved Articles");
            System.out.println("3. Search News");
            System.out.println("4. Notifications");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String option = sc.nextLine();
            switch (option) {
                case "1" -> System.out.println("[API] Show Headlines - coming soon");
                case "2" -> System.out.println("[API] View Saved Articles - coming soon");
                case "3" -> System.out.println("[API] Search News - coming soon");
                case "4" -> System.out.println("[API] View Notifications - coming soon");
                case "5" -> {
                    UserSession.logout();
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}

package com.newsaggregation.client;

import java.util.Scanner;

import com.newsaggregation.util.UserSession;

public class AdminMenu {
	public static void show(Scanner sc) {
        while (true) {
            System.out.println("\n-- Admin Dashboard --");
            System.out.println("1. View External Servers");
            System.out.println("2. View Server Details");
            System.out.println("3. Update Server API Key");
            System.out.println("4. Add News Category");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String option = sc.nextLine();
            switch (option) {
                case "1" -> System.out.println("[API] View External Servers - coming soon");
                case "2" -> System.out.println("[API] View Server Details - coming soon");
                case "3" -> System.out.println("[API] Update Server API Key - coming soon");
                case "4" -> System.out.println("[API] Add News Category - coming soon");
                case "5" -> {
                    UserSession.logout();
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}

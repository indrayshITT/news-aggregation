package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONException;

import com.newsaggregation.util.UserSession;

public class ClientMain {
	public static void main(String[] args) throws JSONException {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the News Aggregation System");
            System.out.println("1. Login");
            System.out.println("2. Sign up");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1 -> AuthClient.login(sc);
                case 2 -> AuthClient.signup(sc);
                case 3 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }

            if (UserSession.isLoggedIn()) {
                if (UserSession.isAdmin()) {
                    AdminMenu.show(sc);
                } else {
                    UserMenu.show(sc);
                }
            }
        }
    }
}

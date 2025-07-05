package com.newsaggregation.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {
	public static String readString(Scanner sc, String prompt) {
        System.out.print(prompt);
        if (sc.hasNextLine()) {
            return sc.nextLine().trim();
        }
        return "";
    }

    public static int readInt(Scanner sc) {
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    public static int readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    public static LocalDate readDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
            }
        }
    }

    public static boolean readBoolean(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no") || input.equals("n")) return false;
            System.out.println("Please answer with 'yes' or 'no'.");
        }
    }
}
